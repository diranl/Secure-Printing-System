package secureprinting.mixnet;

import secureprinting.Parser;
import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalCiphertext;
import civitas.crypto.ElGamalKeyPairShare;
import civitas.crypto.PETDecommitment;
import civitas.crypto.PETShare;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalMsgC;
import civitas.crypto.concrete.ElGamalParametersC;
import civitas.util.CivitasBigInteger;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;

import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * TranslationTable class stores the set of Messages used for mixing and printing
 * aka it stores the alphabet
 *
 * Performs randomization and permutation.
 *
 * <p>The following is an example to initialize a TranslationTable from the test sources
 *   CryptoFactoryC factory = CryptoFactoryC.singleton();
 *   ElGamalParametersC params = (ElGamalParametersC)factory.generateElGamalParameters();
 *   ElGamalKeyPairShare share = factory.generateKeyPairShare(params);
 *   TranslationTable initialTbl = TranslationTable.initTable(share);</p>
 * 
 */
public class TranslationTable implements Serializable {
  protected transient final ElGamalKeyPairShare share;
  protected List<Message> msgLst; // list of Message objects. NB: lists are unsynchronized, used vector if synchronization needed
  protected final int size;

  public static final int DEFAULT_PET_NUM = 3;

  public TranslationTable(TranslationTable copy) {
    this.size = copy.size;
    this.share = copy.share;
    this.msgLst = new ArrayList<Message>(copy.msgLst);
    Collections.copy(this.msgLst, copy.msgLst);
  }

  public TranslationTable(List<Message> msgLst, ElGamalKeyPairShare share) {
    this.msgLst = msgLst;
    this.size = msgLst.size();
    this.share = share;
  }

  /**
   * permute: Permutes TranslationTable according to given Permutation
   */
  public void permute(Permutation permutation) {
    // TODO: sanity check for size equality
    List<Integer> swapped = permutation.getList();
    List<Integer> initial = Permutation.range(permutation.size);

    for (int currPos=0, newPos; currPos<size; currPos++) {
      newPos = Permutation.findPositionOf(swapped.get(currPos), initial);
      Collections.swap(msgLst, currPos, newPos);
      Collections.swap(initial, currPos, newPos);
    }
  }

  /**
   * randomize: Randomizes TranslationTable according to given FactorTable
   */
  public void randomize(FactorTable factorTable) {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    CipherMessage msg;
    for (int i=0; i<size; i++) {
      msg = (CipherMessage)msgLst.get(i);
      msg.key = factory.elGamalReencrypt(share.pubKey, msg.key, factorTable.get(i,0));
      for (int j=0; j<msg.length-1; j++) {
        msg.set(j, factory.elGamalReencrypt(share.pubKey, msg.get(j), factorTable.get(i,j+1)));
      }
    }
  }

  /**
   * decryt: Decrypts encrypted table, CipherMessage becomes PlaintextMessage
   * @return TranslationTable
   */
  public TranslationTable decrypt() throws CryptoException {
    List<Message> decryptedLst = new ArrayList<Message>(size);
    for (int i=0; i<size; i++) {
      CipherMessage msg = (CipherMessage)msgLst.get(i);
      int key = cipherToKey(msg.key);
      BitSet translation = new BitSet(msg.length);
      for (int j=0; j<msg.length; j++) {
        if (cipherToBit(msg.get(j)) == 1) translation.set(j);
      }
      decryptedLst.add(new PlaintextMessage(key, translation, msg.rowSize, msg.colSize));
    }
    return new TranslationTable(decryptedLst, null);
  }

  private int cipherToBit(ElGamalCiphertext cipher) throws CryptoException {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalMsgC msg = (ElGamalMsgC)factory.elGamalDecrypt(share.privKey, cipher);
    return (msg.bigIntValue().equals(CivitasBigInteger.ONE) ? 0 : 1);
  }
  private int cipherToKey(ElGamalCiphertext cipher) throws CryptoException {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalMsgC msg = (ElGamalMsgC)factory.elGamalDecrypt(share.privKey, cipher);
    return msg.bigIntValue().intValue();
  }

  /**
   * extract: Perform Plaintext Equality Test to extract desired CipherMessage
   */
  public CipherMessage extract(ElGamalCiphertext cipher, int petNum) throws CryptoException {
    System.out.println("Performing PET with: " + petNum + " parties");
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)share.params;

    CipherMessage ret = null;
    for (Message msg : msgLst) {
      CipherMessage cipherMsg = (CipherMessage)msg;
      PETShare[] petShares = new PETShare[petNum];
      PETDecommitment[] petDecoms = new PETDecommitment[petNum];
      for (int i=0; i<petNum; i++) {
        petShares[i] = factory.constructPETShare(params, cipherMsg.key, cipher);
        petDecoms[i] = petShares[i].decommitment(params);
      }
      ElGamalCiphertext petResult = factory.combinePETShareDecommitments(petDecoms, params);

      if (factory.petResult(factory.elGamalDecrypt(share.privKey, petResult))) {
        ret = cipherMsg;
        break;
      }
    }
    return ret;
  }
  public CipherMessage extract(ElGamalCiphertext cipher) throws CryptoException {
    return this.extract(cipher, DEFAULT_PET_NUM);
  }
  
  
  public boolean equals(TranslationTable input) {
    if (this.size != input.size)           return false;
    if (!this.msgLst.equals(input.msgLst)) return false;
    return true;
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    out.writeObject(this);
    out.close();
    return baos.toByteArray();
  }

  /**
   * toJson: Serializes the object into a JSON equivalence using the GSON project by Google
   */
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  /**
   * Deserializes from a file into a TranslationTable object using GSON by Google
   *
   * FIXME: due to abstract class definitions like Message, ElGamalParameters 
   * (as opposed to CipherMessage and ElGamalParametersC)
   * the GSON deserialization will fail 
   * Solution: either use implementations as opposed to interfaces 
   * or give tags to GSON
   */
  public static TranslationTable fromString(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, TranslationTable.class);
  }
  
  /**
   * Serializes the object into a file using Java Serializable
   */
  public void toFile(String filename) {
    try {
      FileOutputStream fos = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(this);
      out.close();
      fos.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Deserializes from a file to an object using Java Serializable
   */
  public static TranslationTable fromFile(String filename) {
    TranslationTable table = null;
    try {
      FileInputStream fis = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(fis);
      table = (TranslationTable)in.readObject();
      System.out.println("table read");
      in.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return table;
  }

  /**
   * Prints TranslationTable, message by message
   */
  public void print() {
    for (Message msg : msgLst) {
      msg.print();
    }
  }

  @SuppressWarnings("Testing")
  public static TranslationTable initTable(ElGamalKeyPairShare share) throws FileNotFoundException {
    Message a = new CipherMessage(1, Parser.parseCsv("a.txt"), share);
    Message e = new CipherMessage(2, Parser.parseCsv("e.txt"), share);
    Message f = new CipherMessage(3, Parser.parseCsv("f.txt"), share);
    Message n = new CipherMessage(4, Parser.parseCsv("n.txt"), share);
    Message t = new CipherMessage(5, Parser.parseCsv("t.txt"), share);
    List<Message> lst = new ArrayList<Message>(4);
    lst.add(a);
    lst.add(e);
    lst.add(f);
    lst.add(n);
    lst.add(t);
    return (new TranslationTable(lst, share));
  }
  
  /**
   * Tests all features of TranslationTable
   */
  public static void main(String args[]) throws FileNotFoundException, CryptoException, NoSuchAlgorithmException, NoSuchProviderException {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)factory.generateElGamalParameters();
    ElGamalKeyPairShare share = factory.generateKeyPairShare(params);
    TranslationTable initialTbl = TranslationTable.initTable(share);
    System.out.println("Initial table:");
    initialTbl.print();
    System.out.println("=======================================================");
    // Perform randomization
    FactorTable factorTable = new FactorTable(initialTbl);
    System.out.println("Factor table:");
    System.out.println("...serializing factor table");
    factorTable.toFile("factor");
    System.out.println("...reading persisted factor table");
    FactorTable factorFromFile = FactorTable.fromFile("factor");
    System.out.println("..randomizing");
    initialTbl.randomize(factorFromFile);

    // Perform permutation
    Permutation permutation = new Permutation(initialTbl.size);
    System.out.print("Permutation: ");
    permutation.print();
    permutation.toFile("perm");
    // Test Permuation serializable
    System.out.println("...serializing permutation");
    Permutation permFromFile = Permutation.fromFile("perm");
    System.out.println("...reading persisted permutation");
    System.out.print("..read: ");
    permFromFile.print();
    initialTbl.permute(permFromFile);
    System.out.println("=======================================================");

    // Test serializable
    initialTbl.toFile("test.txt");
    TranslationTable tableFromFile = fromFile("test.txt");

    // Test decryption
    TranslationTable decryptedTable = tableFromFile.decrypt();
    decryptedTable.print();
  }
}

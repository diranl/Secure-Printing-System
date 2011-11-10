package civitas.mixnet;

import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalKeyPairShare;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalCiphertextC;
import civitas.crypto.concrete.ElGamalMsgC;
import civitas.crypto.concrete.ElGamalParametersC;
import civitas.util.CivitasBigInteger;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mixnet {
  public final int serverNum; 
  public TranslationTable mixedTable;

  public Mixnet(int serverNum, ElGamalKeyPairShare share) {
    this.serverNum = serverNum;
  }

  public void execute(ElGamalKeyPairShare share) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException {
    // Performs the mixing, server by server in a serial fashion, as described by part 1. of Sub-protocol 1.1
    // Produces the final mixed table
    List<Server> serverLst = new ArrayList<Server>(serverNum);
    TranslationTable initialTbl = TranslationTable.initTable(share);
    //TODO(crucial!): check that deep copies are performed rather than shallow
    Server newSvr = null;
    for (int idx=0; idx<serverNum; idx++) {
      System.out.println("Processing server: " + idx);
      if (idx == 0) newSvr = new Server(initialTbl);
      else newSvr = new Server(serverLst.get(idx-1));
      serverLst.add(newSvr);
    }
    this.mixedTable = newSvr.translation;
  }

  protected class Server {
    protected final TranslationTable translation;
    protected final FactorTable factorTable;
    protected final Permutation permutation;

    // 1. Generates random factors (for ElGamal reencryption)
    // 2. Generate permutations
    // 3. Commit to random factors
    // 4. Commit to permutations
    // 5. Perform randomize() and permute() methods for TranslationTable
    protected Server(TranslationTable inputTable) throws NoSuchAlgorithmException, NoSuchProviderException {
      this.translation = new TranslationTable(inputTable);
      this.factorTable = new FactorTable(translation);
      this.permutation = new Permutation(translation.size);
      mix();
    }
    protected Server(Server inputSvr) throws NoSuchAlgorithmException, NoSuchProviderException {
      this(inputSvr.translation);
    }
    private void mix() {
      System.out.println("...mixing translation table");
      translation.randomize(factorTable);
      System.out.println("..randomized");
      translation.permute(permutation);
      System.out.print("..permuted with: ");
      permutation.print();
    }
  }

  public static void main(String args[]) throws CryptoException {
    // 1. Initialize Mixnet with desired amount of servers
    // 2. Perform the serial/chain computation to yield the mixed result
    // 3. TODO (change): given an input, encrypt, perform PET to yield a CipherMessage
    // 4. Perform the multi-party printing scheme
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)factory.generateElGamalParameters();
    ElGamalKeyPairShare share = factory.generateKeyPairShare(params);

    System.out.println("================MIXNET=======================================================");
    Mixnet mixnet = new Mixnet(3, share);
    try {
      mixnet.execute(share);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    } catch (NoSuchProviderException ex) {
      ex.printStackTrace();
    }

    /* TEST: make sure mixing was done correctly
    TranslationTable decryptedTbl = mixnet.mixedTable.decrypt();
    decryptedTbl.print();
    */

    // Pick a random position in the table as the plaintext; this position maps to a letter 
    // in the alphabet
    System.out.println("\n================PET=======================================================");
    int selection = new Random().nextInt(5);
    ElGamalMsgC plaintxt = new ElGamalMsgC(CivitasBigInteger.valueOf(selection));
    ElGamalCiphertextC cipher = (ElGamalCiphertextC)factory.elGamalEncrypt(share.pubKey, plaintxt);
    System.out.println("Message to retrieve: " + selection);

    // Perform PET 
    TranslationTable mixedTbl = mixnet.mixedTable;
    CipherMessage cipherMsg = mixedTbl.extract(cipher);
    System.out.println("Retrieved message:");
    cipherMsg.decryptPrint(share.privKey);

    //TODO: check validity via Shadow Mixes

    // Visual Crypto
    System.out.println("\n================VISUAL CRYPTO=======================================================");
    Printing printing = new Printing(2, cipherMsg, share.pubKey);
    try {
      printing.execute();
      printing.writeFinalization(share.privKey);
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    } catch (NoSuchProviderException ex) {
      ex.printStackTrace();
    }
  }
}


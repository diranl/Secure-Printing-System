package civitas.mixnet;

import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalKeyPairShare;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalCiphertextC;
import civitas.crypto.concrete.ElGamalMsgC;
import civitas.crypto.concrete.ElGamalParametersC;
import civitas.mixnet.Printing.Printer;
import civitas.util.CivitasBigInteger;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mixnet {
  public final int serverNum; 
  public TranslationTable mixedTable;
  public List<Server> serverLst;

  public static int CHALLENGE_NUM = 3;

  public Mixnet(int serverNum, ElGamalKeyPairShare share) {
    this.serverNum = serverNum;
  }

  public void execute(ElGamalKeyPairShare share) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException {
    // Performs the mixing, server by server in a serial fashion, as described by part 1. of Sub-protocol 1.1
    // Produces the final mixed table
    this.serverLst = new ArrayList<Server>(serverNum);
    TranslationTable initialTbl = TranslationTable.initTable(share);
    //TODO(crucial!): check that deep copies are performed rather than shallow
    Server newSvr = null;
    for (int idx=0; idx<serverNum; idx++) {
      System.out.println("Processing server: " + idx);
      if (idx == 0) newSvr = new Server(initialTbl);
      else newSvr = new Server(serverLst.get(idx-1));
      serverLst.add(newSvr);
    }
    this.mixedTable = newSvr.outputTbl;
  }

  public void validate() throws NoSuchAlgorithmException, NoSuchProviderException {
    for (Server server : serverLst) {
      Random rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
      for (int idx=0; idx<CHALLENGE_NUM; idx++) {
        server.challenge();
        ChallengeProof proof = server.reveal(rand.nextInt(2)==0 ? true : false);
        if (!verifyProof(proof)) throw new RuntimeException("did not verify");
      }
    }
  }

  public static boolean verifyProof(ChallengeProof proof) {
    TranslationTable transformTbl = new TranslationTable(proof.inputTbl);
    transformTbl.randomize(proof.factorTbl);
    transformTbl.permute(proof.permutation);
    return transformTbl.equals(proof.control);
  }

  protected class Server {
    protected final TranslationTable inputTbl;
    protected final TranslationTable outputTbl;
    protected final FactorTable factorTable;
    protected final Permutation permutation;
    protected Challenge challenge;

    // 1. Generates random factors (for ElGamal reencryption)
    // 2. Generate permutations
    // 3. Commit to random factors
    // 4. Commit to permutations
    // 5. Perform randomize() and permute() methods for TranslationTable
    protected Server(TranslationTable inputTbl) throws NoSuchAlgorithmException, NoSuchProviderException {
      this.inputTbl = inputTbl;
      this.outputTbl = new TranslationTable(inputTbl);
      this.factorTable = new FactorTable(inputTbl);
      this.permutation = new Permutation(inputTbl.size);
      mix();
    }
    protected Server(Server inputSvr) throws NoSuchAlgorithmException, NoSuchProviderException {
      this(inputSvr.outputTbl);
    }
    private void mix() {
      System.out.println("...mixing translation table");
      outputTbl.randomize(factorTable);
      System.out.println("..randomized");
      outputTbl.permute(permutation);
      System.out.print("..permuted with: ");
      permutation.print();
    }

    protected void challenge() {
      try {
        //TODO: post commitments to random and inverting mixes
        this.challenge = new Challenge(inputTbl, outputTbl, factorTable, permutation);
      } catch (NoSuchAlgorithmException ex) {
        ex.printStackTrace();
      } catch (NoSuchProviderException ex) {
        ex.printStackTrace();
      }
    }

    protected ChallengeProof reveal(boolean isHead) {
      return challenge.reveal(isHead);
    }
  }

  private class Challenge {
    private final FactorTable randomTbl;
    private final FactorTable invTbl;
    private final Permutation randomPrm;
    private final Permutation invPrm;
    private final TranslationTable inputTbl;
    private final TranslationTable outputTbl;
    private final TranslationTable midTbl;
    protected transient boolean challenged;

    private Challenge(TranslationTable inputTbl, TranslationTable outputTbl, FactorTable factorTbl, Permutation permutation) throws NoSuchAlgorithmException, NoSuchProviderException {
      this.randomTbl = new FactorTable(inputTbl);
      this.randomPrm = new Permutation(permutation.size);
      this.invTbl = factorTbl.invert(randomTbl, (ElGamalParametersC)inputTbl.share.params);
      this.invPrm = permutation.invert(randomPrm);
      this.inputTbl = inputTbl;
      this.outputTbl = outputTbl;

      //TODO: commitments

      this.midTbl = new TranslationTable(inputTbl);
      midTbl.randomize(randomTbl);
      midTbl.permute(randomPrm);
      this.challenged = false;
    }

    /**
     * Commits to a FactorTable and a Permutation
     * <p>NOTE: the commitment convention used is hash(permutation||random factors)</p>
     * @param type either RANDOM or INVERSION 
     * @return hash of commited values
     */
    private String commit(int type) {
      String factor, prm; 
      if (type == RANDOM) {
        factor = randomTbl.toString();
        prm = randomPrm.toString();
      } else {
        factor = invTbl.toString();
        prm = invPrm.toString();
      }
      return factory.hash(factor + prm);
    }

    private ChallengeProof reveal(boolean isHead) {
      if (challenged) { throw new RuntimeException("ABORT: attempting to reveal the same challenge more than once"); }
      this.challenged = true;
      
      if (isHead) return new ChallengeProof(randomTbl, randomPrm, inputTbl, midTbl);
      else        return new ChallengeProof(invTbl, invPrm, midTbl, outputTbl);
    }
  }

  protected class ChallengeProof {
    public final FactorTable factorTbl;
    public final Permutation permutation;
    public final TranslationTable inputTbl;
    public final TranslationTable control;

    protected ChallengeProof(FactorTable factorTbl, Permutation permutation, TranslationTable inputTbl, TranslationTable control) {
      this.factorTbl = factorTbl;
      this.permutation = permutation;
      this.inputTbl = inputTbl;
      this.control = control;
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

    System.out.println("Shadow mix validation:");
    try {
      mixnet.validate();
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    } catch (NoSuchProviderException ex) {
      ex.printStackTrace();
    }

    // Perform PET 
    TranslationTable mixedTbl = mixnet.mixedTable;
    CipherMessage cipherMsg = mixedTbl.extract(cipher);
    System.out.println("Retrieved message:");
    cipherMsg.decryptPrint(share.privKey);


    // Visual Crypto
    System.out.println("\n================VISUAL CRYPTO=======================================================");
    Printing printing = new Printing(3, cipherMsg, share.pubKey);
    try {
      printing.execute();
      printing.writeFinalization(share.privKey);
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    } catch (NoSuchProviderException ex) {
      ex.printStackTrace();
    }
    // printout of visual crypto
    for (Printer printer : printing.printerLst) {
      printer.print(share.privKey);
    }
    System.out.println("finalization layer:");
    printing.finalizedMsg.decryptPrint(share.privKey);
    // perform xor on plaintexts and compare with input msg
    Matrix result = new Matrix(cipherMsg.rowSize, cipherMsg.colSize);
    for (Printer printer : printing.printerLst) {
      result.xor(printer.share, true);
    }
    result.xor(printing.finalizedMsg.decryptToMatrix(share.privKey), true);
    System.out.println("\nResulting matrix:");
    result.print();
  }
}


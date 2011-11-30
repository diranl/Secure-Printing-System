package secureprinting.mixnet;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Server class receives a TranslationTable, mixes it, and provides proof of a valid mix
 */ 
final class Server {
  protected final TranslationTable inputTbl;
  protected final TranslationTable outputTbl;
  protected final FactorTable factorTable;
  protected final Permutation permutation;
  public Challenge challenge;

  /**
   * Server class instantiation:
   * 1. generates random factors - FactorTable instance 
   * 2. generates permutation - Permutaion instance
   * 3. randomizes and permutes TranslationTable
   */
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

  /**
   * Creates a Shadow Mix challenge
   * <Note>The commitments in the challenges have public value</Note>
   */
  protected void challenge() throws NoSuchAlgorithmException, NoSuchProviderException {
    System.out.println("...creating challenge");
    this.challenge = new Challenge(inputTbl, outputTbl, factorTable, permutation);
  }

  protected ChallengeProof reveal(boolean isHead) {
    return challenge.reveal(isHead);
  }
}

package secureprinting.mixnet;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

final class Server {
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
      System.out.println("...creating challenge");
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

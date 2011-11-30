package secureprinting.mixnet;

import secureprinting.Commitment;

/**
 * ChallengeProof class complimentary to Challenge
 * <Note>All variables are public as needed for proof of knowledge</Note>
 */
final class ChallengeProof {
  public final FactorTable factorTbl;
  public final Permutation permutation;
  public final TranslationTable inputTbl;
  public final TranslationTable control;
  public final Commitment commit;

  protected ChallengeProof(FactorTable factorTbl, Permutation permutation, TranslationTable inputTbl, TranslationTable control, Commitment commit) {
    this.factorTbl = factorTbl;
    this.permutation = permutation;
    this.inputTbl = inputTbl;
    this.control = control;
    this.commit = commit;
  }

  /**
   * verifyProof: Randomize and permute inputTbl, verify result against control Table
   */
  public static boolean verifyProof(ChallengeProof proof) {
    if (!Commitment.verifyCommit(proof.factorTbl, proof.permutation, proof.commit)) throw new RuntimeException("Invalid decommitment to Challenge");
    System.out.println("...commitment verified");
    System.out.println("...verifying proof");
    TranslationTable transformTbl = new TranslationTable(proof.inputTbl);
    transformTbl.randomize(proof.factorTbl);
    transformTbl.permute(proof.permutation);
    return transformTbl.equals(proof.control);
  }
}

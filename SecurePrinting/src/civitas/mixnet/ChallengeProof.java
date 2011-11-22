package civitas.mixnet;

final class ChallengeProof {
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

package civitas.mixnet;

import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalParametersC;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

final class Challenge {
  private final FactorTable randomTbl;
  private final FactorTable invTbl;
  private final Permutation randomPrm;
  private final Permutation invPrm;
  private final TranslationTable inputTbl;
  private final TranslationTable outputTbl;
  private final TranslationTable midTbl;
  protected transient boolean challenged;

  public final Commitment commitToRnd;
  public final Commitment commitToInv;
  
  public static final int COMMIT_RANDOM = 0;
  public static final int COMMIT_INVERSE = 1;

  protected Challenge(TranslationTable inputTbl, TranslationTable outputTbl, FactorTable factorTbl, Permutation permutation) throws NoSuchAlgorithmException, NoSuchProviderException {
    this.randomTbl = new FactorTable(inputTbl);
    this.randomPrm = new Permutation(permutation.size);
    this.invTbl = factorTbl.invert(randomTbl, (ElGamalParametersC)inputTbl.share.params);
    this.invPrm = permutation.invert(randomPrm);
    this.inputTbl = inputTbl;
    this.outputTbl = outputTbl;

    this.commitToRnd = new Commitment(randomTbl, randomPrm);
    this.commitToInv = new Commitment(invTbl, invPrm);

    this.midTbl = new TranslationTable(inputTbl);
    midTbl.randomize(randomTbl);
    midTbl.permute(randomPrm);
    this.challenged = false;
  }

  protected ChallengeProof reveal(boolean isHead) {
    if (challenged) { throw new RuntimeException("ABORT: attempting to reveal the same challenge more than once"); }
    this.challenged = true;
    
    if (isHead) return new ChallengeProof(randomTbl, randomPrm, inputTbl, midTbl, commitToRnd);
    else        return new ChallengeProof(invTbl, invPrm, midTbl, outputTbl, commitToInv);
  }
}


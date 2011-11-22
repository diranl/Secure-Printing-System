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
  
  public static final int COMMIT_RANDOM = 0;
  public static final int COMMIT_INVERSE = 1;

  protected Challenge(TranslationTable inputTbl, TranslationTable outputTbl, FactorTable factorTbl, Permutation permutation) throws NoSuchAlgorithmException, NoSuchProviderException {
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
  protected String commit(int type) {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    String factor, prm; 
    if (type == COMMIT_RANDOM) {
      factor = randomTbl.toString();
      prm = randomPrm.toString();
    } else {
      factor = invTbl.toString();
      prm = invPrm.toString();
    }
    return factory.hash(factor + prm);
  }

  protected ChallengeProof reveal(boolean isHead) {
    if (challenged) { throw new RuntimeException("ABORT: attempting to reveal the same challenge more than once"); }
    this.challenged = true;
    
    if (isHead) return new ChallengeProof(randomTbl, randomPrm, inputTbl, midTbl);
    else        return new ChallengeProof(invTbl, invPrm, midTbl, outputTbl);
  }
}


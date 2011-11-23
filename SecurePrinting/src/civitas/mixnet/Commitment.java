package civitas.mixnet;

import civitas.crypto.concrete.CryptoFactoryC;
import civitas.visual.Matrix;

public final class Commitment {
  public final String value;

  /**
   * Commits to a FactorTable and a Permutation
   * <p>NOTE: the commitment convention used is hash(permutation||random factors)</p>
   * @param type either RANDOM or INVERSION 
   * @return hash of commited values
   */
  public Commitment(FactorTable factorTbl, Permutation prm) {
   CryptoFactoryC factory = CryptoFactoryC.singleton();
   this.value = factory.hash(factorTbl.toJson() + prm.toJson());
  }

  public Commitment(Matrix matrix) {
   CryptoFactoryC factory = CryptoFactoryC.singleton();
   this.value = factory.hash(matrix.toJson());
  }

  public static boolean verifyCommit(FactorTable factorTbl, Permutation prm, Commitment commit) {
   CryptoFactoryC factory = CryptoFactoryC.singleton();
   String hash = factory.hash(factorTbl.toJson() + prm.toJson());
   return commit.value.equals(hash);
  }
}

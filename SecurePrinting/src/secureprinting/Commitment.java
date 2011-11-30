package secureprinting;

import civitas.crypto.concrete.CryptoFactoryC;
import secureprinting.mixnet.FactorTable;
import secureprinting.mixnet.Permutation;

public final class Commitment {
  public final String value;
  private static final CryptoFactoryC factory = CryptoFactoryC.singleton();

  public Commitment(FactorTable factorTbl, Permutation prm) {
    this.value = factory.hash(factorTbl.toJson() + prm.toJson());
  }

  public Commitment(Matrix matrix) {
    this.value = factory.hash(matrix.toJson());
  }

  public static boolean verifyCommit(FactorTable factorTbl, Permutation prm, Commitment commit) {
    String hash = factory.hash(factorTbl.toJson() + prm.toJson());
    return commit.value.equals(hash);
  }

  public static boolean verifyCommit(Matrix m, Commitment commit) {
    String hash = factory.hash(m.toJson());
    return commit.value.equals(hash);
  }
}

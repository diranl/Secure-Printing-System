package secureprinting.visualcrypto;

import secureprinting.Matrix;
import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import secureprinting.mixnet.CipherMessage;
import secureprinting.Commitment;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Printer class creates a random share, xor it with a given cipher, commits his share
 */
final class Printer {
  private final Matrix share;
  public final CipherMessage cipher;
  public final Commitment commitToShare;
  public final String Id;

  public static final String SHARE_PREFIX = "share-";

  protected Printer(CipherMessage cipher, ElGamalPublicKey pubKey, String Id) throws NoSuchAlgorithmException, NoSuchProviderException {
    this.share = Matrix.random(cipher.rowSize, cipher.colSize);
    this.cipher = cipher.xor(this.share, pubKey);
    this.commitToShare = new Commitment(share);
    this.Id = Id;
  }
  protected Printer(Printer inputPrinter, ElGamalPublicKey pubKey, String Id) throws NoSuchAlgorithmException, NoSuchProviderException {
    this(inputPrinter.cipher, pubKey, Id);
  }

  protected Matrix decommit() {
    return this.share;
  }

  protected void write(BasisMatrix basis, int idx) {
    Matrix augmented = share.augment(basis, idx);
    String filename = SHARE_PREFIX + Id + "-" + idx;
    augmented.write(filename);
    System.out.println("..share created to: " + filename + ".bmp");
  }

  protected void print(ElGamalPrivateKey privKey) {
    try {
      System.out.println("Random share:");
      share.print();
      System.out.println("Result from Homomorphic XOR:");
      cipher.decryptPrint(privKey);
    } catch (CryptoException ex) {
      ex.printStackTrace();
    }
  }
}

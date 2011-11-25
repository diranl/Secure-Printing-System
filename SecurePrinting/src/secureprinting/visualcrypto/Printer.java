package secureprinting.visualcrypto;

import secureprinting.Matrix;
import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import secureprinting.mixnet.CipherMessage;
import secureprinting.Commitment;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

final class Printer {
  private final Matrix share;
  public final CipherMessage cipher;
  public final Commitment commitToShare;
  public final String ID;

  protected Printer(CipherMessage cipher, ElGamalPublicKey pubKey, String ID) throws NoSuchAlgorithmException, NoSuchProviderException {
    this.share = Matrix.random(cipher.rowSize, cipher.colSize);
    this.cipher = cipher.xor(this.share, pubKey);
    this.commitToShare = new Commitment(share);
    this.ID = ID;
  }
  protected Printer(Printer inputPrinter, ElGamalPublicKey pubKey, String ID) throws NoSuchAlgorithmException, NoSuchProviderException {
    this(inputPrinter.cipher, pubKey, ID);
  }

  protected Matrix decommit() {
    return this.share;
  }

  protected void write(BasisMatrix basis, int idx) {
    Matrix augmented = share.augment(basis, idx);
    String filename = "share-" + ID + "-" + idx;
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

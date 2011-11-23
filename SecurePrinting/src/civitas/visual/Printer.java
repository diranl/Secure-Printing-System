package civitas.visual;

import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import civitas.mixnet.CipherMessage;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public final class Printer {
  protected final Matrix share;
  public final CipherMessage cipher;
  public final Commitment commitToShare;

  public Printer(CipherMessage cipher, ElGamalPublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException {
    this.share = Matrix.random(cipher.rowSize, cipher.colSize);
    this.cipher = cipher.xor(this.share, pubKey);
    this.commitToShare = new Commitment(share);
  }
  public Printer(Printer inputPrinter, ElGamalPublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException {
    this(inputPrinter.cipher, pubKey);
  }

  protected void write(BasisMatrix basis, int idx) {
    Matrix augmented = share.augment(basis, idx);
    augmented.write("share-"+idx);
    System.out.println("..share created to: share-" + idx + ".bmp");
  }

  @SuppressWarnings("For testing purposes only")
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

package civitas.mixnet;

//TODO: refactor out of the mixnet package

import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

public class Printing {

  public final int printerNum;
  public final CipherMessage initialMsg;
  public final ElGamalPublicKey pubKey;
  public final BasisMatrix basis;
  public CipherMessage finalizedMsg;

  public List<Printer> printerLst; //FIXME: currently only useful for testing purposes

  public Printing(int printerNum, CipherMessage cipher, ElGamalPublicKey pubKey) {
    this.printerNum = printerNum;
    this.initialMsg = cipher;
    this.pubKey = pubKey;
    this.basis = new BasisMatrix(printerNum+1/*Extra party for finalization*/, BasisMatrix.DEFAULT);
  }
  
  public void execute() throws NoSuchAlgorithmException, NoSuchProviderException {
    printerLst = new ArrayList<Printer>(printerNum);
    Printer newPrinter = null;
    for (int idx=0; idx<printerNum; idx++) {
      System.out.println("...initializing printer: " + idx);
      if (idx == 0) newPrinter = new Printer(initialMsg, pubKey);
      else newPrinter = new Printer(printerLst.get(idx-1), pubKey);
      printerLst.add(newPrinter);
      newPrinter.write(basis, idx);
    }
    this.finalizedMsg = newPrinter.cipher;
  }

  public void writeFinalization(ElGamalPrivateKey privKey) {
    System.out.println("...decrypting finalization layer");
    Matrix finalization = finalizedMsg.decryptToMatrix(privKey);
    Matrix augmented = finalization.augment(basis, printerNum);
    augmented.write("finalization");
    System.out.println("..finalization layer written to: finalization.bmp");
  }

  protected class Printer {
    protected final Matrix share;
    public final CipherMessage cipher;

    protected Printer(CipherMessage cipher, ElGamalPublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException {
      this.share = Matrix.random(cipher.rowSize, cipher.colSize);
      this.cipher = cipher.xor(this.share, pubKey);
    }
    protected Printer(Printer inputPrinter, ElGamalPublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException {
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

}

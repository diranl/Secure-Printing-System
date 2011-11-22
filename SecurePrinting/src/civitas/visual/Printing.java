package civitas.visual;

//TODO: refactor out of the mixnet package

import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import civitas.crypto.concrete.ElGamalPrivateKeyC;
import civitas.mixnet.CipherMessage;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Printing {
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
  
  public void execute(ElGamalPrivateKey privKey /*FIXME: privKey is used for debugging only*/) throws NoSuchAlgorithmException, NoSuchProviderException {
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

    //FIXME: Debugging print statements
    int rowSize = 0, colSize = 0;
    for (Printer printer : this.printerLst) {
      printer.print(privKey);
      if (rowSize == 0 || colSize == 0) {
        rowSize = printer.share.rowSize;
        colSize = printer.share.colSize;
      }
    }
    System.out.println("finalization layer:");
    try {
      this.finalizedMsg.decryptPrint(privKey);
    } catch (CryptoException ex) {
      ex.printStackTrace();
    }
    // perform xor on plaintexts and compare with input msg
    Matrix result = new Matrix(rowSize, colSize);
    for (Printer printer : this.printerLst) {
      result.xor(printer.share, true);
    }
    result.xor(this.finalizedMsg.decryptToMatrix(privKey), true);
    System.out.println("\nResulting matrix:");
    result.print();
  }

  public void writeFinalization(ElGamalPrivateKey privKey) {
    System.out.println("...decrypting finalization layer");
    Matrix finalization = finalizedMsg.decryptToMatrix(privKey);
    Matrix augmented = finalization.augment(basis, printerNum);
    augmented.write("finalization");
    System.out.println("..finalization layer written to: finalization.bmp");
  }

}

package secureprinting.visualcrypto;

import java.io.IOException;
import secureprinting.Matrix;
import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import civitas.crypto.concrete.ElGamalPrivateKeyC;
import secureprinting.mixnet.CipherMessage;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import secureprinting.Controller;
import secureprinting.Commitment;

final class PrinterDriver {
  public final int printerNum;
  public final CipherMessage initialCipher;
  public final ElGamalPublicKey pubKey;
  public final BasisMatrix basis;
  public final String Id;

  public CipherMessage finalizedMsg;
  public List<Printer> printerLst; 
  public boolean revealed;

  public final static int DEFAULT_PRINTER_NUM = 3;
  public final static String FINALIZATION_PREFIX = "finalization-";
  public final static String OVERLAY_PREFIX = "overlay-";

  public PrinterDriver(int printerNum, CipherMessage cipher, ElGamalPublicKey pubKey) {
    this.printerNum = printerNum;
    this.initialCipher = cipher;
    this.pubKey = pubKey;
    this.basis = new BasisMatrix(printerNum+1/*Require an extra party for finalization*/);
    this.Id = Controller.nextId();
  }
  public PrinterDriver(CipherMessage cipher, ElGamalPublicKey pubKey) {
    this(DEFAULT_PRINTER_NUM, cipher, pubKey);
  }
  
  public void execute() throws NoSuchAlgorithmException, NoSuchProviderException {
    printerLst = new ArrayList<Printer>(printerNum);
    Printer newPrinter = null;
    for (int idx=0; idx<printerNum; idx++) {
      System.out.println("Initializing printer: " + idx);
      if (idx == 0) newPrinter = new Printer(initialCipher, pubKey, Id);
      else newPrinter = new Printer(printerLst.get(idx-1), pubKey, Id);
      printerLst.add(newPrinter);
      newPrinter.write(basis, idx);
    }
    this.finalizedMsg = newPrinter.cipher;
  }

  public void reveal() {
    System.out.println("..revealing: " + Id);
    for (Printer printer : printerLst) {
      Matrix share = printer.decommit();
      if (!Commitment.verifyCommit(share, printer.commitToShare)) throw new RuntimeException("Invalid decommitment to Printer.share");
      //TODO: decommitment to images 
    }
    this.revealed = true;
  }

  public void writeFinalization(ElGamalPrivateKey privKey) {
    if (revealed) throw new RuntimeException("Cannot finalize a Printer which has been revealed");
    System.out.println("...decrypting finalization layer");
    Matrix finalization = finalizedMsg.decryptToMatrix(privKey);
    Matrix augmented = finalization.augment(basis, printerNum);
    String filename = FINALIZATION_PREFIX + Id;
    augmented.write(filename);
    System.out.println("..finalization layer written to: " + filename + Bitmap.BMP_SUFFIX);
  }

  public void writeOverlay() throws IOException {
    System.out.println("Performing overlay:");
    System.out.println("...reading finalization: " + FINALIZATION_PREFIX + Id + Bitmap.BMP_SUFFIX);
    Matrix finalization = Bitmap.read(FINALIZATION_PREFIX + Id + Bitmap.BMP_SUFFIX);
    for (int idx=0; idx<printerNum; idx++) {
      System.out.println("...reading share from: " + Printer.SHARE_PREFIX + Id + "-" + idx + Bitmap.BMP_SUFFIX);
      Matrix share = Bitmap.read(Printer.SHARE_PREFIX + Id + "-" + idx + Bitmap.BMP_SUFFIX);
      finalization.or(share, Matrix.OVERWRITE);
    }
    System.out.println("..overlay written to: " + OVERLAY_PREFIX + Id + Bitmap.BMP_SUFFIX);
    finalization.write(OVERLAY_PREFIX + Id + Bitmap.BMP_SUFFIX);
  }
}

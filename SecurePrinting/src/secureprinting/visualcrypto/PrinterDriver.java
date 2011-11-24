package secureprinting.visualcrypto;

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
  public final String ID;
  public CipherMessage finalizedMsg;
  public List<Printer> printerLst; 
  public boolean revealed;
  public final static int DEFAULT_PRINTER_NUM = 3;

  public PrinterDriver(int printerNum, CipherMessage cipher, ElGamalPublicKey pubKey) {
    this.printerNum = printerNum;
    this.initialCipher = cipher;
    this.pubKey = pubKey;
    this.basis = new BasisMatrix(printerNum+1/*Require an extra party for finalization*/);
    this.ID = Controller.nextId();
  }
  public PrinterDriver(CipherMessage cipher, ElGamalPublicKey pubKey) {
    this(DEFAULT_PRINTER_NUM, cipher, pubKey);
  }
  
  public void execute() throws NoSuchAlgorithmException, NoSuchProviderException {
    printerLst = new ArrayList<Printer>(printerNum);
    Printer newPrinter = null;
    for (int idx=0; idx<printerNum; idx++) {
      System.out.println("...initializing printer: " + idx);
      if (idx == 0) newPrinter = new Printer(initialCipher, pubKey, ID);
      else newPrinter = new Printer(printerLst.get(idx-1), pubKey, ID);
      printerLst.add(newPrinter);
      newPrinter.write(basis, idx);
    }
    this.finalizedMsg = newPrinter.cipher;
  }

  public void reveal() {
    System.out.println("..revealing: " + ID);
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
    String filename = "finalization-" + ID;
    augmented.write(filename);
    System.out.println("..finalization layer written to: " + filename + ".bmp");
  }
}

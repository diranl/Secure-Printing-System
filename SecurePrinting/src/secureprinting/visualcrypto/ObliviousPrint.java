package secureprinting.visualcrypto;

import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import secureprinting.mixnet.CipherMessage;

public final class ObliviousPrint {
  public final int driverNum;
  public final CipherMessage initialCipher;
  public final ElGamalPublicKey pubKey;
  private final List<PrinterDriver> driverLst;
  private int choose;

  public final static int DEFAULT_DRIVER_NUM = 3;
  
  public ObliviousPrint(int driverNum, CipherMessage cipher, ElGamalPublicKey pubKey) {
    this.driverNum = driverNum;
    this.initialCipher = cipher;
    this.pubKey = pubKey;
    this.driverLst = new ArrayList<PrinterDriver>(driverNum);
  }
  public ObliviousPrint(CipherMessage cipher, ElGamalPublicKey pubKey) {
    this(DEFAULT_DRIVER_NUM, cipher, pubKey);
  }

  public void execute() throws NoSuchAlgorithmException, NoSuchProviderException {
    for (int idx=0; idx<driverNum; idx++) {
      PrinterDriver driver = new PrinterDriver(2, initialCipher, pubKey);
      driver.execute();
      driverLst.add(driver);
    }
  }
  
  public void cutAndChoose() {
    System.out.println("\nCut and choose:");
    SecureRandom rnd = new SecureRandom();
    this.choose = rnd.nextInt(driverNum);
    for (int idx=0; idx<driverNum; idx++) {
      if (idx == choose) continue;
      driverLst.get(idx).reveal();
    }
  }

  public void finalization(ElGamalPrivateKey privKey) {
    PrinterDriver driver = driverLst.get(choose);
    driver.writeFinalization(privKey);
    try {
      driver.writeOverlay(); /*NOTE: overlaying shares used for debugging purposes only*/
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}

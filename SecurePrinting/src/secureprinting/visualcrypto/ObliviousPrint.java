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

/**
 * ObliviousPrint class coordinates the visual crypto and the printing of a given secret
 */
public final class ObliviousPrint {
  public final int driverNum;
  public final CipherMessage initialCipher;
  public final ElGamalPublicKey pubKey;
  private final List<PrinterDriver> driverLst;
  private int selection;

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

  /**
   * execute: Instantiates various copies of PrinterDriver and calls their execution method
   * <Note>Corresponds to Part 1 of Sub-Protocol 1.2 of Oblivious Printing</Note>
   */
  public void execute() throws NoSuchAlgorithmException, NoSuchProviderException {
    for (int idx=0; idx<driverNum; idx++) {
      PrinterDriver driver = new PrinterDriver(2, initialCipher, pubKey);
      driver.execute();
      driverLst.add(driver);
    }
  }
  
  /**
   * cutAndChoose: audit all PrinterDriver's but one random selection
   * <Note>Corresponds to Part 2 of Sub-Protocol 1.2 of Oblivious Printing</Note>
   */
  public void cutAndChoose() {
    System.out.println("\nCut and choose:");
    SecureRandom rnd = new SecureRandom();
    this.selection = rnd.nextInt(driverNum);
    for (int idx=0; idx<driverNum; idx++) {
      if (idx == selection) continue;
      driverLst.get(idx).reveal();
    }
  }

  /**
   * finalization: decryts the unaudited selection, prints the plaintext
   * <Note>Corresponds to Part 3 of Sub-Protocol 1.2 of Oblivious Printing</Note>
   */
  public void finalization(ElGamalPrivateKey privKey) {
    PrinterDriver driver = driverLst.get(selection);
    driver.writeFinalization(privKey);
    try {
      driver.writeOverlay(); /*NOTE: overlaying shares used for debugging purposes only*/
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}

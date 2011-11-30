package secureprinting;

import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalKeyPairShare;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalCiphertextC;
import civitas.crypto.concrete.ElGamalMsgC;
import civitas.crypto.concrete.ElGamalParametersC;
import secureprinting.mixnet.CipherMessage;
import secureprinting.mixnet.Mixnet;
import secureprinting.mixnet.TranslationTable;
import civitas.util.CivitasBigInteger;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;
import secureprinting.visualcrypto.ObliviousPrint;

/**
 * General controller class used for instantiation, coordination, and execution
 * of the Oblivious Printing scheme
 *
 * @author Diran Li
 */
public final class Controller {
  private static Random rnd = new Random();

  public static String nextId() {
    return ("#" + rnd.nextInt(1000));
  }

  /**
   * Demo of the entire Oblivious Printing scheme
   *
   * <p>Default alphabet read from the SecurePrinting/test dir</p>
   * <p>All outputs go into the SecurePrinting/test dir</p>
   *
   */
  public static void main(String args[]) throws CryptoException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)factory.generateElGamalParameters();    // generate group parameters
    ElGamalKeyPairShare share = factory.generateKeyPairShare(params);                       // generate pubKey and privKey

    System.out.println("================MIXNET=======================================================");
    Mixnet mixnet = new Mixnet(3, share);                                                   // generate servers and instantiate TranslationTable
    mixnet.execute(share);                                                                  // iterate through each server which permute and randomize the table

    System.out.println("\n================SHADOW MIX=======================================================");
    System.out.println("Shadow mix validation:");
    mixnet.validate();                                                                      // validate the permutation and randomization performed by each server

    System.out.println("\n================PET=======================================================");
    int selection = new Random().nextInt(5);                                                // pick a random position in the translation table
    ElGamalMsgC plaintxt = new ElGamalMsgC(CivitasBigInteger.valueOf(selection));           // encrypt the value selected
    ElGamalCiphertextC cipher = (ElGamalCiphertextC)factory.elGamalEncrypt(share.pubKey, plaintxt);
    System.out.println("Message to retrieve: " + selection);

    // Perform PET 
    TranslationTable mixedTbl = mixnet.mixedTable;
    CipherMessage cipherMsg = mixedTbl.extract(cipher);                                     // perform PET to extract desired alphabet, which remains encrypted
    System.out.println("Retrieved message:");
    cipherMsg.decryptPrint(share.privKey);


    // Visual Crypto
    System.out.println("\n================VISUAL CRYPTO=======================================================");
    ObliviousPrint op = new ObliviousPrint(cipherMsg, share.pubKey);                        // instantiate object with encrypted alphabet to be printed
    op.execute();                                                                           // generate alpha iterations of printers, each with their random shares
    op.cutAndChoose();                                                                      // reveal and verify alpha-1 iterations 
    op.finalization(share.privKey);                                                         // perform finalization: decrypt and print finalization layer
  }
}

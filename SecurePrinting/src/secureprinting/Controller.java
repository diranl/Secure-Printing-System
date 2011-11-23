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
import secureprinting.visualcrypto.ObliviousPrint;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

/**
 * Generic class used for instantiation, coordination, and execution
 * <p>NOTE: run this class to view a demo of the entire scheme</p>
 *
 * @author Diran Li
 */
public final class Controller {

  private static Random rnd = new Random();

  public static String nextId() {
    return ("#" + rnd.nextInt(1000));
  }

  public static void main(String args[]) throws CryptoException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException {
    // 1. Initialize Mixnet with desired amount of servers
    // 2. Perform the serial/chain computation to yield the mixed result
    // 3. TODO (change): given an input, encrypt, perform PET to yield a CipherMessage
    // 4. Perform the multi-party printing scheme
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)factory.generateElGamalParameters();
    ElGamalKeyPairShare share = factory.generateKeyPairShare(params);

    System.out.println("================MIXNET=======================================================");
    Mixnet mixnet = new Mixnet(3, share);
    mixnet.execute(share);

    // Shadow Mix
    System.out.println("\n================SHADOW MIX=======================================================");
    System.out.println("Shadow mix validation:");
    mixnet.validate();

    // Pick a random position in the table as the plaintext; this position maps to a letter 
    // in the alphabet
    System.out.println("\n================PET=======================================================");
    int selection = new Random().nextInt(5);
    ElGamalMsgC plaintxt = new ElGamalMsgC(CivitasBigInteger.valueOf(selection));
    ElGamalCiphertextC cipher = (ElGamalCiphertextC)factory.elGamalEncrypt(share.pubKey, plaintxt);
    System.out.println("Message to retrieve: " + selection);

    // Perform PET 
    TranslationTable mixedTbl = mixnet.mixedTable;
    CipherMessage cipherMsg = mixedTbl.extract(cipher);
    System.out.println("Retrieved message:");
    cipherMsg.decryptPrint(share.privKey);


    // Visual Crypto
    System.out.println("\n================VISUAL CRYPTO=======================================================");
    ObliviousPrint printing = new ObliviousPrint(3, cipherMsg, share.pubKey);
    printing.execute();
    printing.writeFinalization(share.privKey);
  }
}

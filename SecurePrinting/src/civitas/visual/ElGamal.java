/* NOTE: Tutorial originating from 
 * http://www.java2s.com/Tutorial/Java/0490__Security/ElGamalexamplewithrandomkeygeneration.htm
 */
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ElGamal {
  public static void main(String[] args) throws Exception {
    // Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    Security.addProvider(new BouncyCastleProvider());

    byte[] input = "ab".getBytes();
    Cipher cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
    KeyPairGenerator generator = KeyPairGenerator.getInstance("ElGamal", "BC");
    SecureRandom random = new SecureRandom();

    generator.initialize(128, random);

    KeyPair pair = generator.generateKeyPair();
    Key pubKey = pair.getPublic();
    Key privKey = pair.getPrivate();
    cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);
    byte[] cipherText = cipher.doFinal(input);
    System.out.println("cipher: " + new String(cipherText));

    cipher.init(Cipher.DECRYPT_MODE, privKey);
    byte[] plainText = cipher.doFinal(cipherText);
    System.out.println("plain : " + new String(plainText));
  }
}

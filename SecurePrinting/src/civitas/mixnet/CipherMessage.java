package civitas.mixnet;

import civitas.crypto.CryptoException;
import civitas.util.CivitasBigInteger;
import civitas.crypto.ElGamalCiphertext;
import civitas.crypto.ElGamalKeyPairShare;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.concrete.ElGamalMsgC;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalCiphertextC;
import civitas.crypto.concrete.ElGamalParametersC;
import civitas.crypto.concrete.ElGamalReencryptFactorC;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class CipherMessage extends Message {
  protected ElGamalCiphertext key;
  private List<ElGamalCiphertext> translation;
  
  private static final CivitasBigInteger DEFAULT_FACTOR = CivitasBigInteger.ZERO;

  public CipherMessage(int key, Matrix translation, ElGamalKeyPairShare share) {
    super(translation.rowSize, translation.colSize, translation.rowSize*translation.colSize);
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    this.key = factory.elGamalEncrypt(share.pubKey, 
        new ElGamalMsgC(CivitasBigInteger.valueOf(key)), new ElGamalReencryptFactorC(DEFAULT_FACTOR));
    this.translation = fromMatrix(translation, share);
  }
  /*
  public CipherMessage(int key, String imgfile, ElGamalKeyPairShare share) throws IOException {
    
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    this.key = factory.elGamalEncrypt(share.pubKey, 
        new ElGamalMsgC(CivitasBigInteger.valueOf(key)), new ElGamalReencryptFactorC(DEFAULT_FACTOR));
    BufferedImage image = ImageIO.read(new File(imgfile));
    int width = image.getWidth();
    int height = image.getHeight();
    super(height, width, width*height);
    int[] rgbArray = image.getRGB(0, 0, width, height, null, 0, width);
    this.translation = fromMatrix(toMatrix(width, height, rgbArray), length, share);
  }
   * 
   */

  public void set(int idx, ElGamalCiphertext cipher) {
    translation.set(idx, cipher);
  }
  
  public ElGamalCiphertext get(int idx) {
    return translation.get(idx);
  }
  
  private List<ElGamalCiphertext> fromMatrix(Matrix translation, ElGamalKeyPairShare share) {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    List<ElGamalCiphertext> ciphers = new ArrayList<ElGamalCiphertext>(length);
    ElGamalParametersC params = (ElGamalParametersC)share.params;
    for (int rowIdx=0; rowIdx<translation.rowSize; rowIdx++) {
      for (int colIdx=0; colIdx<translation.colSize; colIdx++) {
        CivitasBigInteger plaintext = (translation.get(rowIdx, colIdx) ? params.g : CivitasBigInteger.ONE);
        ciphers.add(factory.elGamalEncrypt(share.pubKey, new ElGamalMsgC(plaintext), new ElGamalReencryptFactorC(DEFAULT_FACTOR)));
      }
    }
    return ciphers;
  }


  public void print() {
    System.out.println("Message: " + ((ElGamalCiphertextC)key).b);
    ElGamalCiphertextC cipher;
    for (int idx=0; idx<length; idx++) {
      if (idx != 0 && idx % colSize == 0) System.out.println();
      cipher = (ElGamalCiphertextC)translation.get(idx);
      System.out.print((cipher.b.equals(CivitasBigInteger.ONE) ? 0 : 1) + " ");
    }
    System.out.println();
  }

  public void decryptPrint(ElGamalPrivateKey privKey) throws CryptoException {
    BitSet bitSet = new BitSet(length);
    for (int idx=0; idx<length; idx++) {
      if (cipherToBit(privKey, idx) == 1) bitSet.set(idx);
    }
    System.out.println("Message: " + cipherToKey(privKey));
    for (int idx=0; idx<length; idx++) {
      if (idx != 0 && idx % colSize == 0) System.out.println();
      System.out.print((bitSet.get(idx) ? 1 : 0) + " ");
    }
    System.out.println();
  }
  private int cipherToBit(ElGamalPrivateKey privKey, int idx) throws CryptoException {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalMsgC msg = (ElGamalMsgC)factory.elGamalDecrypt(privKey, translation.get(idx));
    return (msg.bigIntValue().equals(CivitasBigInteger.ONE) ? 0 : 1);
  
  }
  private int cipherToKey(ElGamalPrivateKey privKey) throws CryptoException {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalMsgC msg = (ElGamalMsgC)factory.elGamalDecrypt(privKey, key);
    return msg.bigIntValue().intValue();
  }
}

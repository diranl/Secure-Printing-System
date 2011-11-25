/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import java.security.*;
import java.security.spec.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import civitas.crypto.*;
import civitas.crypto.KeyPair;
import civitas.crypto.MessageDigest;
import civitas.crypto.PrivateKey;
import civitas.crypto.PublicKey;
import civitas.crypto.common.Base64;
import civitas.util.CivitasBigInteger;

public class CryptoFactoryC implements CryptoFactory {
  private final boolean DEBUG = false;

  private final int BIT_SECURITY_80 = 1;
  private final int BIT_SECURITY_112 = 2;
  private final int BIT_SECURITY_128 = 3;
  private final int BIT_SECURITY_CCS07 = 0;

  private final int BIT_SECURITY = BIT_SECURITY_80;

  /*
   * The following constants define the algorithms and providers to use.
   */
  private final String MESSAGE_DIGEST_ALG = "SHA-256";
  private final String MESSAGE_DIGEST_PROVIDER = null; // use any provider

  private final String SHARED_KEY_ALG = "AES";
  private final String SHARED_KEY_CIPHER_ALG = "AES"; //"AES/CBC/PKCS7Padding";
  private final String SHARED_KEY_PROVIDER = "BC";    

  private final String PUBLIC_KEY_ALG = "RSA";

  private final String PUBLIC_KEY_CIPHER_ALG = "RSA/ECB/PKCS1Padding";
  //  private final String PUBLIC_KEY_CIPHER_ALG = "RSA/NONE/OAEPPADDING";
  private final String PUBLIC_KEY_SIGNATURE_ALG = "SHA512WithRSAEncryption";
  private final String PUBLIC_KEY_PROVIDER = "BC";

  private int EL_GAMAL_GROUP_LENGTH; // size in bits for p
  private int EL_GAMAL_KEY_LENGTH; // size in bits for q

  private Map<String, KeyGenerator> sharedKeyGenerators = new HashMap<String, KeyGenerator>(); 
  private Map<String, KeyPairGenerator> publicKeyGenerators = new HashMap<String, KeyPairGenerator>();
  private SecretKeyFactory sharedKeyFactory; 
  private KeyFactory publicKeyFactory; 

  // count the number of operations
  private static long numPublicKeyEncs = 0;
  private static long numPublicKeyDecs = 0;
  private static long numPublicKeySign = 0;
  private static long numPublicKeyVerifySig = 0;
  private static long numSharedKeyEncs = 0;
  private static long numSharedKeyDecs = 0;
  private static long numElGamalEncs = 0;
  private static long numElGamalReencs = 0;
  private static long numElGamalDecs = 0;
  private static long numElGamalDecShare = 0;
  private static long numElGamalSignedEncs = 0;
  private static long numElGamalVerifies = 0;

  static {
    BouncyCastleProvider bc = new BouncyCastleProvider();
    Security.addProvider(bc);

    //      // dump the keys and services of the providers.
    //      Provider[] ps = Security.getProviders();
    //      for (int i = 0; i < ps.length; i++) {
    //      System.err.println("Provider: " + ps[i].getName());
    //      System.err.println("Key set");
    //      System.err.println("=======");
    //      System.err.println(ps[i].keySet());
    //      System.err.println("Services");
    //      System.err.println("========");
    //      System.err.println(ps[i].getServices());
    //      }
  }

  private static final CryptoFactoryC singleton = new CryptoFactoryC();
  public static CryptoFactoryC singleton() { return singleton; }

  private CryptoFactoryC() {
    setBitSecurity();
    initializeCryptoProviders();
  }

  /**
   * Initialize the fields of this class that define bit security.
   * I.e., the sizes of keys and nonces.
   */
  private void setBitSecurity() {
    // TODO:  need to initialize nonce sizes too.

    switch (BIT_SECURITY) {
      /* Original CCS submission values.  Not a
       * consistent level of security. */
      case BIT_SECURITY_CCS07:
        EL_GAMAL_KEY_LENGTH = 1024;    
        EL_GAMAL_GROUP_LENGTH = 1025;  // not originally used 
        break;

        /* Note:  the remaining cases set the shared key length to 128.
         * This is generally higher than the bits of security would suggest.
         * The code does this because 128 is the minimum size for AES, which 
         * is the assumed shared key cipher.  For 80 bits of security, e.g.,
         * it would be possible to instead use 3DES.  */

        /* 80 bits of security - NIST says use 2007 to 2010 */
      case BIT_SECURITY_80:
      default:
        EL_GAMAL_KEY_LENGTH = 160;    
        EL_GAMAL_GROUP_LENGTH = 1024;  
        break;

        /* 112 bits of security - NIST says use 2011 to 2030 */
      case BIT_SECURITY_112:
        EL_GAMAL_KEY_LENGTH = 224;    
        EL_GAMAL_GROUP_LENGTH = 2048;   
        break;

        /* 128 bits of security - NIST says use > 2030 */
      case BIT_SECURITY_128:
        EL_GAMAL_KEY_LENGTH = 256;    
        EL_GAMAL_GROUP_LENGTH = 3072; 
        break;
    }
  }


  /**
   * Get an appropriate public key generator, creating one if necessary.
   */
  protected KeyPairGenerator publicKeyGenerator(int keyLength) {
    String genKey = String.valueOf(keyLength);
    KeyPairGenerator g = publicKeyGenerators.get(genKey);
    if (g != null) return g;
    // need to create the public key generator
    try {
      g = KeyPairGenerator.getInstance(PUBLIC_KEY_ALG, PUBLIC_KEY_PROVIDER);
      g.initialize(keyLength);
      publicKeyGenerators.put(genKey, g);
      return g;
    }
    catch (NoSuchAlgorithmException e) {
      throw new CryptoError("Cannot find public key algorithm " + PUBLIC_KEY_ALG);
    }        
    catch (NoSuchProviderException e) {
      throw new CryptoError("Cannot find provider " + PUBLIC_KEY_PROVIDER, e);
    }        
    catch (RuntimeException e) {
      throw new CryptoError("Cannot create key pair generator and/or factory", e);           
    } 
  }

  /**
   * Get an appropriate shared key generator, creating one if necessary.
   */
  protected KeyGenerator sharedKeyGenerator(int keyLength) {
    String genKey = String.valueOf(keyLength);
    KeyGenerator g = sharedKeyGenerators.get(genKey);
    if (g != null) return g;
    // need to create the shared key generator
    try {
      g = KeyGenerator.getInstance(SHARED_KEY_ALG, SHARED_KEY_PROVIDER);
      g.init(keyLength);
      sharedKeyGenerators.put(genKey, g);
      return g;
    }
    catch (NoSuchAlgorithmException e) {
      throw new CryptoError("Cannot find shared key algorithm " + SHARED_KEY_ALG, e);
    }
    catch (NoSuchProviderException e) {
      throw new CryptoError("Cannot find provider " + SHARED_KEY_PROVIDER, e);
    }        
    catch (RuntimeException e) {
      throw new CryptoError("Cannot create key generator", e);           
    } 
  }

  /**
   * Initialize the crypto providers.
   *
   */
  private void initializeCryptoProviders() {
    SecretKeyFactory skf;
    try {
      skf = SecretKeyFactory.getInstance(SHARED_KEY_ALG, SHARED_KEY_PROVIDER);
    }
    catch (NoSuchAlgorithmException e) {
      // this secret key alg does not need a key factory.
      skf = null;
    }
    catch (NoSuchProviderException e) {
      throw new CryptoError("Cannot find provider " + SHARED_KEY_PROVIDER, e);
    }        
    catch (RuntimeException e) {
      throw new CryptoError("Cannot create key factory", e);           
    } 
    sharedKeyFactory = skf; 

    try {
      publicKeyFactory = KeyFactory.getInstance(PUBLIC_KEY_ALG, PUBLIC_KEY_PROVIDER);
    }
    catch (NoSuchAlgorithmException e) {
      throw new CryptoError("Cannot find public key algorithm " + PUBLIC_KEY_ALG);
    }        
    catch (NoSuchProviderException e) {
      throw new CryptoError("Cannot find provider " + PUBLIC_KEY_PROVIDER, e);
    }        
    catch (RuntimeException e) {
      throw new CryptoError("Cannot create key pair generator and/or factory", e);           
    } 
  }


  /*
   * The following methods must return implementation specific KeySpecs.
   */
  private SecretKeySpec secretKeyAlgKeySpec(byte[] bs) {
    return new SecretKeySpec(bs, SHARED_KEY_ALG);        
  }
  private KeySpec publicKeyAlgPublicKeySpec(byte[] bs) {
    return new X509EncodedKeySpec(bs);
  }
  private KeySpec publicKeyAlgPrivateKeySpec(byte[] bs) {
    return new PKCS8EncodedKeySpec(bs);
  }

  /** Generate a Schnorr prime group */
  public ElGamalParameters generateElGamalParameters(int keyLength, int groupLength) {
    return new ElGamalParametersC(keyLength, groupLength);
  } 

  /** Generate a safe prime group */
  public ElGamalParameters generateElGamalParameters(int keyLength) {
    return new ElGamalParametersC(keyLength, keyLength+1);
  } 

  public ElGamalParameters generateElGamalParameters() {
    return generateElGamalParameters(EL_GAMAL_KEY_LENGTH, EL_GAMAL_GROUP_LENGTH);
  } 

  //NOTE: need
  public ElGamalKeyPair generateElGamalKeyPair(ElGamalParameters p) {
    ElGamalParametersC ps = (ElGamalParametersC) p;
    CivitasBigInteger x = CryptoAlgs.randomElement(ps.q);
    CivitasBigInteger y = ps.g.modPow(x, ps.p);
    ElGamalPrivateKeyC k = new ElGamalPrivateKeyC(x, ps);
    ElGamalPublicKeyC K = new ElGamalPublicKeyC(y, ps);
    return new ElGamalKeyPairImpl(K, k);
  }

  public KeyPair generateKeyPair(int keyLength) {        
    java.security.KeyPair kp = publicKeyGenerator(keyLength).generateKeyPair();
    java.security.PublicKey pubk = kp.getPublic();
    java.security.PrivateKey prvk = kp.getPrivate();

    return new KeyPair(new PublicKeyC(pubk, "keypair-" + freshNonceBase64(64)),
        new PrivateKeyC(prvk));
  }

  public ElGamalKeyPairShare generateKeyPairShare(ElGamalParameters params) {
    // The zero knowledge proof is constructed later, in the call to constructKeyShare
    ElGamalParametersC ps = (ElGamalParametersC) params;

    // choose x in Z_q at random. This is the share of the private key.
    CivitasBigInteger x = CryptoAlgs.randomElement(ps.q);
    // the public part of the key is y
    CivitasBigInteger y = ps.g.modPow(x, ps.p);

    ElGamalPublicKey pub = new ElGamalPublicKeyC(y, params);
    ElGamalPrivateKey priv = new ElGamalPrivateKeyC(x, params);
    return new ElGamalKeyPairShare(params, pub, priv);
  }

  // NOTE: workaround for not using generateVoteCapabilityShare
  public ElGamalMsg generateMsgShare(ElGamalParameters p) {
    ElGamalParametersC ps = (ElGamalParametersC) p;
    CivitasBigInteger x = CryptoAlgs.randomElement(ps.q);
    try {
      return new ElGamalMsgC(x, ps);
    }
    catch (CryptoException imposs) {
      throw new CryptoError(imposs);
    }
  }

  public ElGamalPublicKey combineKeyShares(ElGamalKeyShare[] shares) throws CryptoException {
    if (shares == null) return null;
    CivitasBigInteger accum = CivitasBigInteger.ONE;
    ElGamalParameters params = null;
    for (int i = 0; i < shares.length; i++) {
      ElGamalKeyShare s = shares[i];

      // Check the proofs that this is a valid share
      try {
        if (params == null) {
          params = s.pubKey().getParams();
        }
        if (!s.verify()) {
          throw new CryptoException("Invalid share");
        }
      }
      catch (NullPointerException e) {
        throw new CryptoException("Invalid share or proof"); 
      }
      // accumulate the keys..
      if (s.pubKey() instanceof ElGamalPublicKeyC) {
        accum = accum.multiply(((ElGamalPublicKeyC)s.pubKey()).y);
      }
    }
    return new ElGamalPublicKeyC(accum, params);
  }

  //NOTE: need
  public ElGamalCiphertext elGamalEncrypt(ElGamalPublicKey key, ElGamalMsg msg) {
    try {
      numElGamalEncs++;
      ElGamalParametersC ps = (ElGamalParametersC) key.getParams();
      ElGamalPublicKeyC k = (ElGamalPublicKeyC) key;
      CivitasBigInteger m = ((ElGamalMsgC)msg).bigIntValue();
      CivitasBigInteger r = CryptoAlgs.randomElement(ps.q);
      CivitasBigInteger a = ps.g.modPow(r, ps.p);
      CivitasBigInteger b = m.modMultiply(k.y.modPow(r, ps.p), ps.p);
      return new ElGamalCiphertextC(a, b);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  } 

  public ElGamalCiphertext elGamalEncrypt(ElGamalPublicKey key, ElGamalMsg msg, ElGamalReencryptFactor encryptFactor) {
    try {
      numElGamalEncs++;
      ElGamalParametersC ps = (ElGamalParametersC) key.getParams();
      ElGamalPublicKeyC k = (ElGamalPublicKeyC) key;
      CivitasBigInteger r = ((ElGamalReencryptFactorC) encryptFactor).r;
      CivitasBigInteger m = ((ElGamalMsgC) msg).bigIntValue();
      CivitasBigInteger a = ps.g.modPow(r, ps.p);
      CivitasBigInteger b = m.modMultiply(k.y.modPow(r, ps.p), ps.p);
      return new ElGamalCiphertextC(a, b);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }        
  }

  //NOTE:need
  public ElGamalCiphertext elGamalReencrypt(ElGamalPublicKey key, ElGamalCiphertext ciphertext) {
    try {
      numElGamalReencs++;
      ElGamalParametersC ps = (ElGamalParametersC) key.getParams();
      ElGamalPublicKeyC k = (ElGamalPublicKeyC) key;
      ElGamalCiphertextC c = (ElGamalCiphertextC) ciphertext;
      CivitasBigInteger a = c.a;
      CivitasBigInteger b = c.b;
      CivitasBigInteger r = CryptoAlgs.randomElement(ps.q);
      a = a.modMultiply(ps.g.modPow(r, ps.p), ps.p);
      b = b.modMultiply(k.y.modPow(r, ps.p), ps.p);
      return new ElGamalCiphertextC(a, b);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }

  public ElGamalReencryptFactor generateElGamalReencryptFactor(ElGamalParameters params) {
    try {
      ElGamalParametersC ps = (ElGamalParametersC) params;
      return new ElGamalReencryptFactorC(CryptoAlgs.randomElement(ps.q));
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }

  }

  public ElGamalCiphertext elGamalReencrypt(ElGamalPublicKey key, ElGamalCiphertext ciphertext, ElGamalReencryptFactor factor) {
    try {
      numElGamalReencs++;
      ElGamalParametersC ps = (ElGamalParametersC) key.getParams();
      ElGamalPublicKeyC k = (ElGamalPublicKeyC) key;
      ElGamalCiphertextC c = (ElGamalCiphertextC) ciphertext;
      CivitasBigInteger a = c.a;
      CivitasBigInteger b = c.b;
      CivitasBigInteger r = ((ElGamalReencryptFactorC)factor).r;
      a = a.modMultiply(ps.g.modPow(r, ps.p), ps.p);
      b = b.modMultiply(k.y.modPow(r, ps.p), ps.p);
      return new ElGamalCiphertextC(a, b);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }

  //TODO: write a class for Civitas bits
  public ElGamalCiphertextC elGamalXOR(ElGamalPublicKey key, ElGamalCiphertext ciphertext, CivitasBigInteger bit) {
    try {
      if (bit.equals(CivitasBigInteger.ZERO)) {
        return (ElGamalCiphertextC)elGamalReencrypt(key, ciphertext);
      } else {
        ElGamalParametersC ps = (ElGamalParametersC) key.getParams();
        ElGamalPublicKeyC k = (ElGamalPublicKeyC) key;
        ElGamalCiphertextC cipher = (ElGamalCiphertextC) ciphertext;
        CivitasBigInteger a = cipher.a;
        CivitasBigInteger b = cipher.b;
        a = a.modInverse(ps.p);
        b = ps.g.modMultiply(b.modInverse(ps.p), ps.p);
        return new ElGamalCiphertextC(a, b);
      }
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }


  /**
   * @return The decoding of message m to a plaintext.
   * @throws CryptoException If m does not decode to a plaintext i such that 1 <= i <= L.
   */
  public int elGamal1OfLValue(ElGamalMsg m, int L, ElGamalParameters params) throws CryptoException {
    ElGamalMsgC mc = (ElGamalMsgC)m;
    ElGamalParametersC paramsc = (ElGamalParametersC) params;
    // return the int value minus 1, since the well-known ciphertext list is
    // (1, 2, 3, ...), and we want to return the index of the value.
    return paramsc.bruteForceDecode(mc.bigIntValue(),L) - 1;
  }


  /**
   * Convert a hash (or rather, an arbitrary byte array) to 
   * an element from the group defined by the El Gamal parameters. 
   */
  CivitasBigInteger hashToBigInt(byte[] hash) {
    // Force the hash to be positive.
    CivitasBigInteger x = new CivitasBigInteger(1, hash);
    return x;
  }

  /**
   * Computes hash of byte array and convert into Base64
   */
  public String hash(byte[] b) {
    MessageDigest md = messageDigest();
    md.update(b);
    return bytesToBase64(md.digest());
  }

  public String hash(String s) {
    return hash(s.getBytes());
  }

  /**
   * Compute a hash over a list of CivitasBigIntegers.
   */
  byte[] hash(List<CivitasBigInteger> l) {
    // Compute the hash by updating a message digest
    // with the byte representation of the big ints.
    MessageDigest md = messageDigest();
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      CivitasBigInteger i = (CivitasBigInteger)iter.next();
      md.update(i.toByteArray());
    }
    return md.digest();
  }
  CivitasBigInteger hash(CivitasBigInteger a, CivitasBigInteger b) {
    return hash(a,b,null);
  }
  CivitasBigInteger hash(CivitasBigInteger a, CivitasBigInteger b, CivitasBigInteger c) {
    return hash(a,b,c,null);
  }
  CivitasBigInteger hash(CivitasBigInteger a, CivitasBigInteger b, CivitasBigInteger c, byte[] d) {
    // Compute the hash by updating a message digest
    // with the byte representation of the big ints.
    MessageDigest md = messageDigest();
    if (a != null) md.update(a.toByteArray());
    if (b != null) md.update(b.toByteArray());
    if (c != null) md.update(c.toByteArray());
    if (d != null) md.update(d);
    return hashToBigInt(md.digest());
  }


  //NOTE: need
  public ElGamalSignedCiphertext elGamalSignedEncrypt(ElGamalPublicKey key, ElGamalMsg msg) {
    return elGamalSignedEncrypt(key, msg, this.generateElGamalReencryptFactor(key.getParams()));
  }
  public ElGamalSignedCiphertext elGamalSignedEncrypt(ElGamalPublicKey key, ElGamalMsg msg, ElGamalReencryptFactor r) {
    return elGamalSignedEncrypt(key, msg, r, null);        
  }
  public ElGamalSignedCiphertext elGamalSignedEncrypt(ElGamalPublicKey key, ElGamalMsg msg, ElGamalReencryptFactor r, byte[] additionalEnv) {
    try {
      numElGamalSignedEncs++;
      ElGamalParametersC ps = (ElGamalParametersC) key.getParams();
      ElGamalPublicKeyC k = (ElGamalPublicKeyC) key;
      CivitasBigInteger m = ((ElGamalMsgC) msg).bigIntValue();
      CivitasBigInteger rr = ((ElGamalReencryptFactorC)r).r;
      CivitasBigInteger s = CryptoAlgs.randomElement(ps.q);
      CivitasBigInteger a = ps.g.modPow(rr, ps.p);
      CivitasBigInteger b = m.modMultiply(k.y.modPow(rr, ps.p), ps.p);

      CivitasBigInteger c = hash(ps.g.modPow(s, ps.p), a, b, additionalEnv).mod(ps.q); // hash of (g^s,g^r,my^r) == (g^s, a, b)
      CivitasBigInteger d = s.modAdd(c.modMultiply(rr, ps.q), ps.q);
      return new ElGamalSignedCiphertextC(a, b, c, d); 
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }

  //NOTE: need
  public boolean elGamalVerify(ElGamalParameters params, ElGamalSignedCiphertext ciphertext) {
    return elGamalVerify(params, ciphertext, null);
  }
  public boolean elGamalVerify(ElGamalParameters params, ElGamalSignedCiphertext ciphertext, byte[] additionalEnv) {
    try {
      numElGamalVerifies++;
      ElGamalParametersC ps = (ElGamalParametersC) params;
      ElGamalSignedCiphertextC cc = (ElGamalSignedCiphertextC)ciphertext;
      // to verify, check that c == h(g^d * a^(-c), a, b)
      CivitasBigInteger x = ps.g.modPow(cc.d.mod(ps.q), ps.p).modMultiply(cc.a.modPow(cc.c.modNegate(ps.q), ps.p), ps.p);
      CivitasBigInteger v = hash(x, cc.a, cc.b, additionalEnv).mod(ps.q);
      return cc.c.equals(v);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }

  //NOTE: need
  public ElGamalMsg elGamalDecrypt(ElGamalPrivateKey key, ElGamalCiphertext ciphertext) throws CryptoException {
    return elGamalDecryptImpl(key, ciphertext, null);
  }
  private ElGamalMsg elGamalDecryptImpl(ElGamalPrivateKey key, ElGamalCiphertext ciphertext, byte[] additionalEnv) throws CryptoException {
    try {
      numElGamalDecs++;
      ElGamalPrivateKeyC k = (ElGamalPrivateKeyC) key;
      ElGamalParametersC ps = (ElGamalParametersC) key.getParams();

      if (ciphertext instanceof ElGamalSignedCiphertext) {
        if (!elGamalVerify(ps, (ElGamalSignedCiphertext)ciphertext, additionalEnv)) {
          throw new CryptoException("Ciphertext failed verification");
        }
      }
      ElGamalCiphertextC c = (ElGamalCiphertextC) ciphertext;
      CivitasBigInteger a = c.a;
      CivitasBigInteger b = c.b;
      CivitasBigInteger m = b.modDivide(a.modPow(k.x, ps.p), ps.p);
      return new ElGamalMsgC(m);
    }
    catch (ClassCastException e) {
      throw new CryptoError(e);
    } 
    catch (NullPointerException e) {
      throw new CryptoError(e);
    } 

  }
  public ElGamalMsg elGamalDecrypt(ElGamalPrivateKey key, ElGamalSignedCiphertext ciphertext, byte[] additionalEnv) throws CryptoException {
    return elGamalDecryptImpl(key, ciphertext, additionalEnv);
  }


  public ElGamalProofKnowDiscLog constructProofKnowDiscLog(ElGamalParameters prms, ElGamalPrivateKey k) {
    if (k == null || !(k instanceof ElGamalPrivateKeyC)) {
      if (DEBUG) Thread.dumpStack();
      return null;
    }
    if (prms == null || !(prms instanceof ElGamalParametersC)) {
      if (DEBUG) Thread.dumpStack(); 
      return null;
    }
    ElGamalParametersC params = (ElGamalParametersC)prms;
    CivitasBigInteger x = ((ElGamalPrivateKeyC)k).x;
    try {
      CivitasBigInteger v = params.g.modPow(x, params.p);
      CivitasBigInteger z = CryptoAlgs.randomElement(params.q); 
      CivitasBigInteger a = params.g.modPow(z, params.p);
      CivitasBigInteger c = hash(v, a).mod(params.q); // can take mod q without any ill effects.
      CivitasBigInteger r = z.modAdd(c.modMultiply(x, params.q), params.q);
      return new ElGamalProofKnowDiscLogC(a,c,r,v);
    }
    catch (RuntimeException e) {
      if (DEBUG) e.printStackTrace(); 
      return null;
    }
  }

  public PETShare constructPETShare(ElGamalParameters prms, ElGamalCiphertext a, ElGamalCiphertext b) {
    if (a == null || !(a instanceof ElGamalCiphertextC)) return null;
    if (b == null || !(b instanceof ElGamalCiphertextC)) return null;
    if (prms == null || !(prms instanceof ElGamalParametersC)) return null;
    ElGamalParametersC params = (ElGamalParametersC)prms;
    ElGamalCiphertextC ac = (ElGamalCiphertextC)a;
    ElGamalCiphertextC bc = (ElGamalCiphertextC)b;

    try {
      CivitasBigInteger z = CryptoAlgs.randomElement(params.q);
      return new PETShareC(ac, bc, z);
    }
    catch (RuntimeException e) {
      if (DEBUG) e.printStackTrace();
      return  null;
    }        
  }

  public ElGamalMsg elGamalMsg(CivitasBigInteger m, ElGamalParameters params) throws CryptoException {
    try {
      return new ElGamalMsgC(m, (ElGamalParametersC)params);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }

  public ElGamalMsg elGamalMsg(int m, ElGamalParameters params) throws CryptoException {
    try {
      return new ElGamalMsgC(m, (ElGamalParametersC)params);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }

  public ElGamalMsg elGamalMsg(String m, ElGamalParameters params) throws CryptoException {
    try {
      return new ElGamalMsgC(m, (ElGamalParametersC)params);
    } catch (ClassCastException e) {
      throw new CryptoError(e);
    }
  }

  public ElGamalMsg combineDecryptionShares(ElGamalCiphertext c, ElGamalDecryptionShare[] shares, ElGamalParameters params) throws CryptoException { 
    CivitasBigInteger prod = CivitasBigInteger.ONE;       
    try {
      ElGamalCiphertextC cipher = (ElGamalCiphertextC)c;
      ElGamalParametersC ps = (ElGamalParametersC)params;
      for (int i = 0; i < shares.length; i++) {
        ElGamalDecryptionShareC share = (ElGamalDecryptionShareC)shares[i];
        prod = prod.modMultiply(share.ai, ps.p);
      }
      CivitasBigInteger m = cipher.b.modDivide(prod, ps.p);
      return new ElGamalMsgC(m);
    }
    catch (RuntimeException e) {
      throw new CryptoError(e);
    }
  }

  public ElGamalCiphertext combinePETShareDecommitments(PETDecommitment[] decs, ElGamalParameters params) throws CryptoException {        
    CivitasBigInteger d = CivitasBigInteger.ONE;
    CivitasBigInteger e = CivitasBigInteger.ONE;
    ElGamalParametersC ps = (ElGamalParametersC) params;

    for (int i = 0; i < (decs==null?0:decs.length); i++) {
      PETDecommitmentC decom = (PETDecommitmentC)decs[i];
      d = d.modMultiply(decom.di, ps.p);
      e = e.modMultiply(decom.ei, ps.p);            
    }
    return new ElGamalCiphertextC(d, e);
  }

  public boolean petResult(ElGamalMsg petResult) {
    // Pet result is true if the message == 1
    if (petResult instanceof ElGamalMsgC) {
      ElGamalMsgC m = (ElGamalMsgC)petResult;
      return CivitasBigInteger.ONE.equals(m.m);
    }
    return false; 
  }

  public ElGamalDecryptionShare constructDecryptionShare(ElGamalCiphertext c, ElGamalKeyPairShare keyShare) {
    if (c instanceof ElGamalCiphertextC && 
        keyShare.privKey instanceof ElGamalPrivateKeyC &&
        keyShare.privKey.getParams() instanceof ElGamalParametersC) {
      try {
        numElGamalDecShare++;
        ElGamalCiphertextC mc = (ElGamalCiphertextC)c;
        ElGamalPrivateKeyC priv = (ElGamalPrivateKeyC)keyShare.privKey;
        ElGamalParametersC params = (ElGamalParametersC)priv.getParams();
        CivitasBigInteger ai = mc.a.modPow(priv.x, params.p);
        return new ElGamalDecryptionShareC(ai, 
            ElGamalProofDiscLogEqualityC.constructProof(params, 
              mc.a,
              params.g,
              priv.x));
      }
      catch (RuntimeException e) { 
        if (DEBUG) e.printStackTrace(System.err);
      }
        }
    return null;
  }

  public ElGamalKeyShare constructKeyShare(ElGamalKeyPairShare kps) {
    ElGamalKeyShare egks =  elGamalKeyShare(kps.pubKey, 
        constructProofKnowDiscLog(kps.pubKey.getParams(), 
          kps.privKey));
    if (!egks.verify()) {
      throw new CryptoError("Cannot verify a newly created key share!");
    }
    return egks;
  }

  public ElGamalKeyShare elGamalKeyShare(ElGamalPublicKey K, ElGamalProofKnowDiscLog proof) {
    if (K instanceof ElGamalPublicKeyC && proof instanceof ElGamalProofKnowDiscLogC) {
      return new ElGamalKeyShareC((ElGamalPublicKeyC)K, (ElGamalProofKnowDiscLogC)proof);
    }
    if (DEBUG) Thread.dumpStack();
    return null; 
  }


  public byte[] freshNonce(int bitlength) {
    int bytelength = bitlength / 8;
    if (bitlength % 8 != 0) bytelength++; 
    byte[] bs = new byte[bytelength];
    CryptoAlgs.rng().nextBytes(bs);
    return bs;
  }

  public MessageDigest messageDigest() {
    try {
      if (MESSAGE_DIGEST_PROVIDER == null) {
        return new MessageDigestC(java.security.MessageDigest.getInstance(MESSAGE_DIGEST_ALG));
      }
      else {
        return new MessageDigestC(java.security.MessageDigest.getInstance(MESSAGE_DIGEST_ALG, MESSAGE_DIGEST_PROVIDER));                
      }
    }
    catch (NoSuchAlgorithmException e) {
      throw new CryptoError(e);           
    }
    catch (NoSuchProviderException e) {
      throw new CryptoError("No provider " + MESSAGE_DIGEST_PROVIDER);           
    } 
    catch (RuntimeException e) {
      throw new CryptoError("Cannot create message digest", e);           
    } 
  }

  public byte[] messageDigest(byte[] a) {
    MessageDigest md = messageDigest();
    md.update(a);
    return md.digest();
  }
  public byte[] messageDigest(byte[] a, boolean constArray) {
    return messageDigest(a, constArray);
  }
  public byte[] messageDigest(byte[] a, int i) {
    MessageDigest md = messageDigest();
    md.update(a);
    md.update(i);
    return md.digest();
  }
  public byte[] messageDigest(byte[] a, int i, boolean constArray) {
    return messageDigest(a, i, constArray);
  }
  public byte[] messageDigest(String s) {
    MessageDigest md = messageDigest();
    md.update(s);
    return md.digest();
  }

  public int randomInt(int n) {
    if (n <= 0) return 0;
    return CryptoAlgs.rng().nextInt(n);
  }

  private byte[] jseCrypt(String alg, String provider, Key skey, int mode, byte[] input) {

    // Instantiate the cipher
    Cipher cipher;
    try {
      cipher = Cipher.getInstance(alg, provider);
    }
    catch (NoSuchAlgorithmException e) {
      throw new CryptoError("Cannot find algorithm " + alg, e);
    }
    catch (NoSuchPaddingException e) {
      throw new CryptoError("Cannot find algorithm " + alg, e);
    }
    catch (NoSuchProviderException e) {
      throw new CryptoError("Cannot find provider " + provider, e);
    }
    catch (RuntimeException e) {
      throw new CryptoError("Cannot create cipher", e);           
    } 

    try {
      cipher.init(mode, skey);
    }
    catch (InvalidKeyException e) {
      throw new CryptoError("Invalid key.  May need to install unlimited strength crypto policies.", e);
    }

    byte[] output;
    try {
      output = cipher.doFinal(input);
    }
    catch (IllegalBlockSizeException e) {
      throw new CryptoError("Illegal block size", e);
    }
    catch (BadPaddingException e) {
      throw new CryptoError("bad padding", e);
    }
    catch (RuntimeException e) {
      throw new CryptoError(e);            
    }
    return output;
  }
  public byte[] sharedKeyToBytes(SecretKey k) {
    return k.getEncoded();
  }

  public SecretKey sharedKeyFromBytes(byte[] bs) {
    SecretKeySpec skeySpec = secretKeyAlgKeySpec(bs);
    if (sharedKeyFactory == null) {
      // no factory needed
      return skeySpec;
    }
    try {
      return sharedKeyFactory.generateSecret(skeySpec);
    }
    catch (InvalidKeySpecException e) {
      throw new CryptoError(e);
    }
  }

  public byte[] publicKeyToBytes(java.security.PublicKey k) {
    return k.getEncoded();
  }

  public byte[] privateKeyToBytes(java.security.PrivateKey k) {
    return k.getEncoded();
  }

  public java.security.PublicKey publicKeyFromBytes(byte[] bs) {
    KeySpec keySpec = publicKeyAlgPublicKeySpec(bs);
    try {
      return publicKeyFactory.generatePublic(keySpec);
    }
    catch (InvalidKeySpecException e) {
      throw new CryptoError(e);
    }
  }

  public java.security.PrivateKey privateKeyFromBytes(byte[] bs) {
    KeySpec keySpec = publicKeyAlgPrivateKeySpec(bs);
    try {
      return publicKeyFactory.generatePrivate(keySpec);
    }
    catch (InvalidKeySpecException e) {
      throw new CryptoError(e);
    }
  }

  public String freshNonceBase64(int bitlength) {
    return bytesToBase64(freshNonce(bitlength));
  }



  public String bytesToBase64(byte[] a) {
    return Base64.encodeBytes(a);
  }

  public static String bigIntToString(CivitasBigInteger i) {
    return Base64.encodeBytes(i.toByteArray());
  }
  public static CivitasBigInteger stringToBigInt(String s) {
    return new CivitasBigInteger(Base64.decode(s));
  }

  public static long numPublicKeyEncs() { return numPublicKeyEncs; }
  public static long numPublicKeyDecs() { return numPublicKeyDecs; }
  public static long numSharedKeyEncs() { return numSharedKeyEncs; }
  public static long numSharedKeyDecs() { return numSharedKeyDecs; }
  public static long numElGamalEncs() { return numElGamalEncs; }
  public static long numElGamalDecs() { return numElGamalDecs; }
  public static long numElGamalDecShare() { return numElGamalDecShare; }
  public static long numPublicKeySign() { return numPublicKeySign; }
  public static long numPublicKeyVerifySig() { return numPublicKeyVerifySig; }
  public static long numElGamalReencs() { return numElGamalReencs; }
  public static long numElGamalSignedEncs() { return numElGamalSignedEncs; }
  public static long numElGamalVerifies() { return numElGamalVerifies; }

}

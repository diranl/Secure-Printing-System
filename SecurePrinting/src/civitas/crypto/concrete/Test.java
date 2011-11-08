/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalCiphertext;
import civitas.crypto.ElGamalDecryptionShare;
import civitas.crypto.ElGamalKeyPair;
import civitas.crypto.ElGamalKeyPairShare;
import civitas.crypto.ElGamalKeyShare;
import civitas.crypto.ElGamalMsg;
import civitas.crypto.ElGamalPrivateKey;
import civitas.crypto.ElGamalPublicKey;
import civitas.crypto.ElGamalSignedCiphertext;
import civitas.crypto.PETCommitment;
import civitas.crypto.PETDecommitment;
import civitas.crypto.PETShare;
import java.io.*;
import java.security.Provider;
import java.security.Security;

/*TODO: trimming jif
  import jif.lang.Label;
  import jif.lang.LabelUtil;

import civitas.common.CiphertextList;
import civitas.common.VerifiableVote;
import civitas.common.VoterSubmission;
*/
//import civitas.crypto.*;
import civitas.util.CivitasBigInteger;

//import java.io.*;

public class Test {
  public static final CryptoFactoryC f = CryptoFactoryC.singleton();
  private static ElGamalParametersC ps = null;
  private static ElGamalParametersC ps() {
    if (ps != null) return ps;
    ps = (ElGamalParametersC) f.generateElGamalParameters(224, 2048);
    return ps;
  }

  public static final String attack = "Attack at dawn";

  public static void test(String s, boolean b) {
    System.out.println(s + " ? " + (b ? "ok" : "oops !!!!!!!!!!!!!!!!!!!!!!!!!!!!"));
  }

  public static ElGamalMsg distDecrypt(ElGamalCiphertext c, ElGamalKeyPairShare[] keys) {
    ElGamalKeyShare[] tellerPubShares = new ElGamalKeyShare[keys.length];
    for (int i = 0; i < keys.length; i++) {
      tellerPubShares[i] = f.constructKeyShare(keys[i]); 
    }

    ElGamalMsg m2 = null;
    try {
      f.combineKeyShares(tellerPubShares);


      // get the decryption shares
      ElGamalDecryptionShare[] decryptShares = new ElGamalDecryptionShare[keys.length];
      for (int i = 0; i < keys.length; i++) {
        decryptShares[i] = f.constructDecryptionShare(c, keys[i]);
        test("distDecrypt verify decryption share " + i, decryptShares[i].verify(c, keys[i].pubKey)); 
      }
      m2 = f.combineDecryptionShares(c, decryptShares, ps());
    }
    catch (CryptoException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return m2;
  }

  /*TODO: trimmed for jif
  static void foo() {
    Label lbl = LabelUtil.singleton().noComponents();
    ElGamalKeyPair pair = f.generateElGamalKeyPair(ps());
    //ElGamalPrivateKey k = pair.privateKey();
    ElGamalPublicKey K = pair.publicKey();

    int L = 3;
    ElGamalCiphertext[] ciphertexts = f.constructWellKnownCiphertexts(LabelUtil.singleton().noComponents(), K, L);
    ElGamalReencryptFactor encChoiceFactor = f.generateElGamalReencryptFactor(K.getParams());
    ElGamalReencryptFactor encCapFactor = f.generateElGamalReencryptFactor(K.getParams());
    int choice = 1;
    VerifiableVote[] verifV1 = new VerifiableVote[1];
    String context = "cibte";
    for (int i = 0; i < verifV1.length; i++) {            
      ElGamal1OfLReencryptionC encChoice = (ElGamal1OfLReencryptionC)f.elGamal1OfLReencrypt(LabelUtil.singleton().noComponents(),
          K, ciphertexts, L, choice, encChoiceFactor);

      ElGamalCiphertext encCap = f.elGamalEncrypt(K, f.generateVoteCapabilityShare(ps()), encCapFactor);
      ProofVote proofVote = f.constructProofVote(ps(), encCap, encChoice, context, encCapFactor, encChoiceFactor);
      verifV1[i] = new VerifiableVote().civitas$common$VerifiableVote$(context, encChoice , encCap, proofVote);
    }
    VoterSubmission vs1 = new VoterSubmission().civitas$common$VoterSubmission$(lbl, 2, verifV1);

    StringWriter sb = new StringWriter();
    vs1.toXML(lbl, new PrintWriter(sb));
    String vxml = sb.toString();
    try {
      VoterSubmission vs2 = VoterSubmission.fromXML(lbl, new StringReader(vxml));
      System.err.println(vxml);
      System.err.println(vs1.equals(vs2));
    }
    catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }
  */
  public static void main(String[] args) {
    ps();
    //        performance();
    //        foo();
    //      genTest();
    //      msgTest();
    // decTest();
    XORtest();
    //petTest();
    // decryptionShareTest();
    // homoTest();
    //
    //      // showProviderServices();
    //      egHomoTest();
    //      dvrTest();
    //        oneOfLTest();
    //        proofVoteTest();
    //        //      characterEncodings();
    //      sharedKeyTest();
    //      publicKeyTest();
    //      signatureTest();
    //      decryptionShareTest();
    //
    //      petTest();
    //      try {
    //          xmlTest();
    //      }
    //      catch (IllegalArgumentException e) {
    //          e.printStackTrace();
    //      }
    //      catch (IOException e) {
    //          e.printStackTrace();
    //      }
    //      catch (CryptoException e) {
    //          e.printStackTrace();
    //      }
  }

  @SuppressWarnings("unused")
    private static void showProviderServices() {
      Provider[] providers = Security.getProviders();
      for (int i = 0; i < providers.length; i++) {
        Provider p = providers[i];
        System.out.println("NAME: " + p.getName());
        System.out.println("INFO: " + p.getInfo());
        System.out.println("services: " + p.getServices());
      }

    }

  /*TODO: trimmed for jif
  @SuppressWarnings("unused")
    private static void sharedKeyTest() {
      try {
        int keyLength = 256;
        SharedKeyC key = (SharedKeyC)f.generateSharedKey(keyLength);

        String keyXMLinit = key.toXML();
        key = (SharedKeyC)f.sharedKeyFromXML(LabelUtil.singleton().noComponents(), new StringReader(key.toXML()));
        test("shared key xml parsing", key.toXML().equals(keyXMLinit));

        int MSG_LENGTH = 1000;
        String s = "";
        int ch = 0;
        for (int i = 0 ; i < MSG_LENGTH; i++) {
          s += (char)('A' + ch);
          ch = (ch + 1) % 26;
        }
        SharedKeyMsg m = f.sharedKeyMsg(s);

        SharedKeyCiphertextC c = (SharedKeyCiphertextC)f.sharedKeyEncrypt(key, m);

        String ciphertextXMLinit = c.toXML();
        c = (SharedKeyCiphertextC)f.sharedKeyCiphertextFromXML(LabelUtil.singleton().noComponents(), new StringReader(c.toXML()));
        test("ciphertext xml parsing", c.toXML().equals(ciphertextXMLinit));

        SharedKeyMsg m2 = f.sharedKeyDecrypt(key, c);
        boolean test = (m.toString().equals(m2.toString()) && m.toString().length() == MSG_LENGTH);
        test("shared key encryption/decryption", test);
      }
      catch (CryptoException e) {
        e.printStackTrace();
      }
      catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }


    }
    */

  @SuppressWarnings("unused")
    private static void characterEncodings() {
      String s = "";
      int ch = 0;
      int MSG_LENGTH = 100;
      for (int i = 0 ; i < MSG_LENGTH; i++) {
        s += (char)('A' + ch);
        ch = (ch + 1) % 26;
      }

      String[] encodings = {"UTF-16", "UTF-8", "US-ASCII", "ISO-8859-1"};
      for (int i = 0; i < encodings.length; i++) {
        try {
          System.out.println(encodings[i] + " = " + s.getBytes(encodings[i]).length);
        }
        catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }

    }

  /*TODO: trimmed for jif
  @SuppressWarnings("unused")
    private static void publicKeyTest() {
      try {
        int keyLength = 2048;
        KeyPair keyPair = f.generateKeyPair(keyLength);
        PublicKeyC publicKey = (PublicKeyC)keyPair.publicKey;
        PrivateKeyC privateKey = (PrivateKeyC)keyPair.privateKey;

        String pubKeyXMLinit = publicKey.toXML();
        publicKey = (PublicKeyC)f.publicKeyFromXML(LabelUtil.singleton().noComponents(), new StringReader(publicKey.toXML()));
        test("public key xml parsing", publicKey.toXML().equals(pubKeyXMLinit));

        String privKeyXMLinit = privateKey.toXML();
        privateKey = (PrivateKeyC)f.privateKeyFromXML(LabelUtil.singleton().noComponents(), new StringReader(privateKey.toXML()));
        test("private key xml parsing", privateKey.toXML().equals(privKeyXMLinit));

        String s = "";
        int ch = 0;
        int MSG_LENGTH = 2000;
        for (int i = 0 ; i < MSG_LENGTH; i++) {
          s += (char)('A' + ch);
          ch = (ch + 1) % 26;
        }
        PublicKeyMsg m = f.publicKeyMsg(s);

        PublicKeyCiphertextC c = (PublicKeyCiphertextC)f.publicKeyEncrypt(keyPair.publicKey, m);
        PublicKeyMsg m2 = f.publicKeyDecrypt(keyPair.privateKey, c);

        String ciphertextXMLinit = c.toXML();
        c = (PublicKeyCiphertextC)f.publicKeyCiphertextFromXML(LabelUtil.singleton().noComponents(), new StringReader(c.toXML()));
        test("ciphertext xml parsing", c.toXML().equals(ciphertextXMLinit));

        boolean test = (m.toString().equals(m2.toString()) && m.toString().length() == MSG_LENGTH);
        test("public key encryption/decryption ", test);
      }
      catch (CryptoException e) {
        e.printStackTrace();
      }
      catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }


    }
    */

  /*TODO: trimmed for test
  @SuppressWarnings("unused")
    private static void signatureTest() {
      signatureTest(2048, 1000, 1);
    }
  private static long signatureTest(int keyLength, int msgLength, int numTests) {
    long total = 0;
    try {
      KeyPair keyPair = f.generateKeyPair(keyLength);
      //PublicKeyC publicKey = keyPair.publicKey;
      //PrivateKeyC privateKey = keyPair.privateKey;


      String s = "";
      int ch = 0;
      for (int i = 0 ; i < msgLength; i++) {
        s += (char)('A' + ch);
        ch = (ch + 1) % 26;
      }
      PublicKeyMsg m = f.publicKeyMsg(s);

      SignatureC sig = (SignatureC)f.signature(keyPair.privateKey, m);
      String sigxmlinit = sig.toXML();
      sig = (SignatureC)f.signatureFromXML(LabelUtil.singleton().noComponents(), new StringReader(sig.toXML()));
      test("signature xml parsing", sig.toXML().equals(sigxmlinit));

      long start = System.currentTimeMillis();
      test("signature correct ", f.publicKeyVerifySignature(keyPair.publicKey, sig, f.publicKeyMsg(s)));
      long testTime = start - System.currentTimeMillis();
      total += testTime;
    }
    catch (CryptoException e) {
      e.printStackTrace();
    }
    catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return total;

  }
  */

  public static void genTest() {
    System.out.println("p = " + ps.p);
    System.out.println("q = " + ps.q);
    System.out.println("g = " + ps.g);
    test("p=2q+1", ps.p.equals(ps.q.multiply(CivitasBigInteger.valueOf(2)).add(CivitasBigInteger.ONE)));
    // g is a generator of QR_p if it passes two tests.
    // Test 1. g is order q, i.e. g^q = 1.
    test("g^q mod p = 1", ps.g.modPow(ps.q, ps.p).equals(CivitasBigInteger.ONE)); 
    // Test 2. g is in QR_p, i.e. J_p(g) = 1.
    test("J_p(g) = 1", CryptoAlgs.legendreSymbol(ps.g, ps.p, ps.q) == 1);
    // If g \in QR_p then -g \notin QR_p
    test("J_p(-g) = -1", CryptoAlgs.legendreSymbol(ps.g.modNegate(ps.p), ps.p, ps.q) == -1);
  }

  /**
   * Test the designated verifier proof
   *
   */
  /*TODO: trim
  @SuppressWarnings("unused")
    private static void dvrTest() {
      ElGamalKeyPair pair = f.generateElGamalKeyPair(ps);
      //ElGamalPrivateKey k = pair.privateKey();
      ElGamalPublicKey K = pair.publicKey();

      ElGamalKeyPair verifierPair = f.generateElGamalKeyPair(ps);

      ElGamalMsg msg = f.generateVoteCapabilityShare(ps);

      ElGamalReencryptFactor r = f.generateElGamalReencryptFactor(ps);
      ElGamalReencryptFactor rp = f.generateElGamalReencryptFactor(ps);
      ElGamalCiphertext e = f.elGamalEncrypt(K, msg, r);
      ElGamalCiphertext ep = f.elGamalEncrypt(K, msg, rp);

      ElGamalProofDVR proof = f.constructElGamalProofDVR(K, verifierPair.publicKey(), e, ep, r, rp);

      test("DVR proof verifies", proof.verify(K, verifierPair.publicKey()));

      // now construct a fake proof
      ElGamalProofDVR fakeproof = f.constructFakeElGamalProofDVR(K, verifierPair.publicKey(), verifierPair.privateKey(), e, ep);
      test("DVR fake proof verifies", fakeproof.verify(K, verifierPair.publicKey()));


    }
    */

  /**
   * test homomorphic properties of el gamal
   */
  /*TODO: trimmed for jif
  @SuppressWarnings("unused")
    private static void egHomoTest() {
      ElGamalKeyPair pair = f.generateElGamalKeyPair(ps);
      ElGamalPrivateKeyC k = (ElGamalPrivateKeyC)pair.privateKey();
      ElGamalPublicKeyC K = (ElGamalPublicKeyC)pair.publicKey();

      VoteCapabilityShareC m1 = (VoteCapabilityShareC)f.generateVoteCapabilityShare(ps);
      VoteCapabilityShareC m2 = (VoteCapabilityShareC)f.generateVoteCapabilityShare(ps);
      VoteCapabilityShare[][] vs = new VoteCapabilityShare[2][1];
      vs[0][0] = m1;
      vs[1][0] = m2;

      //        System.err.println("q = " + ps.q);
      //        System.err.println("p = " + ps.p);
      //        System.err.println("x = " + k.x);
      //        System.err.println("y = " + K.y);
      //        System.err.println("m1 = " + m1.intValue());
      //        System.err.println("m2 = " + m2.intValue());
      //        System.err.println("m1 in QR = " + m1.encodeQR(ps));
      //        System.err.println("m2 in QR = " + m2.encodeQR(ps));

      ElGamalReencryptFactorC r = new ElGamalReencryptFactorC(CivitasBigInteger.ONE);

      ElGamalSignedCiphertextC c1 = (ElGamalSignedCiphertextC)f.elGamalSignedEncrypt(K, m1, r);
      ElGamalSignedCiphertextC c2 = (ElGamalSignedCiphertextC)f.elGamalSignedEncrypt(K, m2, r);
      //        System.err.println("c1 = " + c1.a + "," + c1.b);
      //        System.err.println("c2 = " + c2.a + "," + c2.b);
      ElGamalSignedCiphertext[][] cs = new ElGamalSignedCiphertext[2][1];
      cs[0][0] = c1;
      cs[1][0] = c2;

      ElGamalMsg mf = f.combineVoteCapabilityShares(LabelUtil.singleton().noComponents(), vs, ps)[0];
      ElGamalCiphertextC cf = (ElGamalCiphertextC)f.multiplyCiphertexts(LabelUtil.singleton().noComponents(), cs, ps)[0];
      //        System.err.println("mf = m1 * m2 = " + mf.intValue());
      //        try {
      //            System.err.println("  sanity check: lower(lift(m1) * list(m2)0= " + ElGamalMsgC.decodeQR(m1.encodeQR(ps).modMultiply(m2.encodeQR(ps), ps.p), ps).intValue());
      //        }
      //        catch (CryptoException e1) {
      //            // TODO Auto-generated catch block
      //            e1.printStackTrace();
      //        }
      //        System.err.println("cf = c1 * c2 = " + cf.a + "," + cf.b);
      //        System.err.println("  sanity check: c1.b * c2.b = " + c1.b.modMultiply(c2.b, ps.p));
      //        System.err.println("                c1.b * c2.b * y = " + c1.b.modMultiply(c2.b, ps.p).modMultiply(K.y, ps.p));
      //        System.err.println("                c1.b * c2.b * y * y= " + c1.b.modMultiply(c2.b, ps.p).modMultiply(K.y, ps.p).modMultiply(K.y, ps.p));

      ElGamalMsg md = null;
      try {
        md = f.elGamalDecrypt(k, cf);
      }
      catch (CryptoException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      //        System.err.println("md = dec(cf) = " + md.intValue());
      test("eg homomorphic", md.equals(mf));


    }
    */
  /**
   * Test one of L encryption and proof
   */
  /*TODO: trimmed for test
  @SuppressWarnings("unused")
    private static void oneOfLTest() {
      ElGamalKeyPair pair = f.generateElGamalKeyPair(ps);
      ElGamalPrivateKey k = pair.privateKey();
      ElGamalPublicKey K = pair.publicKey();

      int L = 3;
      ElGamalCiphertext[] ciphertexts = f.constructWellKnownCiphertexts(LabelUtil.singleton().noComponents(), K, L);

      for (int choice = 0; choice < L; choice++) {
        ElGamalReencryptFactor factor = f.generateElGamalReencryptFactor(K.getParams());
        ElGamal1OfLReencryptionC oneOfL = (ElGamal1OfLReencryptionC)f.elGamal1OfLReencrypt(LabelUtil.singleton().noComponents(),
            K, ciphertexts, L, choice, factor);

        CiphertextList cipherList = new CiphertextList().civitas$common$CiphertextList$(LabelUtil.singleton().noComponents(),ciphertexts);
        test("oneOfL verifies ", oneOfL.verify(K, cipherList, L));

        // decrypt and see what we get
        ElGamalCiphertext cipher = oneOfL.getCiphertext();
        try {
          ElGamalMsg msg = f.elGamalDecrypt(k, cipher);
          System.err.println(f.elGamal1OfLValue(msg, L, ps)+ " and " + choice);
          test("1 of L value correct", f.elGamal1OfLValue(msg, L, ps) == choice);
        }
        catch (CryptoException e) {
          e.printStackTrace();
        }
      }        
    }
    */

  /**
   * Test decryption shares
   */
  @SuppressWarnings("unused")
  private static void decryptionShareTest() {
    // Label lbl = LabelUtil.singleton().noComponents();
    final int NUM_TELLERS = 3;
    ElGamalKeyPairShare[] tellerShares = new ElGamalKeyPairShare[NUM_TELLERS];
    ElGamalKeyShare[] tellerPubShares = new ElGamalKeyShare[NUM_TELLERS];
    for (int i = 0; i < tellerShares.length; i++) {
      tellerShares[i] = f.generateKeyPairShare(ps());      
      tellerPubShares[i] = f.constructKeyShare(tellerShares[i]);
      /*
      StringWriter sb = new StringWriter();
      tellerShares[i].toXML(lbl, new PrintWriter(sb));
      String orig = sb.toString();
      try {
        sb = new StringWriter();
        ElGamalKeyPairShare.fromXML(lbl, reader(orig)).toXML(lbl, new PrintWriter(sb));
        test("XML ElGamalKeyPairShare", orig.equals(sb.toString()));
        orig = ((ElGamalKeyShareC)tellerPubShares[i]).toXML();
        test("XML ElGamalKeyShareC", orig.equals(ElGamalKeyShareC.fromXML(lbl, reader(orig)).toXML()));
      }
      catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      */

    }

    ElGamalPublicKey sharedPubKey = null;
    try {
      sharedPubKey = f.combineKeyShares(tellerPubShares);
    }
    catch (CryptoException e1) {
      e1.printStackTrace();
    }

    // choose a message 
    ElGamalMsgC m = (ElGamalMsgC)f.generateMsgShare(ps);
    // ElGamalMsgC m = (ElGamalMsgC)f.generateVoteCapabilityShare(ps);

    // encrypt it
    ElGamalReencryptFactorC r = (ElGamalReencryptFactorC)f.generateElGamalReencryptFactor(ps);
    ElGamalCiphertextC c = (ElGamalCiphertextC)f.elGamalEncrypt(sharedPubKey, m, r);

    // get the decryption shares
    ElGamalDecryptionShare[] decryptShares = new ElGamalDecryptionShare[NUM_TELLERS];
    for (int i = 0; i < tellerShares.length; i++) {
      decryptShares[i] = f.constructDecryptionShare(c, tellerShares[i]);
      test("verify decryption share " + i, decryptShares[i].verify(c, tellerPubShares[i].pubKey()));
    }
    ElGamalMsgC m2 = null;
    try {
      m2 = (ElGamalMsgC)f.combineDecryptionShares(c, decryptShares, ps);
    }
    catch (CryptoException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    test("distributed decryption", m2.equals(m));
  }
  public static void msgTest() {
    /*
       try {
       m = new ElGamalMsgC(attack, ps);
       } catch (CryptoException e) {
       System.out.println("oops");
       }
       test("BigInt encoding", m.stringValue().equals(attack));
       CivitasBigInteger i1 = null, i2 = null;
       ElGamalMsgC m2 = null;
       try {
       i1 = findQR();
       i2 = new ElGamalMsgC(i1, ps).encodeQR(ps);
       test("Encode a QR", CryptoAlgs.legendreSymbol(i2, ps.p, ps.q) == 1);
       m2 = ElGamalMsgC.decodeQR(i2, ps);
       test("Decode a QR", i1.equals(m2.bigIntValue()));

       i1 = findNonQR();
       i2 = new ElGamalMsgC(i1, ps).encodeQR(ps);
       test("Encode a non-QR", CryptoAlgs.legendreSymbol(i2, ps.p, ps.q) == 1);
       m2 = ElGamalMsgC.decodeQR(i2, ps);
       test("Decode a non-QR", i1.equals(m2.bigIntValue()));

       CivitasBigInteger j = m.encodeQR(ps);
       ElGamalMsgC m3 = ElGamalMsgC.decodeQR(j, ps);
       test("BigInt and QR encoding", m3.stringValue().equals(attack));
       } catch (CryptoException e) {
       System.out.println("oops");
       }

       boolean caught = false;
       try {
       new ElGamalMsgC(ps.q.add(CivitasBigInteger.ONE), ps);
       } catch (CryptoException e) {
       System.out.println("Reject q+1 as message ? ok");
       caught = true;
       }
       if (!caught) {
       System.out.println("Reject q+1 as message ? oops");
       }
     */
  }

  public static CivitasBigInteger findQR() {
    return findQR(1);
  }

  public static CivitasBigInteger findNonQR() {
    return findQR(-1);
  }

  /**
   * @param flag 1 to find QR, -1 to find non-QR
   */
  public static CivitasBigInteger findQR(int flag) {
    CivitasBigInteger i = null;
    do {
      i = CryptoAlgs.randomElement(ps.q);
    } while (CryptoAlgs.legendreSymbol(i, ps.p, ps.q) != flag);
    return i;
  }

  
  public static void XORtest() {
    System.out.println("Testing Homomorphic XOR");
    CivitasBigInteger gZero = CivitasBigInteger.ONE;
    CivitasBigInteger gOne = ps.g.modPow(CivitasBigInteger.ONE, ps.p);

    try {
      ElGamalMsgC mZero = new ElGamalMsgC(gZero);
      ElGamalMsgC mOne = new ElGamalMsgC(gOne);
    
      ElGamalKeyPair p = f.generateElGamalKeyPair(ps);
      ElGamalPublicKey K = p.publicKey();
      ElGamalPrivateKey k = p.privateKey();

      ElGamalCiphertextC cZero = (ElGamalCiphertextC)f.elGamalEncrypt(K, mZero);
      ElGamalCiphertextC cOne = (ElGamalCiphertextC)f.elGamalEncrypt(K, mOne);

      ElGamalCiphertextC c0xor0 = f.elGamalXOR(K, cZero, CivitasBigInteger.ZERO);
      ElGamalCiphertextC c0xor1 = f.elGamalXOR(K, cZero, CivitasBigInteger.ONE);
      ElGamalCiphertextC c1xor0 = f.elGamalXOR(K, cOne, CivitasBigInteger.ZERO);
      ElGamalCiphertextC c1xor1 = f.elGamalXOR(K, cOne, CivitasBigInteger.ONE);

      ElGamalMsgC d0xor0 = (ElGamalMsgC)f.elGamalDecrypt(k, c0xor0);
      ElGamalMsgC d0xor1 = (ElGamalMsgC)f.elGamalDecrypt(k, c0xor1);
      ElGamalMsgC d1xor0 = (ElGamalMsgC)f.elGamalDecrypt(k, c1xor0);
      ElGamalMsgC d1xor1 = (ElGamalMsgC)f.elGamalDecrypt(k, c1xor1);
      
      test("0 XOR 0", d0xor0.m.equals(gZero));
      test("0 XOR 1", d0xor1.m.equals(gOne));
      test("1 XOR 0", d1xor0.m.equals(gOne));
      test("1 XOR 1", d1xor1.m.equals(gZero));

    } catch (Exception e) {
      System.out.println(e);
    }
  }
  
  public static void homoTest() {
    // ElGamalMsgC msg1 = (ElGamalMsgC)f.generateMsgShare(ps);
    // ElGamalMsgC msg2 = (ElGamalMsgC)f.generateMsgShare(ps);
    CivitasBigInteger val1 = CivitasBigInteger.ONE;
    CivitasBigInteger val2 = CivitasBigInteger.TWO;
 

    try {
      CivitasBigInteger hm1 = ps.g.modPow(val1, ps.p);
      CivitasBigInteger hm2 = ps.g.modPow(val2, ps.p);
      System.out.println("got here");

      ElGamalKeyPair p = f.generateElGamalKeyPair(ps);
      ElGamalPublicKey K = p.publicKey();
      ElGamalPrivateKey k = p.privateKey();
      ElGamalMsgC msg1 = new ElGamalMsgC(hm1);
      ElGamalMsgC msg2 = new ElGamalMsgC(hm2);

      ElGamalCiphertextC c1 = (ElGamalCiphertextC)f.elGamalEncrypt(K, msg1);
      ElGamalCiphertextC c2 = (ElGamalCiphertextC)f.elGamalEncrypt(K, msg2);

      //multiplying ciphertexts
      CivitasBigInteger a = c1.a.modMultiply(c2.a, ps.p);
      CivitasBigInteger b = c1.b.modMultiply(c2.b, ps.p);
      ElGamalCiphertextC cf = new ElGamalCiphertextC(a, b);
      ElGamalMsgC ret = (ElGamalMsgC)f.elGamalDecrypt(k, cf);
      ElGamalMsgC mult = new ElGamalMsgC(msg1.m.modMultiply(msg2.m, ps.p));
      test("homomorphic", ret.equals(mult));
      System.out.println("decrypted"+ret.m.toString());
      System.out.println("mult"+mult.m.toString());

    } catch(Exception e) {
      System.out.println(e);
    }
  }
  public static void decTest() {

    // TODO: generate ElGamalMsg instead of using votecap.
    // ElGamalMsgC m = (ElGamalMsgC)f.generateVoteCapabilityShare(ps);
    ElGamalMsgC m = (ElGamalMsgC)f.generateMsgShare(ps);
    System.out.println(m);
    ElGamalKeyPair p = f.generateElGamalKeyPair(ps);
    ElGamalPublicKey K = p.publicKey();
    ElGamalPrivateKey k = p.privateKey();
    ElGamalCiphertext c = f.elGamalEncrypt(K,m);
    ElGamalKeyPair p2 = f.generateElGamalKeyPair(ps);
    ElGamalPrivateKey k2 = p2.privateKey();

    System.out.println("x = " + ((ElGamalPrivateKeyC) k).x);
    System.out.println("y = " + ((ElGamalPublicKeyC) K).y);

    try {
      ElGamalMsgC m1 = (ElGamalMsgC) f.elGamalDecrypt(k,c);

      test(m + "==" + m1, m.equals(m1));

      ElGamalMsgC m2 = (ElGamalMsgC) f.elGamalDecrypt(k2,c);
      test(m + "!=" + m2, !m.equals(m2));

      ElGamalCiphertext c2 = f.elGamalReencrypt(K, c);
      test("Reencryption changes ciphertext", !c.equals(c2));
      ElGamalMsgC m3 = (ElGamalMsgC) f.elGamalDecrypt(k,c2);
      test(m + "==" + m3, m.equals(m3));

      ElGamalSignedCiphertextC c3 = (ElGamalSignedCiphertextC)f.elGamalSignedEncrypt(K, m);
      ElGamalMsgC m4 = (ElGamalMsgC)f.elGamalDecrypt(k,c3);
      test(m + "==" + m4, m.equals(m4));
      boolean b = f.elGamalVerify(ps, c3);
      test("Signature checks", b);

      ElGamalSignedCiphertext c4 = new ElGamalSignedCiphertextC(c3.a, c3.b, CivitasBigInteger.ONE, CivitasBigInteger.ONE);
      boolean b2 = f.elGamalVerify(ps, c4);
      test("Signature corrupted", !b2);

      byte[] addEnv = f.messageDigest("addEnv");
      ElGamalSignedCiphertextC c5 = (ElGamalSignedCiphertextC)f.elGamalSignedEncrypt(K, m, f.generateElGamalReencryptFactor(ps), addEnv);
      ElGamalMsgC m5 = (ElGamalMsgC)f.elGamalDecrypt(k,c5,addEnv);
      test(m + "==" + m5, m.equals(m5));
      boolean b5 = f.elGamalVerify(ps, c5, addEnv);
      test("Signature checks with additional env", b5);

      boolean b6 = f.elGamalVerify(ps, c5, f.messageDigest("wrongEnv"));
      test("Signature corrupted", !b6);
    } catch (CryptoException e) {
      System.out.println("oops: " + e);
    }
  }


  public static void petTest() {
    final int NUM_TELLERS = 3;
    ElGamalKeyPairShare[] tellerShares = new ElGamalKeyPairShare[NUM_TELLERS];
    ElGamalKeyShare[] tellerPubShares = new ElGamalKeyShare[NUM_TELLERS];
    for (int i = 0; i < tellerShares.length; i++) {
      tellerShares[i] = f.generateKeyPairShare(ps);      
      tellerPubShares[i] = f.constructKeyShare(tellerShares[i]); 
    }

    ElGamalPublicKey sharedPubKey = null;
    try {
      sharedPubKey = f.combineKeyShares(tellerPubShares);
    }
    catch (CryptoException e1) {
      e1.printStackTrace();
    }

    // choose matching messages 

    for (int round = 0; round < 2; round++) {
      // ElGamalMsgC m1 = (ElGamalMsgC)f.generateVoteCapabilityShare(ps);
      // ElGamalMsgC m2 = (ElGamalMsgC)(round==0?m1:f.generateVoteCapabilityShare(ps));
      ElGamalMsgC m1 = (ElGamalMsgC)f.generateMsgShare(ps);
      ElGamalMsgC m2 = (ElGamalMsgC)(round==0 ? m1 : f.generateMsgShare(ps));

      ElGamalCiphertext c1 = f.elGamalEncrypt(sharedPubKey, m1);
      ElGamalCiphertext c2 = f.elGamalEncrypt(sharedPubKey, m2);

      PETShare[] petShares = new PETShare[NUM_TELLERS];
      PETCommitment[] petComs = new PETCommitment[NUM_TELLERS];
      PETDecommitment[] petDecoms = new PETDecommitment[NUM_TELLERS];

      for(int i = 0; i < NUM_TELLERS; i++) {
        petShares[i] = f.constructPETShare(ps, c1, c2);
        petComs[i] = petShares[i].commitment(ps);
        petDecoms[i] = petShares[i].decommitment(ps);
        test("PET decommitments verify", petDecoms[i].verify(petComs[i], ps, c1, c2));
      }

      ElGamalCiphertext petResult = null;
      try {
        petResult = f.combinePETShareDecommitments(petDecoms, ps);
      } catch (CryptoException e) {
        System.out.println("oops: " + e);
      }

      // now decrypt
      ElGamalMsg petResDec = distDecrypt(petResult, tellerShares);
      if (round == 0) {
        test("PET result for matching", f.petResult(petResDec));
      }
      else {
        test("PET result for nonmatching", !f.petResult(petResDec));                
      }
    }
  }

  /*TODO:trimmed for jif
  private static void performance() {
    Label lbl = LabelUtil.singleton().noComponents();
    ElGamalParametersC[] ps = new ElGamalParametersC[4]; 
    System.err.println("Generating params 1");
    try {
      ps[0] = ElGamalParametersC.fromXML(LabelUtil.singleton().noComponents(), new FileReader("experiments/keys/elGamalKeyParams-160-1024.xml"));
    }
    catch (IOException e) {
      ps[0] = (ElGamalParametersC) f.generateElGamalParameters(160, 1024);
    }
    System.err.println("Generating params 2");
    ElGamalParametersC ps2 = null;
    try {
      ps[1] = ElGamalParametersC.fromXML(LabelUtil.singleton().noComponents(), new FileReader("experiments/keys/elGamalKeyParams-224-2048.xml"));
    }
    catch (IOException e) {
      ps[1] = (ElGamalParametersC) f.generateElGamalParameters(224, 2048);
    }
    System.err.println("Generating params 3");
    try {
      ps[2] = ElGamalParametersC.fromXML(LabelUtil.singleton().noComponents(), new FileReader("experiments/keys/elGamalKeyParams-1024-1025.xml"));
    }
    catch (IOException e) {
      ps[2] = (ElGamalParametersC) f.generateElGamalParameters(1024, 1025);
    }
    System.err.println("Generating params 4");
    try {
      ps[3] = ElGamalParametersC.fromXML(LabelUtil.singleton().noComponents(), new FileReader("experiments/keys/elGamalKeyParams-256-3072.xml"));
    }
    catch (IOException e) {
      ps[3] = (ElGamalParametersC) f.generateElGamalParameters(256, 3072);
    }

    int COUNT = 100;
    // try doing 100 el gamal encryptions with different length keys
    for (int i = 0; i < ps.length; i++) {
      System.err.println("Testing params " + (i+1));
      ElGamalParametersC params = ps[i];              
      ElGamalKeyPair pair = f.generateElGamalKeyPair(params);
      long start = System.currentTimeMillis();
      for (int j = 0; j < COUNT; j++) {
        ElGamalMsgC msg = (ElGamalMsgC)f.generateVoteCapabilityShare(params);
        ElGamalCiphertext encCap = f.elGamalEncrypt(pair.publicKey(), msg);                
      }
      long total = System.currentTimeMillis() - start;
      System.err.println("Time to do " + COUNT + " encryptions with params " + params.q.bitLength() + "-" + params.p.bitLength() + " : " + total);

      // try some distributed decryptions
      ElGamalMsgC msg = (ElGamalMsgC)f.generateVoteCapabilityShare(params);
      ElGamalCiphertext encCap = f.elGamalEncrypt(pair.publicKey(), msg);
      ElGamalKeyPairShare keyShare = f.generateKeyPairShare(params);
      start = System.currentTimeMillis();
      for (int j = 0; j < COUNT; j++) {
        f.constructDecryptionShare(lbl, lbl, encCap, keyShare);
      }
      total = System.currentTimeMillis() - start;
      System.err.println("Time to do " + COUNT + " distributed decryptions with params " + params.q.bitLength() + "-" + params.p.bitLength() + " : " + total);
    }
  }
  */


  /*TODO:trimmed for jif
  @SuppressWarnings("unused")
    private static void proofVoteTest() {
      ElGamalMsgC capability = (ElGamalMsgC)f.generateVoteCapabilityShare(ps);
      ElGamalKeyPair pair = f.generateElGamalKeyPair(ps);
      @SuppressWarnings("unused")
        ElGamalPrivateKey k = pair.privateKey();
      ElGamalPublicKey K = pair.publicKey();
      String context = "context123";

      int L = 3;
      int choice = 1;

      ElGamalCiphertext[] ciphertexts = f.constructWellKnownCiphertexts(LabelUtil.singleton().noComponents(), K, L);
      ElGamalReencryptFactor factorChoice = f.generateElGamalReencryptFactor(K.getParams());
      ElGamal1OfLReencryption encChoice = f.elGamal1OfLReencrypt(LabelUtil.singleton().noComponents(),
          K, ciphertexts, L, choice, factorChoice);
      ElGamalReencryptFactor factorCap = f.generateElGamalReencryptFactor(K.getParams());
      ElGamalCiphertext encCapability = f.elGamalEncrypt(K, capability, factorCap);

      ProofVote proofVote = f.constructProofVote(ps, encCapability, encChoice, context, factorCap, factorChoice);

      CiphertextList cipherList = new CiphertextList().civitas$common$CiphertextList$(LabelUtil.singleton().noComponents(),ciphertexts);

      VerifiableVote vv = new VerifiableVote().civitas$common$VerifiableVote$(context, encChoice, encCapability, proofVote);
      System.err.println(vv.encChoice.verify(K, cipherList, L));
      System.err.println(vv.proofVote.verify(ps, vv.encCapability, vv.encChoice.getCiphertext(), vv.context));

      test("vv verifies", vv.verify(K, cipherList, L));
    }
    */

  /*TODO:trimmed for jif
  public static void xmlTest() throws IllegalArgumentException, IOException, CryptoException {
    ElGamalKeyPair p = f.generateElGamalKeyPair(ps);
    ElGamalPublicKeyC K = (ElGamalPublicKeyC)p.publicKey();
    ElGamalPrivateKeyC k = (ElGamalPrivateKeyC)p.privateKey();
    ElGamalMsg m = new ElGamalMsgC(CryptoAlgs.randomElement(ps.q), ps);
    ElGamalCiphertextC c = (ElGamalCiphertextC)f.elGamalEncrypt(K,m);
    //ElGamalCiphertext d = f.elGamalEncrypt(K,m);
    Label lbl = LabelUtil.singleton().noComponents();
    String orig;

    //      ElGamalPublicKeyC.java 
    orig = K.toXML();
    test("XML ElGamalPublicKeyC", orig.equals(ElGamalPublicKeyC.fromXML(lbl, reader(orig)).toXML()));

    //      ElGamalPrivateKeyC.java 
    orig = k.toXML();
    test("XML ElGamalPrivateKeyC", orig.equals(ElGamalPrivateKeyC.fromXML(lbl, reader(orig)).toXML()));

    // ElGamalCiphertextC.java 
    orig = c.toXML();
    test("XML ElGamalCiphertextC", orig.equals(ElGamalCiphertextC.fromXML(lbl, reader(orig)).toXML()));

    //      ElGamalParametersC.java
    orig = ps.toXML();
    test("XML ElGamalParametersC", orig.equals(ElGamalParametersC.fromXML(lbl, reader(orig)).toXML()));

    //      ElGamalDecryptionShareC.java 
    //      ElGamalKeyShareC.java 
    // see decryptionShareTest 

    //        ElGamal1OfLReencryptionC.java 
    //      ElGamalProof1OfLC.java
    //      ElGamalProofDiscLogEqualityC.java
    //      ElGamalProofDVRC.java 
    //      ElGamalProofKnowDiscLogC.java
    //      ElGamalReencryptFactorC.java
    //      ElGamalSignedCiphertextC.java 
    //      KeyCiphertextC.java
    //      PETCommitmentC.java
    //      PETDecommitmentC.java 
    //      PETShareC.java 
    //      PrivateKeyC.java
    //      PublicKeyC.java
    //      SharedKeyC.java
    //      SignatureC.java
    //      VoteCapabilityC.java

  }
  */

  private static Reader reader(String orig) {
    return new StringReader(orig);
  }
}

/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.math.BigInteger;

import civitas.crypto.*;

public interface CryptoFactory {
  /** Returns an array of length size that is a permutation, i.e. i maps to j iff ret[i] == j */
  /*TODO: trimmed for jif
  int[] createPermutation(Label lbl, int size);
  */

  //    KeyPairPrincipal keyPair(String name, ElGamalPublicKey publicKey, ElGamalPrivateKey privateKey);
  //    KeyPairPrincipal keyPair(String name, PublicKey publicKey);
  //    KeyPairPrincipal keyPair(String name, PublicKey publicKey, PrivateKey privateKey);

  // TODO: it is unclear what the label on the return result should be.
  // ElGamalPublicKey egPubKeyFromFile(String keyFile) throws IllegalArgumentException, FileNotFoundException, IOException;
  // ElGamalPrivateKey egPrivKeyFromFile(String keyFile) throws IllegalArgumentException, FileNotFoundException, IOException;
  // PublicKey publicKeyFromFile(String keyFile) throws IllegalArgumentException, FileNotFoundException, IOException;
  // PrivateKey privateKeyFromFile(String keyFile) throws IllegalArgumentException, FileNotFoundException, IOException;

  KeyPair generateKeyPair(int keyLength);

  ElGamalParameters generateElGamalParameters();
  ElGamalParameters generateElGamalParameters(int keyLength, int groupLength);
  ElGamalKeyPair generateElGamalKeyPair(ElGamalParameters params);
  ElGamalKeyPairShare generateKeyPairShare(ElGamalParameters params);
  ElGamalKeyShare constructKeyShare(ElGamalKeyPairShare kps);
  ElGamalPublicKey combineKeyShares(ElGamalKeyShare[] shares) throws CryptoException;
  ElGamalCiphertext elGamalEncrypt(ElGamalPublicKey key, ElGamalMsg msg);
  ElGamalCiphertext elGamalEncrypt(ElGamalPublicKey key, ElGamalMsg msg, ElGamalReencryptFactor r);
  ElGamalCiphertext elGamalReencrypt(ElGamalPublicKey key, ElGamalCiphertext c);
  ElGamalCiphertext elGamalReencrypt(ElGamalPublicKey key, ElGamalCiphertext c, ElGamalReencryptFactor r);
  ElGamalReencryptFactor generateElGamalReencryptFactor(ElGamalParameters params);
  ElGamalSignedCiphertext elGamalSignedEncrypt(ElGamalPublicKey key, ElGamalMsg msg);
  ElGamalSignedCiphertext elGamalSignedEncrypt(ElGamalPublicKey key, ElGamalMsg msg, ElGamalReencryptFactor r);
  ElGamalSignedCiphertext elGamalSignedEncrypt(ElGamalPublicKey key, ElGamalMsg msg, ElGamalReencryptFactor r, byte[] additionalEnv);
  boolean elGamalVerify(ElGamalParameters params, ElGamalSignedCiphertext ciphertext);
  boolean elGamalVerify(ElGamalParameters params, ElGamalSignedCiphertext ciphertext, byte[] additionalEnv);
  ElGamalMsg elGamalDecrypt(ElGamalPrivateKey key, ElGamalCiphertext ciphertext) throws CryptoException;
  ElGamalMsg elGamalDecrypt(ElGamalPrivateKey key, ElGamalSignedCiphertext ciphertext, byte[] additionalEnv) throws CryptoException;
  ElGamalProofKnowDiscLog constructProofKnowDiscLog(ElGamalParameters params, ElGamalPrivateKey k);
  //ElGamalProofDVR constructElGamalProofDVR(ElGamalPublicKey k, ElGamalPublicKey verifierKey, ElGamalCiphertext e, ElGamalCiphertext ePrime, ElGamalReencryptFactor er, ElGamalReencryptFactor erPrime);
  //ElGamalProofDVR constructFakeElGamalProofDVR(ElGamalPublicKey k, ElGamalPublicKey verifierKey, ElGamalPrivateKey verifierPrivKey, ElGamalCiphertext e, ElGamalCiphertext ePrime);
  PETShare constructPETShare(ElGamalParameters params, ElGamalCiphertext a, ElGamalCiphertext b);
  // ElGamalCiphertext combinePETShareDecommitments(Label lbl, PETDecommitment[] decs, ElGamalParameters params) throws CryptoException;
  /** returns true iff the ElGamalMessage resulting from the PET says the plain texts are equivalent */
  boolean petResult(ElGamalMsg petResult);

  ElGamalMsg generateMsgShare(ElGamalParameters p);
  // VoteCapabilityShare generateVoteCapabilityShare(ElGamalParameters params);
  // VoteCapability[] combineVoteCapabilityShares(Label lbl, VoteCapabilityShare[][] shares, ElGamalParameters params);
  // ElGamalCiphertext[] multiplyCiphertexts(Label lbl, ElGamalSignedCiphertext[][] shares, ElGamalParameters params);

  // ElGamalCiphertext[] constructWellKnownCiphertexts(Label lbl, ElGamalPublicKey key, int count);

  // ElGamalDecryptionShare constructDecryptionShare(Label lbl, Label lbl2, ElGamalCiphertext c, ElGamalKeyPairShare keyShare);
  // ElGamalMsg combineDecryptionShares(Label lbl, ElGamalCiphertext c, ElGamalDecryptionShare[] shares, ElGamalParameters params) throws CryptoException;

  // ElGamal1OfLReencryption elGamal1OfLReencrypt(Label lbl, ElGamalPublicKey key, ElGamalCiphertext[] ciphertexts, int L, int choice, ElGamalReencryptFactor reencryptFactor); 
  int elGamal1OfLValue(ElGamalMsg m, int L, ElGamalParameters params) throws CryptoException;

  // ProofVote constructProofVote(ElGamalParameters params, ElGamalCiphertext encCapability, ElGamal1OfLReencryption encChoice, String context, ElGamalReencryptFactor encCapabilityFactor, ElGamalReencryptFactor encChoiceFactor);


  /*
   * Public Key and shared key encryption
   */    
  //PublicKeyCiphertext publicKeyEncrypt(PublicKey key, PublicKeyMsg msg);
  //PublicKeyMsg publicKeyDecrypt(PrivateKey key, PublicKeyCiphertext ciphertext) throws CryptoException;
  //SharedKeyCiphertext sharedKeyEncrypt(SharedKey key, SharedKeyMsg msg);
  //SharedKeyMsg sharedKeyDecrypt(SharedKey key, SharedKeyCiphertext ciphertext) throws CryptoException;
  //SharedKey generateSharedKey(int keyLength);

  /* hashing and nonces
   * Note that we assume the digest does not reveal any information about 
   * the data used to produce the digest.
   */
  byte[] freshNonce(int bitlength);

  byte[] messageDigest(byte[] a, int b);
  byte[] messageDigest(byte[] a);
  byte[] messageDigest(byte[] a, int b, boolean constBytes);
  byte[] messageDigest(byte[] a, boolean constBytes);
  byte[] messageDigest(String s);

  /*
   * Base64 methods
   */
  String freshNonceBase64(int bitlength);
  String bytesToBase64(byte[] a);
  // String constBytesToBase64(Label lbl, byte[] a);

  /*
   * Randomness
   */

  /**
   * Return a non-negative int less than n. n must be a positive integer.
   */
  int randomInt(int n); 

  /*
   * Public key signing operations
   */
  // Signature signature(PrivateKey k, PublicKeyMsg m);
  // boolean publicKeyVerifySignature(PublicKey K, Signature s, PublicKeyMsg m);    
  // PublicKeyMsg publicKeyVerifySignatureMsg(PublicKey K, Signature s, PublicKeyMsg m);    
  // Signature signature(PrivateKey k, Label lbl, byte[] bytes);
  // boolean publicKeyVerifySignature(PublicKey K, Signature s, Label lbl, byte[] bytes);    
  // boolean publicKeyVerifySignature(PublicKey K, Signature s, byte[] bytes);    
  /*
   * XML parsing methods
   */
  // ElGamalPublicKey elGamalPublicKeyFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalPrivateKey elGamalPrivateKeyFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalParameters elGamalParametersFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalProofKnowDiscLog elGamalProofKnowDiscLogFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalProofDiscLogEquality elGamalProofDiscLogEqualityFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalCiphertext elGamalCiphertextFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalSignedCiphertext elGamalSignedCiphertextFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalDecryptionShare decryptionShareFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // PETCommitment petCommitmentFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // PETDecommitment petDecommitmentFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalKeyShare elGamalKeyShareFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamal1OfLReencryption elGamal1OfLReencryptionFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalProof1OfL elGamalProof1OfLFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalReencryptFactor elGamalReencryptFactorFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // PrivateKey privateKeyFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // PublicKeyCiphertext publicKeyCiphertextFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // SharedKeyCiphertext sharedKeyCiphertextFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // SharedKey sharedKeyFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // SharedKey sharedKeyFromWire(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ElGamalProofDVR elGamalProofDVRFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // VoteCapability voteCapabilityFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // VoteCapabilityShare voteCapabilityShareFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // PublicKey publicKeyFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // Signature signatureFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // ProofVote proofVoteFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;
  // PETShare petShareFromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException;

  /*
   * Factory methods
   */
  ElGamalMsg elGamalMsg(int m, ElGamalParameters p) throws CryptoException;
  ElGamalMsg elGamalMsg(String m, ElGamalParameters p) throws CryptoException;
  ElGamalKeyShare elGamalKeyShare(ElGamalPublicKey K, ElGamalProofKnowDiscLog proof);
  // PublicKeyMsg publicKeyMsg(String m) throws CryptoException;
  // SharedKeyMsg sharedKeyMsg(String m) throws CryptoException;

}

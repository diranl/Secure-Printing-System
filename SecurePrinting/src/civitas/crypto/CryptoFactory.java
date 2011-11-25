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
  PETShare constructPETShare(ElGamalParameters params, ElGamalCiphertext a, ElGamalCiphertext b);
  ElGamalCiphertext combinePETShareDecommitments(PETDecommitment[] decs, ElGamalParameters params) throws CryptoException;
  /** returns true iff the ElGamalMessage resulting from the PET says the plain texts are equivalent */
  boolean petResult(ElGamalMsg petResult);

  ElGamalMsg generateMsgShare(ElGamalParameters p);
  int elGamal1OfLValue(ElGamalMsg m, int L, ElGamalParameters params) throws CryptoException;

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
   * Factory methods
   */
  ElGamalMsg elGamalMsg(int m, ElGamalParameters p) throws CryptoException;
  ElGamalMsg elGamalMsg(String m, ElGamalParameters p) throws CryptoException;
  ElGamalKeyShare elGamalKeyShare(ElGamalPublicKey K, ElGamalProofKnowDiscLog proof);
}

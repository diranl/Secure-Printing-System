/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

public class ElGamalKeyPairImpl implements ElGamalKeyPair {
  final ElGamalPublicKey K;
  final ElGamalPrivateKey k;

  public ElGamalKeyPairImpl(ElGamalPublicKey K, ElGamalPrivateKey k) {
    this.K = K;
    this.k = k;
  }

  public ElGamalPublicKey publicKey() {
    return K;
  }

  public ElGamalPrivateKey privateKey() {
    return k;
  }
}

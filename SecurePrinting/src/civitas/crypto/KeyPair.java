/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

public class KeyPair {
    public final PublicKey publicKey;
    // by definition, the private key is only readable
    // by the principal represented by the publicKey
    public final PrivateKey privateKey;

    public KeyPair(final PublicKey publicKey, final PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}

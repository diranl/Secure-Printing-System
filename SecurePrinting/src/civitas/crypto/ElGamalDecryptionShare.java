/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.*;

/**
 * A teller's share of a decryption. This is used for distributed non-threshold ElGamal.
 */
public interface ElGamalDecryptionShare {
    public final static String OPENING_TAG = "elGamalDecryptionShare";
    public ElGamalProofDiscLogEquality getProof();
    
    /**
     * Verify the decryption share
     * @param m the ciphertext being decrypted
     * @param K the public key for decrypting the ciphertext
     * @return
     */
    public boolean verify(ElGamalCiphertext m, ElGamalPublicKey K);
//    public ElGamalParameters{this} getParams();

 //   public void toXML(label lbl, PrintWriter[lbl] sb);
}

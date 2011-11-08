/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.*;
import java.math.BigInteger;

import civitas.crypto.ElGamalCiphertext;
import civitas.crypto.ElGamalParameters;
import civitas.crypto.PETCommitment;

public interface PETDecommitment {
    /*TODO:trim
    public final static String{*<-*} OPENING_TAG = "petD";
    public void toXML{*lbl}(label lbl, PrintWriter[lbl]{*lbl} sb) where {this} <= lbl;
     * 
     */
    
    ElGamalProofDiscLogEquality proof();
    
    /**
     * Verify that the decommitment and the commitment agree
     */
    public boolean verify(PETCommitment c, ElGamalParameters params, ElGamalCiphertext ciphertext1, ElGamalCiphertext ciphertext2);
}
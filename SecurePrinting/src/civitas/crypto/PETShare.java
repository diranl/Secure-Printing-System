/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.*;

/**
 * A server's share of the info needed for a distrubted Plaintext Eqiuvalence Test.
 */
public interface PETShare {
    
    ElGamalCiphertext ciphertext1();
    ElGamalCiphertext ciphertext2();

    PETCommitment commitment(ElGamalParameters params);
    PETDecommitment decommitment(ElGamalParameters params);
    
    //public void toXML{*lbl}(label lbl, PrintWriter[lbl]{*lbl} sb);
  
}
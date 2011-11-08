/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.*;

/**
 * Proof that log_{g_1} v = log_{g_2} w. That is, that v = g_1^x and w = g_2^x 
 */
public interface ElGamalProofDiscLogEquality {
    public boolean verify(ElGamalParameters params);
    // public void toXML(label lbl, PrintWriter[] sb);
}

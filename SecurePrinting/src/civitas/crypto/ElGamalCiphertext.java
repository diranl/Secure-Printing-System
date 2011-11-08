/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.*;

public interface ElGamalCiphertext {
    public static final String OPENING_TAG = "elGamalCiphertext";
    /*TODO: trim
    public void toXML(label lbl, PrintWriter[] sb);
    public void toUnsignedCiphertextXML(label lbl, PrintWriter[] sb);
    public boolean equals(ElGamalCiphertext o);
     * 
     */
    public int hashCode();
}

/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.*;

/**
 * Proof that an entity knows x in v = g^x.
 */
public interface ElGamalProofKnowDiscLog {
    public boolean verify(ElGamalParameters params);
    // public void toXML{*lbl}(label lbl, PrintWriter[lbl]{*lbl} sb) where {this} <= lbl;
}

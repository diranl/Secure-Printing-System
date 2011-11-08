/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import civitas.common.*;
import java.io.*;

/**
 * A teller's share of a public key. Note that a ElGamalKeyPairShare does not contain
 * the proof objects, and does contain the private key share too.
 */
public interface ElGamalKeyShare {
  //public void toXML(label lbl, PrintWriter[] sb);
	ElGamalPublicKey pubKey();
	ElGamalProofKnowDiscLog proof();
	boolean verify();
}

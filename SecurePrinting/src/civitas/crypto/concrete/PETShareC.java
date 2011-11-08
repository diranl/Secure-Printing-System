/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/*TODO: trim
import jif.lang.Label;
import civitas.common.Util;
 * 
 */
import civitas.crypto.*;
import civitas.util.CivitasBigInteger;

class PETShareC implements PETShare {
    protected final ElGamalCiphertextC ciphertext1;
    protected final ElGamalCiphertextC ciphertext2;
    
    public final CivitasBigInteger exponent;
    
    public PETShareC(ElGamalCiphertextC ciphertext1, ElGamalCiphertextC ciphertext2, CivitasBigInteger exponent) {
        this.ciphertext1 = ciphertext1;
        this.ciphertext2 = ciphertext2;
        this.exponent = exponent;
    }
    
    public ElGamalCiphertext ciphertext1() {
        return ciphertext1;
    }
    public ElGamalCiphertext ciphertext2() {
        return ciphertext2;
    }

    // return a hash of the ciphertexts and exponent
    public PETCommitment commitment(ElGamalParameters params) {
        try {
            ElGamalParametersC ps = (ElGamalParametersC)params;
            CryptoFactoryC factory = CryptoFactoryC.singleton();

            CivitasBigInteger zi = exponent;
            CivitasBigInteger d = ciphertext1.a.modDivide(ciphertext2.a, ps.p);
            CivitasBigInteger e = ciphertext1.b.modDivide(ciphertext2.b, ps.p);

            CivitasBigInteger di = d.modPow(zi, ps.p);
            CivitasBigInteger ei = e.modPow(zi, ps.p);

            return new PETCommitmentC(factory.hash(di, ei));
        }
        catch (ClassCastException e) {
            return null;
        }
    }
    
    // return the pair (d_i, e_i)
    public PETDecommitment decommitment(ElGamalParameters p) {
        try {
            ElGamalParametersC params = (ElGamalParametersC)p;

            CivitasBigInteger zi = exponent;
            CivitasBigInteger d = ciphertext1.a.modDivide(ciphertext2.a, params.p);
            CivitasBigInteger e = ciphertext1.b.modDivide(ciphertext2.b, params.p);

            CivitasBigInteger di = d.modPow(zi, params.p);
            CivitasBigInteger ei = e.modPow(zi, params.p);

            return new PETDecommitmentC(di, ei, decommitmentProof(params, d, e, zi)); 
        }
        catch (ClassCastException e) {
            return null;
        }
    }
    
    private static ElGamalProofDiscLogEquality decommitmentProof(ElGamalParametersC params,
            CivitasBigInteger g1,
            CivitasBigInteger g2, 
            CivitasBigInteger x) {
        return ElGamalProofDiscLogEqualityC.constructProof(params, g1, g2, x);
    }

    /*TODO:trim
    public void toXML(Label lbl, PrintWriter sb) {
        if (sb == null) return;
        sb.append("<petShare>");
        if (this.ciphertext1 != null) {
            this.ciphertext1.toUnsignedCiphertextXML(lbl, sb);
        }
        if (this.ciphertext2 != null) {
            this.ciphertext2.toUnsignedCiphertextXML(lbl, sb);
        }
        if (this.exponent != null) {
            sb.append("<exponent>");
            Util.escapeString(CryptoFactoryC.bigIntToString(this.exponent), lbl, sb);
            sb.append("</exponent>");
        }
        sb.append("</petShare>");
    }
    
    public static PETShareC fromXML(Label lbl, Reader r) throws IOException {
        Util.swallowTag(lbl, r, "petShare");
        
        ElGamalCiphertextC ciphertext1 = null;
        ElGamalCiphertextC ciphertext2 = null;

        ciphertext1 = ElGamalCiphertextC.fromXML(lbl, r);
        ciphertext2 = ElGamalCiphertextC.fromXML(lbl, r);



        CivitasBigInteger exponent = CryptoFactoryC.stringToBigInt(Util.unescapeString(Util.readSimpleTag(lbl, r, "exponent")));

        
        Util.swallowEndTag(lbl, r, "petShare");

        return new PETShareC(ciphertext1, ciphertext2, exponent);
    }
     * 
     */

	public ElGamalCiphertext ciphertextA() {
		return ciphertext1;
	}

	public ElGamalCiphertext ciphertextB() {
		return ciphertext2;
	}

	public CivitasBigInteger exponent() {
		return exponent;
	}
}
/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;


/*TODO: trim
import jif.lang.Label;
import jif.lang.LabelUtil;
import civitas.common.Util;
 * 
 */
import civitas.crypto.*;
import civitas.util.CivitasBigInteger;

/**
 * A server's decommitment for a PET share
 */
public class PETDecommitmentC implements PETDecommitment {
    public final CivitasBigInteger di;
    public final CivitasBigInteger ei;
    public final ElGamalProofDiscLogEquality proof;
        
    public PETDecommitmentC(CivitasBigInteger di, CivitasBigInteger ei, ElGamalProofDiscLogEquality proof) {
        this.di = di;
        this.ei = ei;
        this.proof = proof;
    }
    
    // return proof of equality of logs of d_i and e_i
    public ElGamalProofDiscLogEquality proof() {
    	return proof;
    }
    
    public boolean verify(PETCommitment c, ElGamalParameters params, ElGamalCiphertext ciphertext1, ElGamalCiphertext ciphertext2) {
        if (!(c instanceof PETCommitmentC)) {
            return false;
        }
        ElGamalProofDiscLogEqualityC prf = (ElGamalProofDiscLogEqualityC)proof;
        ElGamalParametersC ps = (ElGamalParametersC)params;
        PETCommitmentC com = (PETCommitmentC)c;
        ElGamalCiphertextC m1 = (ElGamalCiphertextC)ciphertext1;
        ElGamalCiphertextC m2 = (ElGamalCiphertextC)ciphertext2;
        
        CryptoFactoryC factory = CryptoFactoryC.singleton();

        CivitasBigInteger d = m1.a.modDivide(m2.a, ps.p);
        CivitasBigInteger e = m1.b.modDivide(m2.b, ps.p);
        
        
        // check that it's a proof of the correct thing.
        if (di == null || ei == null) return false;
        if (!d.equals(prf.g1) || !e.equals(prf.g2)) return false;

        return com.hash.equals(factory.hash(di, ei)) && prf.verify(params);
    }
    

    /*TODO: trim
    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }
    public void toXML(Label lbl, PrintWriter s) {
        s.print('<'); s.print(OPENING_TAG); s.print('>');
        s.print("<d>");
        if (di != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.di), lbl, s);
        s.print("</d>");
        s.print("<e>");
        if (ei != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.ei), lbl, s);
        s.print("</e>");
        s.print("<prf>");
        proof.toXML(lbl, s);
        s.print("</prf>");
        s.print("</"); s.print(OPENING_TAG); s.print('>');
    }
    
    public static PETDecommitmentC fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        Util.swallowTag(lbl, r, OPENING_TAG);
        String d = Util.unescapeString(Util.readSimpleTag(lbl, r, "d"));
        String e = Util.unescapeString(Util.readSimpleTag(lbl, r, "e"));
        Util.swallowTag(lbl, r, "prf");
        ElGamalProofDiscLogEquality proof = ElGamalProofDiscLogEqualityC.fromXML(lbl, r);
        Util.swallowEndTag(lbl, r, "prf");
        Util.swallowEndTag(lbl, r, OPENING_TAG);
        return new PETDecommitmentC(CryptoFactoryC.stringToBigInt(d), CryptoFactoryC.stringToBigInt(e), proof);
    }
     * 
     */

}
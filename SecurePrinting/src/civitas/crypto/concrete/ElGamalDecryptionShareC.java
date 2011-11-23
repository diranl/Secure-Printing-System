/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;


/* TODO: trim
import jif.lang.Label;
import jif.lang.LabelUtil;
 *
 */
// import civitas.common.Util;
import civitas.crypto.*;
import civitas.util.CivitasBigInteger;

public class ElGamalDecryptionShareC implements ElGamalDecryptionShare {
    public final CivitasBigInteger ai;
    public final ElGamalProofDiscLogEqualityC proof;

    public ElGamalDecryptionShareC(CivitasBigInteger ai, ElGamalProofDiscLogEqualityC proof) {
        this.ai = ai;
        this.proof = proof;
    }
        
    public ElGamalProofDiscLogEquality getProof() {
        return proof;
    }
    
    /**
     * 
     * @param m the ciphertext being decrypted
     * @return
     */
    public boolean verify(ElGamalCiphertext c, ElGamalPublicKey K) {
        if (proof != null) {
            try {
                ElGamalCiphertextC cipher = (ElGamalCiphertextC)c;
                ElGamalPublicKeyC KC = (ElGamalPublicKeyC)K;
                ElGamalParametersC params = (ElGamalParametersC)K.getParams();

                // check that 
                //    proof.g1 = m.a and
                //    proof.g2 == params.g and
                //    proof.v == ai 
                //    proof.w == yi == public key and
                if (proof.g1.equals(cipher.a) && proof.g2.equals(params.g) &&
                        proof.v.equals(ai) && proof.w.equals(KC.y)) { 
                    return proof.verify(params);
                }
                else {
                    // failed verification
                    System.err.println("proof.g1 = " + proof.g1);
                    System.err.println("    mc.a = " + cipher.a);
                    System.err.println("proof.g2 = " + proof.g2);
                    System.err.println("params.g = " + params.g);
                    System.err.println("       v = " + proof.v);
                    System.err.println("      ai = " + ai);
                    System.err.println("    kc.y = " + KC.y);
                    System.err.println("       w = " + proof.w);
                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            } 
            catch (ClassCastException e) {
                e.printStackTrace();
                return false;
            } 
        }
        
        return false;
    }
    /* TODO: trim
    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }
    public void toXML(Label lbl, PrintWriter s) {
        s.print('<');
        s.print(OPENING_TAG);
        s.print('>');
        s.print("<ai>");
        if (ai != null) Util.escapeString(CryptoFactoryC.bigIntToString(ai), lbl, s);
        s.print("</ai>");
        if (proof != null) this.proof.toXML(lbl, s);
        s.print("</");
        s.print(OPENING_TAG);
        s.print('>');
    }


    public static ElGamalDecryptionShareC fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        Util.swallowTag(lbl, r, OPENING_TAG);

        String sa = Util.unescapeString(Util.readSimpleTag(lbl, r, "ai"));
        CivitasBigInteger ai = CryptoFactoryC.stringToBigInt(sa);
        
        ElGamalProofDiscLogEqualityC proof = (ElGamalProofDiscLogEqualityC)CryptoFactoryC.singleton().elGamalProofDiscLogEqualityFromXML(lbl, r);

        Util.swallowEndTag(lbl, r, OPENING_TAG);
        return new ElGamalDecryptionShareC(ai, proof);
    }     
    */

}

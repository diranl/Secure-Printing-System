/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import java.io.*;

/*TODO: trim
import jif.lang.Label;
import jif.lang.LabelUtil;
 *
 */
import civitas.crypto.ElGamalAbstractKeyShare;


class ElGamalKeyShareC extends ElGamalAbstractKeyShare {

    protected ElGamalKeyShareC(ElGamalPublicKeyC pubKey, ElGamalProofKnowDiscLogC proof) {
        super(pubKey, proof);
    }

    public boolean verify() {
        ElGamalProofKnowDiscLogC prf = (ElGamalProofKnowDiscLogC)proof;
        // the base of the prf is correct, as it is taken from params.
        ElGamalPublicKeyC K = (ElGamalPublicKeyC)pubKey;
        if (prf == null || K == null) { 
            return false;
        }
        return prf.v.equals(K.y) && prf.verify(pubKey.getParams());
    }
    /* TODO: trim
    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }

    public void toXML(Label lbl, PrintWriter s) {
        s.print("<elGamalKeyShare>");
        s.print("<pubKey>");
        if (pubKey != null) pubKey.toXML(lbl, s);
        s.print("</pubKey>");
        s.print("<proof>");
        if (proof != null) proof.toXML(lbl, s);
        s.print("</proof>");
        s.print("</elGamalKeyShare>");
    }

    public static ElGamalKeyShareC fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        Util.swallowTag(lbl, r, "elGamalKeyShare");
        Util.swallowTag(lbl, r, "pubKey");	
        ElGamalPublicKeyC pubKey = ElGamalPublicKeyC.fromXML(lbl, r);
        Util.swallowEndTag(lbl, r, "pubKey");
        Util.swallowTag(lbl, r, "proof");		
        ElGamalProofKnowDiscLogC proof = ElGamalProofKnowDiscLogC.fromXML(lbl, r);
        Util.swallowEndTag(lbl, r, "proof");
        Util.swallowEndTag(lbl, r, "elGamalKeyShare");
        return new ElGamalKeyShareC(pubKey, proof);
    }
     * 
     */
}

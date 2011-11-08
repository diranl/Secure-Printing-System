/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import java.io.*;

//import jif.lang.*;
//import civitas.common.Util;
import civitas.crypto.ElGamalAbstractKey;
import civitas.crypto.ElGamalParameters;
import civitas.crypto.ElGamalPublicKey;
import civitas.util.CivitasBigInteger;

public class ElGamalPublicKeyC extends ElGamalAbstractKey implements ElGamalPublicKey {
    public final CivitasBigInteger y;

    public ElGamalPublicKeyC(CivitasBigInteger y, ElGamalParameters params) {
        super(params);
        this.y = y;
    }

    /*TODO: trim
    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }
    public void toXML(Label lbl, PrintWriter s) {
        s.print('<'); s.print(EG_OPENING_TAG); s.print('>');

        s.print("<params>");
        if (this.params != null) {
            this.params.toXML(lbl, s);
        }
        s.print("</params>");
        s.print("<y>");
        if (this.y != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.y), lbl, s);
        s.print("</y>");
        
        s.print("</"); s.print(EG_OPENING_TAG); s.print('>');
    }
    
    public static ElGamalPublicKeyC fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        Util.swallowTag(lbl, r, EG_OPENING_TAG);
        Util.swallowTag(lbl, r, "params");
        ElGamalParameters params = ElGamalParametersC.fromXML(lbl, r);
        Util.swallowEndTag(lbl, r, "params");
        String y = Util.unescapeString(Util.readSimpleTag(lbl, r, "y"));
        Util.swallowEndTag(lbl, r, EG_OPENING_TAG);
        return new ElGamalPublicKeyC(CryptoFactoryC.stringToBigInt(y), params);
    }

    public boolean delegatesTo(Principal p) {
        return false;
    }

    public boolean equals(Principal p) {
        return equals((Object)p);
    }

    public ActsForProof findProofDownto(Principal p, Object q) {
        return null;
    }

    public ActsForProof findProofUpto(Principal p, Object q) {
        return null;
    }

    public boolean isAuthorized(Object prf, Closure c, Label l, boolean executeNow) {
        // check if prf is the matching ElGamalPrivateKey
        if (prf instanceof ElGamalPrivateKeyC) {
            ElGamalPrivateKeyC k = (ElGamalPrivateKeyC)prf;
            ElGamalParametersC param = (ElGamalParametersC)this.params;
            return y.equals(param.g.modPow(k.x, param.p));
        }
        return false;
    }
     * 
     */

    public String name() {
        return "ElGamalPublicKey-" + CryptoFactoryC.bigIntToString(y); 
    }    
    
}

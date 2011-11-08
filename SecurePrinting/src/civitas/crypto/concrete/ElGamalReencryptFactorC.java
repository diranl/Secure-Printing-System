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
//import civitas.common.Util;
import civitas.crypto.ElGamalReencryptFactor;
import civitas.util.CivitasBigInteger;

public class ElGamalReencryptFactorC implements ElGamalReencryptFactor, Serializable {
    protected final CivitasBigInteger r;
    public ElGamalReencryptFactorC(CivitasBigInteger r) {
        this.r = r;
    }
    /*
    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }
    public void toXML(Label lbl, PrintWriter s) {
        s.print("<r>");
        if (this.r != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.r), lbl, s);
        s.print("</r>");
    }


    public static ElGamalReencryptFactor fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        String s = Util.unescapeString(Util.readSimpleTag(lbl, r, "r"));
        return new ElGamalReencryptFactorC(CryptoFactoryC.stringToBigInt(s));
    }

     * 
     */
}

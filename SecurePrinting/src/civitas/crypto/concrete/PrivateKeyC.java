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
import civitas.crypto.PrivateKey;
import civitas.crypto.common.Base64;

public class PrivateKeyC implements PrivateKey {
    private static final String OPENING_TAG = "privateKey";
    final java.security.PrivateKey k;

    public PrivateKeyC(java.security.PrivateKey k) {
        this.k = k;
    }

    /*TODO:trim
    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }
    public void toXML(Label lbl, PrintWriter s) {
        s.print('<'); s.print(OPENING_TAG); s.print('>');
        CryptoFactoryC factory = CryptoFactoryC.singleton();
        byte[] bs = factory.privateKeyToBytes(k);
        Util.escapeString(Base64.encodeBytes(bs), lbl, s);
        s.print("</"); s.print(OPENING_TAG); s.print('>');
    }

    public static PrivateKey fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        String s = Util.unescapeString(Util.readSimpleTag(lbl, r, OPENING_TAG));

        byte[] bs = Base64.decode(s);
        CryptoFactoryC factory = CryptoFactoryC.singleton();
        return new PrivateKeyC(factory.privateKeyFromBytes(bs));
    }
     * 
     */
}

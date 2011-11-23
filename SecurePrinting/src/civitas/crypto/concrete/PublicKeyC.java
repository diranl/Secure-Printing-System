/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;


/*TODO: trim
import jif.lang.*;
import civitas.common.Util;
 * 
 */
import civitas.crypto.*;

public class PublicKeyC implements PublicKey {
    private static final int AUTHENTICATION_NONCE_LENGTH = 64;
    final java.security.PublicKey k;
    final String name;

    public PublicKeyC(java.security.PublicKey k, String name) {
        this.k = k;
        this.name = name;
    }

    /*TODO: trim
    public boolean delegatesTo(Principal p) {
        return false;
    }

    public boolean equals(Principal p) {
        if (p instanceof PublicKeyC) {
            PublicKeyC that = (PublicKeyC)p;
            return this.k.equals(that.k);
        }
        return false;
    }

    public String name() {
        return name;
    }

    public ActsForProof findProofDownto(Principal q, Object searchState) {
        return null;
    }

    public ActsForProof findProofUpto(Principal p, Object searchState) {
        return null;
    }

    public boolean isAuthorized(Object authPrf, Closure closure, Label lb,
            boolean executeNow) {
        if (authPrf instanceof PrivateKeyC) {
            try {
                PrivateKeyC privKey = (PrivateKeyC)authPrf;
                // check if privKey is the matching private key for this public key

                PublicKeyMsg m = CryptoFactoryC.singleton().publicKeyMsg(CryptoFactoryC.singleton().freshNonceBase64(LabelUtil.singleton().noComponents(), 
                                                                                                                     AUTHENTICATION_NONCE_LENGTH)); 
                Signature sig = CryptoFactoryC.singleton().signature(privKey, m);
                return CryptoFactoryC.singleton().publicKeyVerifySignature(this, sig, m);
                
            }
            catch (CryptoException e) {
                return false;
            }
        }
        return false;
    }


    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }

    public void toXML(Label lbl, PrintWriter s) {
        s.print('<');
        s.print(OPENING_TAG);
        s.print('>');
        s.print("<name>");
        s.print(name);
        s.print("</name>");
        s.print("<key>");
        CryptoFactoryC factory = CryptoFactoryC.singleton();
        byte[] bs = factory.publicKeyToBytes(k);
        Util.escapeString(Base64.encodeBytes(bs), lbl, s);
        s.print("</key>");
        s.print("</");
        s.print(OPENING_TAG);
        s.print('>');
    }

    public static PublicKey fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        Util.swallowTag(lbl, r, OPENING_TAG);
        String name = Util.readSimpleTag(lbl, r, "name");
        String s = Util.unescapeString(Util.readSimpleTag(lbl, r, "key"));
        Util.swallowEndTag(lbl, r, OPENING_TAG);

        byte[] bs = Base64.decode(s);
        CryptoFactoryC factory = CryptoFactoryC.singleton();
        return new PublicKeyC(factory.publicKeyFromBytes(bs), name);
    }
     * 
     */
}

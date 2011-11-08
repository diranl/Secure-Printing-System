/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;


import java.io.*;

public class ElGamalKeyPairShare implements Serializable {
    public final ElGamalParameters params;
    public final ElGamalPublicKey pubKey;
    public final ElGamalPrivateKey privKey;
    
    public ElGamalKeyPairShare(ElGamalParameters params, 
                                ElGamalPublicKey pubKey,
                                ElGamalPrivateKey privKey) {
        this.params = params;
        this.pubKey = pubKey;
        this.privKey = privKey;
    }
    /* TODO: trim
    public void toXML(label lbl, PrintWriter[] sb) {
        if (sb == null) return;
        sb.print("<elGamalKeyPairShare>");

        if (this.pubKey != null) {
            this.pubKey.toXML(lbl, sb);
        }
        if (this.privKey != null) {
            this.privKey.toXML(lbl, sb);
        }
        
        sb.print("</elGamalKeyPairShare>");
    }
    
    public static ElGamalKeyPairShare fromXML(label lbl, Reader[] r) throws (IllegalArgumentException, IOException) {
        try {
            Util.swallowTag(lbl, r, "elGamalKeyPairShare");
            ElGamalPublicKey pubKey = CryptoUtil.factory().elGamalPublicKeyFromXML(lbl, r);
            ElGamalPrivateKey privKey = CryptoUtil.factory().elGamalPrivateKeyFromXML(lbl, r);
            Util.swallowEndTag(lbl, r, "elGamalKeyPairShare");

            ElGamalParameters params = pubKey==null?null:pubKey.getParams();
            return new ElGamalKeyPairShare(params, pubKey, privKey);
        }
        catch (NullPointerException e) { throw new IllegalArgumentException(); }
    }
     * 
     */
    
}

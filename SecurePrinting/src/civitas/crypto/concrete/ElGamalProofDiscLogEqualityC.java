/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*TODO: trim
import jif.lang.Label;
import jif.lang.LabelUtil;
 * 
 */
//import civitas.common.Util;
import civitas.crypto.ElGamalParameters;
import civitas.crypto.ElGamalProofDiscLogEquality;
import civitas.util.CivitasBigInteger;

/**
 * To prove that log v = log w, where v = g_1^x and w = g_2^x, let:
 *      z = random in Z_q
 *      a = g_1^z
 *      b = g_2^z
 *      c = hash(v,w,a,b)
 *      r = (z + cx) mod q 
 * The proof is (a,b,c,r).
 * To verify, check that g_1^r = av^c (mod p) and g_2^r = bw^c (mod p). 
 */
public class ElGamalProofDiscLogEqualityC implements ElGamalProofDiscLogEquality {
    
    public final CivitasBigInteger g1;
    public final CivitasBigInteger g2;

    public final CivitasBigInteger v;
    public final CivitasBigInteger w;

    public final CivitasBigInteger a;
    public final CivitasBigInteger b;
    public final CivitasBigInteger c;
    public final CivitasBigInteger r;
    
    public ElGamalProofDiscLogEqualityC(CivitasBigInteger g1,
            CivitasBigInteger g2,
            CivitasBigInteger a,
            CivitasBigInteger v,
            CivitasBigInteger w,
            CivitasBigInteger b, CivitasBigInteger c, CivitasBigInteger r) {
        this.g1 = g1;
        this.g2 = g2;
        this.v = v;
        this.w = w;
        this.a = a;
        this.b = b;
        this.c = c;
        this.r = r;
    }
    
     public static ElGamalProofDiscLogEqualityC constructProof(ElGamalParametersC params, CivitasBigInteger g1, CivitasBigInteger g2, CivitasBigInteger x) {
        
        CryptoFactoryC factory = CryptoFactoryC.singleton();
        
        CivitasBigInteger v = g1.modPow(x, params.p);
        CivitasBigInteger w = g2.modPow(x, params.p);

        CivitasBigInteger z = CryptoAlgs.randomElement(params.q);
        CivitasBigInteger a = g1.modPow(z, params.p);
        CivitasBigInteger b = g2.modPow(z, params.p);
        
        List<CivitasBigInteger> l = new ArrayList<CivitasBigInteger>();
        l.add(v);
        l.add(w);
        l.add(a);
        l.add(b);
        CivitasBigInteger c = factory.hashToBigInt(factory.hash(l)).mod(params.q);
        
        CivitasBigInteger r = z.modAdd(c.modMultiply(x, params.q), params.q);
        
        
        return new ElGamalProofDiscLogEqualityC(g1, g2, a, v, w, b, c, r);
    }

    public boolean verify(ElGamalParameters prms) {
        if (!(prms instanceof ElGamalParametersC)) return false;
        ElGamalParametersC params = (ElGamalParametersC)prms;

        try {
            //To verify, check that g_1^r = av^c (mod p) and g_2^r = bw^c (mod p)
            return g1.modPow(r, params.p).equals(a.modMultiply(v.modPow(c, params.p), params.p)) &&
                    g2.modPow(r, params.p).equals(b.modMultiply(w.modPow(c, params.p), params.p));
        }
        catch (NullPointerException e) {
            return false;
        }
        catch (ArithmeticException e) {
            return false;
        }
    }
    /* TODO: trim
    public String toXML() {
        StringWriter sb = new StringWriter();
        toXML(LabelUtil.singleton().noComponents(), new PrintWriter(sb));
        return sb.toString();
    }
    public void toXML(Label lbl, PrintWriter s) {
        s.print("<egPrfKnwDscLog>");

        s.print("<g1>");
        if (this.g1 != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.g1), lbl, s);
        s.print("</g1>");
        s.print("<g2>");
        if (this.g2 != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.g2), lbl, s);
        s.print("</g2>");
        s.print("<v>");
        if (this.v != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.v), lbl, s);
        s.print("</v>");
        s.print("<w>");
        if (this.w != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.w), lbl, s);
        s.print("</w>");
        s.print("<a>");
        if (this.a != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.a), lbl, s);
        s.print("</a>");
        s.print("<b>");
        if (this.b != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.b), lbl, s);
        s.print("</b>");
        s.print("<c>");
        if (this.c != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.c), lbl, s);
        s.print("</c>");
        s.print("<r>");
        if (this.r != null) Util.escapeString(CryptoFactoryC.bigIntToString(this.r), lbl, s);
        s.print("</r>");
    
        s.print("</egPrfKnwDscLog>");
    }
    
    public static ElGamalProofDiscLogEquality fromXML(Label lbl, Reader r) throws IllegalArgumentException, IOException {
        Util.swallowTag(lbl, r, "egPrfKnwDscLog");
        String g1 = Util.unescapeString(Util.readSimpleTag(lbl, r, "g1"));
        String g2 = Util.unescapeString(Util.readSimpleTag(lbl, r, "g2"));
        String v = Util.unescapeString(Util.readSimpleTag(lbl, r, "v"));
        String w = Util.unescapeString(Util.readSimpleTag(lbl, r, "w"));
        String a = Util.unescapeString(Util.readSimpleTag(lbl, r, "a"));
        String b = Util.unescapeString(Util.readSimpleTag(lbl, r, "b"));
        String c = Util.unescapeString(Util.readSimpleTag(lbl, r, "c"));
        String rr = Util.unescapeString(Util.readSimpleTag(lbl, r, "r"));

        Util.swallowEndTag(lbl, r, "egPrfKnwDscLog");
        return new ElGamalProofDiscLogEqualityC(CryptoFactoryC.stringToBigInt(g1), CryptoFactoryC.stringToBigInt(g2),
                                                CryptoFactoryC.stringToBigInt(a), CryptoFactoryC.stringToBigInt(v),
                                                CryptoFactoryC.stringToBigInt(w), CryptoFactoryC.stringToBigInt(b),
                                                CryptoFactoryC.stringToBigInt(c), CryptoFactoryC.stringToBigInt(rr));
    }
     * 
     */
}
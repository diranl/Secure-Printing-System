/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import java.io.UnsupportedEncodingException;

import civitas.crypto.CryptoError;
import civitas.crypto.CryptoException;
import civitas.crypto.ElGamalMsg;
import civitas.util.CivitasBigInteger;

/**
 * An element of an El Gamal message space. 
 * A <i>message space</i> is a multiplicative group
 * over which DDH is believed to hold, and thus over
 * which ElGamal can be implemented.
 */
public class ElGamalMsgC implements ElGamalMsg {

    /**
     * The character set used to encode strings into integers.
     */
    protected static final String CHARSET = "UTF-8";

    /**
     * The message.  It is an element of a multiplicative group.
     */
    protected final CivitasBigInteger m;    

    /**
     * @param plaintext A plaintext.  This is not the same as a message.  A plaintext 
     *          is a value from some consecutive set of integers, e.g. Zq.  
     *          This constructor encodes the plaintext as a message.
     * @throws CryptoException Unless plaintext can be converted to a valid message
     *          in the message space defined by params.
     */
    protected ElGamalMsgC(CivitasBigInteger plaintext, ElGamalParametersC params) throws CryptoException {
        this.m = params.encodePlaintext(plaintext);
    }
    
    /**
     * @param message An element of a group.  This constructor does not do any
     *                verification that the element corresponds to any particular group.
     */
    public ElGamalMsgC(CivitasBigInteger message) {
        this.m = message;
    }

    /**
     * @throws CryptoException Unless i can be converted to a valid message
     *          in the message space defined by params.
     */
    protected ElGamalMsgC(int i, ElGamalParametersC params) throws CryptoException {
        this(CivitasBigInteger.valueOf(i), params);
    }

    /**
     * @throws CryptoException If s is empty or cannot be converted to a valid message
     *          in the message space defined by params.
     */
    protected ElGamalMsgC(String s, ElGamalParametersC params) throws CryptoException {
        this(stringToBigInt(s), params);
    }

    public int plaintextIntValue(ElGamalParametersC params) throws NumberFormatException, CryptoException {
        return plaintextBigIntValue(params).intValue();
    }

    public CivitasBigInteger plaintextBigIntValue(ElGamalParametersC params) throws CryptoException {
        return params.decodeMessage(m);
    }

    public String plaintextStringValue(ElGamalParametersC params) throws CryptoException {
        return bigIntToString(plaintextBigIntValue(params));
    } 
    
    public CivitasBigInteger bigIntValue() {
        return m;
    }

    protected static CivitasBigInteger stringToBigInt(String s) throws CryptoException {
        if (s.length() < 1) {
            throw new CryptoException("ElGamal messages cannot be constructed from empty strings");
        }
        try {
            return new CivitasBigInteger(s.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            // Note: UTF-8 is required to be supported by all VMs by the Java API spec.
            throw new CryptoError("Character set " + CHARSET + " is not supported by this VM");
        } catch (NumberFormatException imposs) {
            // Should only happen if s is the empty string, which we already checked for
            throw new CryptoError("Message is non-empty yet has no bytes");
        }
    }

    protected static String bigIntToString(CivitasBigInteger m) {
        try {
            return new String(m.toByteArray(), CHARSET);
        } catch (UnsupportedEncodingException e) {
            // Note: UTF-8 is required to be supported by all VMs by the Java API spec.
            throw new CryptoError("Character set " + CHARSET + " is not supported by this VM");
        }
    }

    public String toString() {
        return CryptoFactoryC.bigIntToString(m);
    }

    public boolean equals(Object o) {
        if (!(o instanceof ElGamalMsgC)) {
            return false;
        }

        ElGamalMsgC x = (ElGamalMsgC) o;
        return this.m.equals(x.m);
    }

    public int hashCode() {
        return m.hashCode();
    }
}

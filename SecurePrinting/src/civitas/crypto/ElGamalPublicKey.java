/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.Serializable;

public interface ElGamalPublicKey extends PublicKey, ElGamalKey, Serializable {
    public final static String EG_OPENING_TAG = "elGamalPublicKey";
}

/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;


public class CryptoError extends Error { 
	/**
	 * Required by the documentation for the Serializable interface.
	 * The arbitrary number is the date this class was implemented.
	 */
	private static final long serialVersionUID = 20061102L;
	
    public CryptoError(String m) { 
        super(m); 
    }
    public CryptoError(String m, Throwable cause) { 
        super(m, cause); 
    }
    public CryptoError(Throwable cause) {
        super(cause); 
    }
}
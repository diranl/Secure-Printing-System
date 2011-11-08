/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import civitas.util.CivitasBigInteger;

/**
 * A prime p of the form p = 2kq+1, where q is also prime.
 */
class SchnorrPrime {
	protected CivitasBigInteger p;
	protected CivitasBigInteger q;
	
	protected SchnorrPrime(CivitasBigInteger p, CivitasBigInteger q) {
		this.p = p;
		this.q = q;
	}
		
}

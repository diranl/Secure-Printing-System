/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto.concrete;

import java.security.SecureRandom;
import java.util.Random;

import civitas.crypto.CryptoError;
import civitas.util.CivitasBigInteger;

class CryptoAlgs { 
	protected final static CivitasBigInteger ZERO = CivitasBigInteger.ZERO;
	protected final static CivitasBigInteger ONE = CivitasBigInteger.ONE;
	protected final static CivitasBigInteger TWO = CivitasBigInteger.valueOf(2);

	// TODO: should we be requesting a specific RNG algorithm in the constructor call?
	private static Random rng = new SecureRandom(); 
	
	private CryptoAlgs() {
		// No instantiation allowed
	}
	
	public static Random rng() {
		return rng;
	}
	
	/**
	 * 2^-CERTAINTY is false positive rate for probablePrime.
	 */
	protected final static int CERTAINTY = 80; // 2^80 recommended by FIPS 186.
	
	/**
	 * @return A random safe prime p=2q+1 where |q| = length.
	 */
	protected static SchnorrPrime safePrime(int length) {
      	CivitasBigInteger possibleP, possibleQ;
    	do {
    		possibleQ = probablePrime(length);
    		possibleP = possibleQ.multiply(TWO).add(ONE); // p = 2q+1
    	} while (!isProbablePrime(possibleP)); 
    	return new SchnorrPrime(possibleP, possibleQ);
	}
	
	/**
	 * @return A random Schnorr prime p=2kq+1 where |q| = qLength
	 * and |p| = pLength.
	 */
	protected static SchnorrPrime schnorrPrime(int qLength, int pLength) {
		CivitasBigInteger p, q;
      	
      	final int NUM_P_TESTS = numPrimeTests(pLength);  
     	CivitasBigInteger l = TWO.pow(pLength);  // l = 2^pLength
      	boolean done = false;

      	int nQ = 0;
      	do {
      		nQ++;
      		q = probablePrime(qLength);
      		
      		int nP = 0;
      		do {
      			nP++;
      			
      			/* Make p a random integer of the correct length */
      			p = randomElement(l); // 0 < p < l
      			p.add(l);  // l < p < 2l 

      			/* Round p-1 down to a multiple of 2q */
      			CivitasBigInteger m = p.mod(q.multiply(TWO));  // m = p mod 2q
      			p = p.subtract(m).add(ONE);  // p = 1 (mod 2q)

      			/* Rounding may have made p too small */
      			if (p.bitLength() == pLength) {
      				if (isProbablePrime(p)) {
      					done = true;
      				} 
      			} 
      		} while (!done && nP < NUM_P_TESTS); 
      		
      		/* If we get here, either we have a Schnorr prime pair,
      		 * or we failed to find a prime p for the current q.
      		 * In the latter case, pick a new q and start over. */
      		
      	} while (!done);
      	
    	return new SchnorrPrime(p, q);
	}

  	/**
  	 * The number of attempts to generate a p from a given q.
  	 * The DSA generation algorithm uses 4096, though no explanation for
  	 * why is given.  Some playing with probabilities and the prime number
  	 * theorem reveals that, for pLength=1024, the probability of finding
  	 * a prime is 2/711 in a single try, so the probability of finding
  	 * a prime after 4096 tries is greater than 99%.  Further analysis, 
  	 * based on treating this as a geometric process, suggests that, 
  	 * the number of tries needed for > 99% success is 2^(k+2) where k = log2(pLength). 
  	 * All this ignores the fact that we've already picked a given q,
  	 * which all these probabilities should really be conditioned upon.
  	 * It also ignores the fact that many tests get wasted because the rounding
  	 * step in the DSA algorithm causes many p's to be rejected as the wrong size.
  	 */
	private static int numPrimeTests(int pLength) {
		int k = (int) Math.ceil(Math.log(pLength) / Math.log(2));
      	return (int) Math.pow(2, k+2);
	}
	
	/**
	 * @return A generator for the subgroup represented by the Schnorr prime.
	 */
	protected static CivitasBigInteger generator(SchnorrPrime sp) {
		// Implementation of step 3 of Algorithm 11.54 from Handbook of Applied Cryptography
		CivitasBigInteger g = null; 
		boolean reject = false;
		CivitasBigInteger p = sp.p;
		CivitasBigInteger negONE = p.subtract(ONE); // -1 mod p
		CivitasBigInteger twoK = p.subtract(ONE).divide(sp.q);  // (p-1)/q = 2k
		do {
			g = randomElement(p);
			g = g.modPow(twoK, p);
			reject = g.equals(ONE) || g.equals(negONE);
		} while (reject);
	
		return g; 
	}
	
	/**
	 * @return A random element from Z*_n, where n is prime, or equivalently from [1..n-1].
	 */
	public static CivitasBigInteger randomElement(CivitasBigInteger n) {
		CivitasBigInteger r = null;
		do {
			r = new CivitasBigInteger(n.bitLength(), rng());
		} while (r.equals(CivitasBigInteger.ZERO) || r.compareTo(n) >= 0); // while r >= n
		// The guard is necessary because the CivitasBigInteger constructor returns
		// a random element from [0..2^|n|-1], which is not distributed
		// the same as [1..n-1].  
		
		return r;
	}

	/**
	 * @return A number that is probably prime and is length bits long,
	 * with false positive probability <= 2^-CERTAINTY.
	 */
	protected static CivitasBigInteger probablePrime(int length) {
		return new CivitasBigInteger(length, CERTAINTY, rng()); 
	}

	/**
	 * @return Whether n is a prime, with false positive probability <= 2^-CERTAINTY.
	 */
	protected static boolean isProbablePrime(CivitasBigInteger n) {
		return n.isProbablePrime(CERTAINTY);
	}

	/**
	 * @param p A prime s.t. p = 2q+1 for any q.
	 * @return The Legendre symbol J_p(a):  1 if a \in QR_p, 0 if a mod p = 0, -1 otherwise.
	 */
	protected static int legendreSymbol(CivitasBigInteger a, CivitasBigInteger p, CivitasBigInteger q) {
		CivitasBigInteger j = a.modPow(q, p);
		if (j.equals(ONE)) {
			return 1;
		} else if (j.equals(p.subtract(ONE))) {
			return -1;
		} else if (j.equals(ZERO)) {
			return 0;
		} else {
			throw new CryptoError("Impossible Legendre symbol");
		}
	}
}

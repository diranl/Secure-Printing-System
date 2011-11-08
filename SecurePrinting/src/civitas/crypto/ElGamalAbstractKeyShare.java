/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;



public abstract class ElGamalAbstractKeyShare implements ElGamalKeyShare
{
	protected final ElGamalPublicKey pubKey;
	protected final ElGamalProofKnowDiscLog proof;

	protected ElGamalAbstractKeyShare(ElGamalPublicKey pubKey, ElGamalProofKnowDiscLog proof) {
		this.pubKey = pubKey;
		this.proof = proof;
	}

	public ElGamalPublicKey pubKey() { return pubKey; }
	public ElGamalProofKnowDiscLog proof() { return proof; }

	public boolean equals(Object o) {
		if (!(o instanceof ElGamalAbstractKeyShare)) {
			return false;
		}

		ElGamalAbstractKeyShare z = (ElGamalAbstractKeyShare) o;
		return this.pubKey.equals(z.pubKey)
			&& this.proof.equals(z.proof);
	}
   
}

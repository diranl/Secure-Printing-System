/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.Serializable;

public abstract class ElGamalAbstractKey implements ElGamalKey, Serializable {
    public final ElGamalParameters params;
    
    protected ElGamalAbstractKey(ElGamalParameters params) {
        this.params = params;
    }
    
    public ElGamalParameters getParams() {
        return params;
    }
    
    public boolean equals(Object o) {
    	if (!(o instanceof ElGamalAbstractKey)) {
    		return false;
    	}
    	
    	ElGamalAbstractKey x = (ElGamalAbstractKey) o;
    	return this.params.equals(x.params);
    }
}

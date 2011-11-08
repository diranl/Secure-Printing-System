/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

public interface MessageDigest {
    void update(byte[] bs);
    void update(byte[] bs, boolean constBytes);
    void update(byte b);
    void update(int i);
    void update(long l);
    void update(String s);
    void update(char[] cbuf, int off, int len);
    
    // Get the digest. Note that we assume the digest
    // does not reveal any information about data used
    // to update it, i.e. it is a byte{this}const[],
    // not a byte{L}const[].
    byte[] digest();
}

/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.crypto;

import java.io.IOException;
import java.io.Writer;

public class MessageDigestWriter extends Writer {
    private final Writer w;
    private final MessageDigest md;

    public MessageDigestWriter(Writer w, MessageDigest md) {
        this.w = w;
        this.md = md;
    }
    
    public void close() throws IOException {
        if (w != null) w.close();
    }

    public void flush() throws IOException {
        if (w != null) w.flush();
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (w != null) w.write(cbuf, off, len);
        if (md != null) {
            md.update(cbuf, off, len);
        }
    }
    
    
    public void main(String[] args) { 
        // test this thing
        
    }

}

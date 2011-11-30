package secureprinting.mixnet;

import secureprinting.Matrix;
import java.io.Serializable;
import java.util.BitSet;

/**
 * Message class: generic parent of CipherMessage and PlaintextMessage
 */
public abstract class Message implements Serializable {
  public final int rowSize;
  public final int colSize;
  public final int length;
  
  protected static final int BYTE_TO_BIT = 8; 
  protected static final int INT_TO_BIT = 32;
  protected static final int WHITE_PXL = (255<<24)|(255<<16)|(255<<8)|255;
  protected static final int BLACK_PXL = 255<<24;

  public abstract void print();
  public abstract boolean equals(Object obj);

  public Message(int rowSize, int colSize, int length) {
    this.rowSize = rowSize;
    this.colSize = colSize;
    this.length = length;
  }
}

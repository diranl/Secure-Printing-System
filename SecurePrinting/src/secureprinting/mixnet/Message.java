package secureprinting.mixnet;

import secureprinting.visualcrypto.Matrix;
import java.io.Serializable;
import java.util.BitSet;

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

  public static Matrix toMatrix(int width, int height, int[] rgbArray) {
    Matrix matrix = new Matrix(height, width);
    for (int idx=0, rowIdx=0, colIdx=0; idx<rgbArray.length; idx++, colIdx++) {
      if (colIdx != 0 && colIdx % width == 0) { colIdx = 0; rowIdx++; }
      matrix.set(rowIdx, colIdx, (rgbArray[idx] == WHITE_PXL) ? 0 : 1);
    }
    return matrix;
  }

  public static BitSet fromIntArray(int[] intArray) {
    BitSet bits = new BitSet();
    for (int i=0; i<intArray.length * INT_TO_BIT; i++) {
      if ((intArray[intArray.length - i/INT_TO_BIT - 1] & (i%INT_TO_BIT)) > 0) { bits.set(i); }
    }
    return bits;
  }
  public static BitSet fromByteArray(byte[] bytes) {
    BitSet bits = new BitSet();
    for (int i=0; i<bytes.length * BYTE_TO_BIT; i++) {
      if ((bytes[bytes.length - i/BYTE_TO_BIT - 1] & (1 << (i%BYTE_TO_BIT))) > 0) { bits.set(i); }
    }
    return bits;
  }

  public static byte[] fromBitSet(BitSet bits) {
    byte[] bytes = new byte[bits.length()/BYTE_TO_BIT+1];
    for (int i=0; i<bits.length(); i++) {
      if (bits.get(i)) { bytes[bytes.length - i/BYTE_TO_BIT -1] |= 1 << (i%BYTE_TO_BIT); }
    }
    return bytes;
  }
}

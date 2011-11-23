package secureprinting.mixnet;

import java.util.BitSet;

public class PlaintextMessage extends Message {
  protected final int key;
  private final BitSet translation;

  public PlaintextMessage(int key, int rowSize, int colSize) {
    super(rowSize, colSize, rowSize*colSize);
    this.key = key;
    this.translation = new BitSet(length);
  }
  public PlaintextMessage(int key, BitSet translation, int rowSize, int colSize) {
    super(rowSize, colSize, rowSize*colSize);
    this.key = key;
    this.translation = translation;
  }

  public void set(int idx) {
    translation.set(idx);
  }

  public boolean get(int idx) {
    return translation.get(idx);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof PlaintextMessage)) return false;

    PlaintextMessage plaintxt = (PlaintextMessage)obj;
    if (key != plaintxt.key)                return false;
    return translation.equals(plaintxt.translation);
  }

  public void print() {
    System.out.println("Key: " + key);
    for (int idx=0; idx<length; idx++) {
      if (idx != 0 && idx % colSize == 0) System.out.println();
      System.out.print((translation.get(idx) ? 1 : 0) + " ");
    }
    System.out.println();
  }
}

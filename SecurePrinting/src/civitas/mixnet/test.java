package civitas.mixnet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Vector;
import java.util.Collections;
import java.util.List;

public class test {
  private static final int BYTE_TO_BIT = 8; 
  private static final int INT_TO_BIT = 32;

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

  public static BitSet fromIntArray(int[] intArray) {
    BitSet bits = new BitSet();
    for (int i=0; i<intArray.length * INT_TO_BIT; i++) {
      if ((intArray[intArray.length - i/INT_TO_BIT - 1] & (i%INT_TO_BIT)) > 0) { bits.set(i); }
    }
    return bits;
  }

  public static void permute(List<Integer> lst, Permutation permutation) {
    List<Integer> swapped = permutation.getList();
    List<Integer> initial  = Permutation.range(permutation.size);
    int pos;
    for (int idx=0; idx<lst.size(); idx++) {
      pos = findPositionOf(swapped.get(idx), initial);
      Collections.swap(lst, idx, pos);
      Collections.swap(initial, idx, pos);
    }
  }

  public static int findPositionOf(int elem, List<Integer> lst) {
    int pos=0;
    for (; pos<lst.size(); pos++) {
      if (lst.get(pos) == elem) break;
    }
    return pos;
  }
  
  public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchProviderException {
    /* TEST: from/to array
    int a = 12;
    byte[] bytes = new byte[] {
      (byte) (a >>> 24),
      (byte) (a >>> 16),
      (byte) a };
    String val = new String(bytes);
    System.out.println(bytes.toString());
    BitSet bits = fromByteArray(bytes);
    byte[] newBytes = fromBitSet(bits);
    val = new String(newBytes);
    System.out.println(new String(newBytes));
    System.out.println("bits:"+bits);

    int[] intArray = new int[] { a };
    System.out.println(intArray);
    BitSet ints = fromIntArray(intArray);
    System.out.println("ints:" + ints);
    */

    /* TEST: shuffle 
    Vector v = new Vector();
    v.add("1");
    v.add("2");
    v.add("3");
    v.add("4");
    v.add("5");
    System.out.println("Before swaping, Vector contains : " + v);
    Collections.swap(v,0,4);
    System.out.println("After swaping, Vector contains : " + v);
    */

    /* TEST: matrix reader, from txtfile 
    try {
      Matrix mat = Parser.parse("n.txt");
      mat.print();
    }  catch (IOException ex) {
      ex.printStackTrace();
    }
    */
    
    /* TEST: permutation */
    Permutation permutation = new Permutation(5);
    List<Integer> lst = Permutation.range(5);
    System.out.println("initial lst: " + lst);
    System.out.print("permutation: ");
    permutation.print();
    permute(lst, permutation);
    System.out.println("permuted lst: " + lst);

  }
}

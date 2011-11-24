package secureprinting.mixnet;

import civitas.crypto.ElGamalKeyPairShare;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalParametersC;
import civitas.crypto.concrete.ElGamalReencryptFactorC;
import civitas.util.CivitasBigInteger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Vector;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.google.gson.Gson;
import secureprinting.Controller;
import secureprinting.Matrix;
import secureprinting.Matrix;
import secureprinting.mixnet.Permutation;
import secureprinting.visualcrypto.BasisMatrix;
import secureprinting.visualcrypto.Bitmap;

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
    List<Integer> initial = Permutation.range(permutation.size);
    int pos;
    for (int idx=0; idx<lst.size(); idx++) {
      pos = findPositionOf(swapped.get(idx), initial);
      System.out.println("  Swapping - idx:" + idx + ", pos:" + pos);
      Collections.swap(lst, idx, pos);
      Collections.swap(initial, idx, pos);
      System.out.println("  ...lst: " + lst);
      System.out.println("  ...initial: " + initial);
    }
  }

  public static int findPositionOf(int elem, List<Integer> lst) {
    int pos=0;
    for (; pos<lst.size(); pos++) {
      if (lst.get(pos) == elem) break;
    }
    return pos;
  }
  
  public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, IOException {
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
    
    /* TEST: permutation 
    Permutation permutation = new Permutation(5);
    List<Integer> lst = Permutation.range(5);
    System.out.println("initial lst: " + lst);
    System.out.print("permutation: ");
    permutation.print();
    permute(lst, permutation);
    System.out.println("permuted lst: " + lst);
    
    Permutation randomPrm = new Permutation(5);
    Permutation invPrm = permutation.invert(randomPrm);
    List<Integer> modLst = Permutation.range(5);
    System.out.print("\n\nrandom perm: ");
    randomPrm.print();
    permute(modLst, randomPrm);
    System.out.print("..random permuted: ");
    System.out.println(modLst+"\n");
    System.out.print("inverted perm: ");
    invPrm.print();
    permute(modLst, invPrm);
    System.out.print("..inverse permuted: ");
    System.out.println(modLst);
    */

    /* TEST: random matrix
    Matrix rand = Matrix.random(3,3);
    rand.print();
    */

    /* TEST: basis matrix, matrix augmentation
    Matrix rand = Matrix.random(4,4);
    BasisMatrix basis = new BasisMatrix(3, BasisMatrix.DEFAULT_METHOD);
    Matrix augmented = rand.augment(basis, 0);
    System.out.println("Random matrix:");
    rand.print();
    System.out.println("Basis matrix:");
    basis.print();
    System.out.println("Augmented matrix:");
    augmented.print();
    augmented.write("augmented");
    System.out.println("written to augmented.bmp");
    Matrix matrix = new Matrix(3,4);
    matrix.set(1,3,1);
    matrix.print();
    */

    /* TEST: share printing debugging*/
    BasisMatrix basis = new BasisMatrix(4);
    System.out.println("basis: ");
    basis.print();
    Matrix share = Matrix.random(10, 10);
    System.out.println("share:" );
    share.print();
    System.out.println("augemented share:" );
    Matrix augmented = share.augment(basis, 0);
    augmented.print();
    augmented.write("random");

    /* TEST: arraylist
    try {
      List< List<Integer> > lst = new ArrayList< List<Integer> >(3);
      lst.add(new ArrayList<Integer>(1));
      lst.get(0).add(50);
      lst.get(0).add(20);
      System.out.println(lst.get(0).size());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    */

    /* TEST: permutation inverse
    Permutation original = new Permutation(5);
    Permutation shadow = new Permutation(5);
    Permutation inverse = original.invert(shadow);
    System.out.print("original: ");
    original.print();
    System.out.print("shadow: "); 
    shadow.print();
    System.out.print("inverse: ");
    inverse.print();
    */

    /* TEST: FIXME random factor inverse 
    ElGamalReencryptFactorC r_o = new ElGamalReencryptFactorC(CivitasBigInteger.valueOf(23));
    ElGamalReencryptFactorC r_1 = new ElGamalReencryptFactorC(CivitasBigInteger.valueOf(50));
    ElGamalReencryptFactorC r_2 = r_o.subtract(r_1, CivitasBigInteger.valueOf(58));
    ElGamalReencryptFactorC sum = r_1.add(r_2, CivitasBigInteger.valueOf(58));
    sum.print();
    */
    
    /*
    Random rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
    for (int idx=0; idx<100; idx++) {
      if (idx != 0 && idx % 10 == 0) System.out.println();
      System.out.print(rand.nextInt(2)+" ");
    }
    */

    /* TEST: object hashing, serialization 
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)factory.generateElGamalParameters();
    ElGamalKeyPairShare share = factory.generateKeyPairShare(params);
    TranslationTable initialTbl = TranslationTable.initTable(share);
    TranslationTable copiedTbl = new TranslationTable(initialTbl);
    Gson gson = new Gson();

    byte[] init = initialTbl.toByteArray();
    byte[] copied = copiedTbl.toByteArray();
    System.out.println("init: " + factory.hash(init));
    System.out.println("copy: " + factory.hash(copied));
    System.out.println(init.equals(copied));

    System.out.println("init hashcode: " + initialTbl.hashCode());
    System.out.println("copy hashcode: " + copiedTbl.hashCode());
    System.out.println("init secure hash: " + factory.hash(ByteBuffer.allocate(4).putInt(initialTbl.hashCode()).array()));
    System.out.println("copy secure hash: " + factory.hash(ByteBuffer.allocate(4).putInt(copiedTbl.hashCode()).array()));
    String i = gson.toJson(initialTbl);
    String c = gson.toJson(copiedTbl);
    System.out.println("init JSON: " + i);
    System.out.println("copy JSON: " + c);
    System.out.println("equality: " + i.equals(c));

    TranslationTable iTbl = gson.fromJson(i, TranslationTable.class);
    TranslationTable cTbl = gson.fromJson(c, TranslationTable.class);
    System.out.println("tbl equality: " + (iTbl.equals(cTbl)));
    */

    /* TEST: String appending
    String a = "a";
    String b = "b";
    System.out.println(a+b);
    */

    /* TEST: object part 2; commitment
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)factory.generateElGamalParameters();
    ElGamalKeyPairShare share = factory.generateKeyPairShare(params);
    TranslationTable initialTbl = TranslationTable.initTable(share);
    TranslationTable copiedTbl = new TranslationTable(initialTbl);
    Gson gson = new Gson();
    String i = gson.toJson(initialTbl);
    String c = gson.toJson(copiedTbl);
    System.out.println("hash i: "+factory.hash(i));
    System.out.println("hash c: "+factory.hash(c));
    */

    /* TEST: random ID
    String id = Controller.nextId();
    System.out.println("ID: " + id);
    */
  }
}

package secureprinting.mixnet;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Permutation implements Serializable {

  private final List<Integer> swapped;
  //TODO: implement a mechanism for reseeding/or reinitializing 
  // as described by http://www.cigital.com/justiceleague/2009/08/14/proper-use-of-javas-securerandom/
  protected final SecureRandom srnd; 
  protected final int size;

  public Permutation(int size) throws NoSuchAlgorithmException, NoSuchProviderException {
    this.swapped = range(size);
    this.srnd = SecureRandom.getInstance("SHA1PRNG", "SUN");
    this.size = size;
    Collections.shuffle(swapped, srnd);
  }
  public Permutation(int size, SecureRandom srnd) {
    this.swapped = range(size);
    this.srnd = srnd;
    this.size = size;
    Collections.shuffle(swapped, srnd);
  }
  public Permutation(List<Integer> permutation, int size) {
    this.swapped = permutation;
    this.size = size;
    this.srnd = null;
  }

  // Generate a map f: {0,1,...} -> {...}
  // which dictates the mapping of elements of an ordered list based on the given permutation
  // For e.g.: permutation={3,2,1,4,0}
  // f({0,1,2,3,4}) = {4,2,1,0,3}, that is, 0->4, 1->2, 2->1, 3->0, 4->3
  public static List<Integer> toMap(List<Integer> swapped) {
    List<Integer> map = new ArrayList<Integer>(swapped.size());
    for (int idx=0; idx<swapped.size(); idx++) map.add(findPositionOf(idx, swapped));
    return map;
  }

  protected static int findPositionOf(int elem, List<Integer> lst) {
    int pos=0;
    for (; pos<lst.size(); pos++) {
      if (lst.get(pos) == elem) break;
    }
    return pos;
  }
  
  protected Permutation invert(Permutation shadowPrm) {
    //TODO: sanity check for size equality
    //For an original permutation prm_o and shadow permutation prm_s
    // find prm_inv s.t. prm_inv(prm_s(lst)) = prm_o(lst)
    int[] inversePrm = new int[size];
    for (int startPos=0, midPos, endPos; startPos<size; startPos++) {
      midPos = findPositionOf(startPos, shadowPrm.swapped);
      endPos = findPositionOf(startPos, this.swapped);
      inversePrm[midPos] = endPos;
    }
    List<Integer> _swapped = new ArrayList<Integer>(size);
    for (int idx=0; idx<size; idx++) _swapped.add(inversePrm[idx]);
    return (new Permutation(toMap(_swapped), size));
  }

  public int get(int idx) {
    return swapped.get(idx);
  }

  public List<Integer> getList() {
    return swapped;
  }

  public SecureRandom getRnd() {
    return srnd;
  }

  public static List<Integer> range(int length) {
    List<Integer> range = new ArrayList<Integer>(length);
    for (int i=0;i<length;i++) { range.add(i); }
    return range;
  }

  public static List<Integer> range(int start, int stop) {
    List<Integer> range = new ArrayList<Integer>(stop-start);
    for (int i=0;i<stop-start;i++) { range.add(start+i); }
    return range;
  }

  public void print() {
    for (int num : swapped) System.out.print(num + " ");
    System.out.println();
  }

  /**
   * Serializes the object into a JSON equivalence using the GSON project by Google
   */
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public void toFile(String filename) {
    try {
      FileOutputStream fos = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(this);
      out.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  public static Permutation fromFile(String filename) {
    Permutation permutation = null;
    try {
      FileInputStream fis = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(fis);
      permutation = (Permutation)in.readObject();
      in.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return permutation;
  }
}

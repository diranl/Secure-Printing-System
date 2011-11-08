package civitas.mixnet;

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

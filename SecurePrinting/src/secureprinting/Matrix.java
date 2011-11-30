package secureprinting;

import com.google.gson.Gson;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Random;
import secureprinting.visualcrypto.BasisMatrix;
import secureprinting.visualcrypto.Bitmap;

public class Matrix {
  public final int rowSize;
  public final int colSize;
  protected final BitSet matrix;

  public static final boolean OVERWRITE = true;
  public static final boolean NOOVERWRITE = false;

  private final int TYPE_XOR = 0;
  private final int TYPE_OR  = 1;

  public Matrix(int rowSize, int colSize) {
    this.rowSize = rowSize;
    this.colSize = colSize;
    this.matrix = new BitSet(rowSize*colSize); /*Default entries: 0*/
  } 

  public Matrix(int rowSize, int colSize, BitSet set) {
    this.rowSize = rowSize;
    this.colSize = colSize;
    this.matrix = set;
  }

  public Matrix(Matrix copy) {
    this.rowSize = copy.rowSize;
    this.colSize = copy.colSize;
    this.matrix = (BitSet)copy.matrix.clone();
  }

  /**
   * Retrieve elem at M[rowIdx, colIdx]
   */
  public boolean get(int rowIdx, int colIdx) {
    return matrix.get(rowIdx*colSize + colIdx);
  }

  /**
   * Retrieve elem at M[idx]
   * <Note>this is possible due to the inherent BitSet representation</Note>
   */
  public boolean get(int idx) {
    return matrix.get(idx);
  }

  public BitSet extractRow(int rowIdx) {
    BitSet row = new BitSet(colSize);
    for (int colIdx=0; colIdx<colSize; colIdx++) {
      if (this.get(rowIdx, colIdx)) row.set(colIdx);
    }
    return row;
  }

  public void set(int rowIdx, int colIdx, int bit) {
    if (bit == 1) { 
      matrix.set(rowIdx*colSize + colIdx);
    }
  }
  public void set(int idx) {
    matrix.set(idx);
  }

  /**
   * Printout of matrix row by row 
   */
  public void print() {
    for (int i=0; i<rowSize; i++) {
      for (int j=0; j<colSize; j++) {
        System.out.print((this.get(i, j) ? 1 : 0) + " ");
      }
      System.out.print('\n');
    }
  }
  
  /**
   * Generate random matrix of size  @param rowSize by @param colSize
   */
  public static Matrix random(int rowSize, int colSize) throws NoSuchAlgorithmException, NoSuchProviderException {
    Random rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
    Matrix matrix = new Matrix(rowSize, colSize);
    for (int i=0; i<rowSize; i++) {
      for (int j=0; j<colSize; j++) {
        matrix.set(i, j, rand.nextInt(2));
      }
    }
    return matrix;
  }

  /**
   * Generate Visual Crypto augmentation 
   * @param basis contains the squaring method and the number of parties
   */
  public Matrix augment(BasisMatrix basis, int partyIdx) {
    Matrix _whitePxl = basis.retrieve(partyIdx, 0);
    Matrix _blackPxl = basis.retrieve(partyIdx, 1);
    int[] pxlDim = basis.pxlDim();
    Matrix augmented = new Matrix(rowSize*pxlDim[0], colSize*pxlDim[1]);

    for (int rowIdx=0; rowIdx<rowSize; rowIdx++) {
      for (int colIdx=0; colIdx<colSize; colIdx++) {
        if (this.get(rowIdx, colIdx)) augmented.insert(rowIdx*pxlDim[0], colIdx*pxlDim[1], _blackPxl);
        else augmented.insert(rowIdx*pxlDim[0], colIdx*pxlDim[1], _whitePxl);
      }
    }
    return augmented;
  }
  
  /**
   * Inserts matrix  @param A starting at position [rowStart, colStart]  
   */
  private void insert(int rowStart, int colStart, Matrix A) {
    for (int rowIdx=0; rowIdx<A.rowSize; rowIdx++) {
      for (int colIdx=0; colIdx<A.colSize; colIdx++) {
        this.set(rowStart+rowIdx, colStart+colIdx, A.get(rowIdx, colIdx) ? 1 : 0);
      }
    }
  }

  /**
   * Writes matrix input a bitmap file
   */
  public void write(String filename) {
    try {
      Bitmap bmp = new Bitmap(this.toRGBArray(), colSize, rowSize);
      bmp.write(filename + Bitmap.BMP_SUFFIX);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Converts matrix input an RGB array format
   * <Note>RGB array needed for java.awt.image.BufferedImage</Note>
   */
  public int[] toRGBArray() {
    int[] rgbArray = new int[rowSize*colSize];
    for (int idx=0, rowIdx=0; rowIdx<rowSize; rowIdx++) {
      for (int colIdx=0; colIdx<colSize; colIdx++) {
        rgbArray[idx] = this.get(rowIdx, colIdx) ? Bitmap.BLACK_PXL : Bitmap.WHITE_PXL;
        idx++;
      }
    }
    return rgbArray;
  }

  /**
   * Serializes the object into a JSON equivalence using the GSON project by Google
   */
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public Matrix xor(Matrix B, boolean overwrite) {
    return operation(TYPE_XOR, B, overwrite);
  }

  public Matrix or(Matrix B, boolean overwrite) {
    return operation(TYPE_OR, B, overwrite);
  }
  
  /**
   * Helper function for matrix operations
   */
  private Matrix operation(int type, Matrix B, boolean overwrite) {
    Matrix A = this;
    if (B.rowSize != A.rowSize || B.colSize != A.colSize) throw new RuntimeException("Illegal matrix dimensions.");

    Matrix C = this;
    if (overwrite == OVERWRITE) {
      if      (type == TYPE_XOR) this.matrix.xor(B.matrix);
      else if (type == TYPE_OR)  this.matrix.or(B.matrix);
      else throw new RuntimeException("Illegal operation type: " + type);
    } else {
      BitSet bitSet = (BitSet) this.matrix.clone();
      if      (type == TYPE_XOR) bitSet.xor(B.matrix);
      else if (type == TYPE_OR)  bitSet.or(B.matrix);
      else throw new RuntimeException("Illegal operation type: " + type);
      C = new Matrix(rowSize, colSize, bitSet);
    }
    return C;
  }
}


package civitas.mixnet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Random;

public class Matrix {
  public final int rowSize;
  public final int colSize;
  private final BitSet matrix;

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

  public boolean get(int rowIdx, int colIdx) {
    return matrix.get(rowIdx*rowSize + colIdx);
  }
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
      matrix.set(rowIdx*rowSize + colIdx);
    }
  }
  public void set(int idx) {
    matrix.set(idx);
  }

  public void print() {
    // Print out of matrix row by row
    for (int i=0; i<rowSize; i++) {
      for (int j=0; j<colSize; j++) {
        System.out.print((this.get(i, j) ? 1 : 0) + " ");
      }
      System.out.print('\n');
    }
  }
  
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
  
  private void insert(int rowStart, int colStart, Matrix A) {
    for (int rowIdx=0; rowIdx<A.rowSize; rowIdx++) {
      for (int colIdx=0; colIdx<A.colSize; colIdx++) {
        this.set(rowStart+rowIdx, colStart+colIdx, A.get(rowIdx, colIdx) ? 1 : 0);
      }
    }
  }

  public void write(String filename) {
    try {
      Bitmap bmp = new Bitmap(this.toRGBArray(), colSize, rowSize);
      bmp.write(filename + ".bmp");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

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

  /*
  private Matrix _operation(int type, Matrix B, boolean overwrite) {
    Matrix A = this;
    if (B.row != A.row || B.col != A.col) throw new RuntimeException("Illegal matrix dimensions.");
    
    Matrix C = (overwrite == true) ? A : new Matrix(this.row, this.col);
    for (int i=0; i<this.row; i++) {
      for (int j=0; j<this.col; j++) {
        if (type == TYPE_XOR)      C.matrix[i][j] = A.matrix[i][j] ^ B.matrix[i][j];
        else if (type == TYPE_OR)  C.matrix[i][j] = A.matrix[i][j] | B.matrix[i][j];
        else throw new RuntimeException("Invalid type parameter for _operation");
      }
    }
    return C;
  }

  public Matrix XOR(Matrix B, boolean overwrite) {
    return _operation(TYPE_XOR, B, overwrite);
  }
  
  public static Matrix XOR(ArrayList<Matrix> matrices) {
    int[] dim = {matrices.get(0).row, matrices.get(0).col};
    Matrix result = new Matrix(dim[0], dim[1]);
    for (Matrix mat : matrices) result.XOR(mat, true);
    return result;
  }
  
  public static Matrix XOR(Matrix A, ArrayList<Matrix> matrices) {
    Matrix result = new Matrix(A.row, A.col);
    result.XOR(A, true);
    for (Matrix mat : matrices) result.XOR(mat, true);
    return result;
  }
  
  public Matrix OR(Matrix B, boolean overwrite) {
    return _operation(TYPE_OR, B, overwrite);
  }

  public static Matrix OR(ArrayList<Matrix> matrices) {
    int[] dim = {matrices.get(0).row, matrices.get(0).col};
    Matrix result = new Matrix(dim[0], dim[1]);
    for (Matrix mat : matrices) result.OR(mat, true);
    return result;
  }


  public int[] toRGBArray() {
    int[] rgbArray = new int[row*col];
    for (int i=0, idx=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        rgbArray[idx] = (matrix[i][j] == 0) ? Bitmap.WHITE_PXL : Bitmap.BLACK_PXL;
        idx++;
      }
    }
    return rgbArray;
  }

  public void insert(int rowStart, int colStart, Matrix A) {
    for (int rowIdx=0; rowIdx<A.row; rowIdx++) {
      for (int colIdx=0; colIdx<A.col; colIdx++) {
        this.matrix[rowStart+rowIdx][colStart+colIdx] = A.matrix[rowIdx][colIdx];
      }
    }
  }
  */
}


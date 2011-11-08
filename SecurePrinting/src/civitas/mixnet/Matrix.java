package civitas.mixnet;

import java.util.BitSet;

public class Matrix {
  protected final int rowSize;
  protected final int colSize;
  private BitSet[] matrix;

  private final int TYPE_XOR = 0;
  private final int TYPE_OR  = 1;

  public Matrix(int rowSize, int colSize) {
    this.rowSize = rowSize;
    this.colSize = colSize;
    matrix = new BitSet[rowSize]; /*Default entries: 0*/
  } 

  public Matrix(int rowSize, int colSize, BitSet[] set) {
    this.rowSize = rowSize;
    this.colSize = colSize;
    matrix = set;
  }

  public boolean get(int rowIdx, int colIdx) {
    BitSet bitset = matrix[rowIdx];
    return bitset.get(colIdx);
  }

  public void set(int rowIdx, int colIdx, int bit) {
    if (bit == 1) { 
      BitSet bitset = matrix[rowIdx];
      bitset.set(rowIdx);
    }
  }

  public void print() {
    // Print out of matrix row by row
    for (int i=0; i<rowSize; i++) {
      for (int j=0; j<colSize; j++) {
        System.out.print((matrix[i].get(j) ? 1 : 0) + " ");
      }
      System.out.print('\n');
    }
  }
  
  /*
  public static Matrix random(int row, int col) {
    // TODO: use SecureRandom
    Random rand = new Random();
    Matrix A = new Matrix(row, col);
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        A.matrix[i][j] = rand.nextInt(2);
      }
    }
    return A;
  }
  
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


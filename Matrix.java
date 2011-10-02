import java.util.Random;
import java.util.ArrayList;

public class Matrix {
  public final int row;
  public final int col;
  public short[][] matrix;

  private final int TYPE_XOR = 0;
  private final int TYPE_OR  = 1;

  public Matrix(int row, int col) {
    this.row = row;
    this.col = col;
    matrix = new short[row][col];   /*Default entries: 0*/
  } 

  public static Matrix random(int row, int col) {
    Random rand = new Random();
    Matrix A = new Matrix(row, col);
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        A.matrix[i][j] = (short)rand.nextInt(2);
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

  public void print() {
    // Print out of matrix row by row
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.print('\n');
    }
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
}


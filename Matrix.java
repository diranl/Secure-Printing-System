/* Inspired from: http://introcs.cs.princeton.edu/java/95linear/Matrix.java.html */
import java.util.Random;
import java.util.ArrayList;

public class Matrix {
  public final int row;
  public final int col;
  public int[][] matrix;

  private final int TYPE_XOR = 0;
  private final int TYPE_OR  = 1;

  public Matrix(int row, int col) {
    this.row = row;
    this.col = col;
    matrix = new int[row][col];   /*Default values: 0*/
  } 

  public static Matrix random(int row, int col) {
    Random rand = new Random();
    Matrix A = new Matrix(row, col);
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        A.matrix[i][j] = rand.nextInt(2);
      }
    }
    return A;
  }
  
  private Matrix _op(int type, Matrix B) {
    Matrix A = this;
    if (B.row != A.row || B.col != A.col) throw new RuntimeException("Illegal matrix dimensions.");
    Matrix C = new Matrix(this.row, this.col);
    for (int i=0; i<this.row; i++) {
      for (int j=0; j<this.col; j++) {
        if (type == TYPE_XOR)      C.matrix[i][j] = A.matrix[i][j] ^ B.matrix[i][j];
        else if (type == TYPE_OR)  C.matrix[i][j] = A.matrix[i][j] | B.matrix[i][j];
        else throw new RuntimeException("Invalid type parameter for _op");
      }
    }
    return C;
  }

  public Matrix XOR(Matrix B) {
    return _op(TYPE_XOR, B);
  }
  
  public static Matrix XOR(ArrayList<Matrix> matrices) {
    int[] dim = {matrices.get(0).row, matrices.get(0).col};
    Matrix result = new Matrix(dim[0], dim[1]);
    for (Matrix mat : matrices) result = result.XOR(mat);
    return result;
  }
  
  public static Matrix XOR(Matrix A, ArrayList<Matrix> matrices) {
    Matrix result = A;
    for (Matrix mat : matrices) result = result.XOR(mat);
    return result;
  }
  
  public Matrix OR(Matrix B) {
    return _op(TYPE_OR, B);
  }

  public static Matrix OR(ArrayList<Matrix> matrices) {
    int[] dim = {matrices.get(0).row, matrices.get(0).col};
    Matrix result = new Matrix(dim[0], dim[1]);
    for (Matrix mat : matrices) result = result.OR(mat);
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
}


/* Inspired from: http://introcs.cs.princeton.edu/java/95linear/Matrix.java.html */
import java.util.Random

final public class Matrix {
  public final int row;
  public final int col;
  public final short[][] matrix;

  private final int TYPE_XOR = 0;
  private final int TYPE_OR  = 1;

  public Matrix(int row, int col) {
    this.row = row;
    this.col = col;
    matrix = new short[row][col];
  } 

  public static Matrix random(int row, int col) {
    Random rand = new Random();
    Matrix A = Matrix(row, col);
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        A.matrix[i][j] = randomGenerator.nextInt(1);
      }
    }
    return A;
  }
  
  private Matrix _op(int type) {
    Matrix A = this;
    if (B.row != A.row || B.col != A.col) throw new RuntimeException("Illegal matrix dimensions.");
    Matrix C = Matrix(row, col);
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        if (type == TYPE_XOR)      C.matrix[i][j] = A.matrix[i][j] ^ B.matrix[i][j];
        else if (type == TYPE_XOR) C.matrix[i][j] = A.matrix[i][j] | B.matrix[i][j];
        else throw new RuntimeException("Invalid type parameter for _op");
      }
    }
    return C;
  }

  public Matrix xor(Matrix B) {
    return _op(TYPE_XOR);
  }
  
  public Matrix or(Matrix B) {
    return _op(TYPE_OR);
  }
}

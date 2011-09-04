import java.lang.Math
import java.math.BigInteger
import java.util.Random

class Crypto {
  public static short[][] basisMatrix(int n) {
    /*TODO: dynamic basis generation
    int rows = n;
    BigInteger cols = (BigInteger)pow(2, n-1);
    short[][] basis = new short[rows][cols];
    */
    //FIXME: hardcoded basis for two party
    short[][] basis = {{0, 1}, 
                       {0, 1}};
    return basis;
  }

  public static short[][] generateBitmap(short bit, int party) {
    short[][] matrix = basisMatrix(2);
    short[] basisRow = matrix[party];
    if (bit == 1) {
      // Perform conjugate
      for (int i=0; i<basisRow.length; i++) {
        basisRow[i] = (basisRow[i] == 0) ? 1:0;
      }
    }
    // FIXME: generalize pixel square to higher dimensions
    // Hardcoded for two-party
    short[][] bitmap = new short[2][2];
    bitmap[0] = basisRow;
    bitmap[1] = basisRow;
    return bitmap;
  }

  public static void printBitmap(short[][] bitmap) {
    
  }

  public static short[][] generateSecret() {
    short[][] secret = {{0,0,1,1,0,0},
                        {0,1,1,1,1,0},
                        {1,1,0,0,1,1},
                        {1,1,0,0,1,1},
                        {0,1,1,1,1,0},
                        {0,0,1,1,0,0}};
    return secret;
  }

  public static short[][] generateMatrix(int row, int col) {
    short[][] matrix = short[row][col];
    Random randomGenerator = new Random();
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        matrix[i][j] = randomGenerator.nextInt(1);
      }
    }
    return matrix;
  }

  public static short[][] matrixXOR(short[][] alpha, short[][] beta) {
    assert alpha.length == beta.length;
    assert alpha[0].length == beta[0].length;
    int row = alpha.length;
    int col = alpha[0].length;

    short[][] ret = new short[
    for (int i=0; i<row; i++) {
      for (int j=0; j<col; j++) {
        ret = alpha[i][j] ^ beta[i][j];   
      }
    }
    return ret;
  }
  
  public static void main(String args[]) {
    short[][] alpha = generateMatrix(6, 6);
    short[][] secret = generateSecret();
    short[][] beta = matrixXOR(alpha, secret);
  }
}

import java.io.IOException;

class SecurePrinting {
  public static Matrix basisMatrix(int n) {
    //TODO: Generalize to higher dimensions
    Matrix basis = new Matrix(2,2);
    int[][] tmp = {{0,1}, 
                   {0,1}};
    basis.matrix = tmp;
    return basis;
  }

  public static Matrix generatePixel(int bit, int party, int partyNum) {
    Matrix basis = basisMatrix(2);
    int[] row = basis.matrix[party];
    if (bit == 1) {
      for (int i=0; i<row.length; i++) {
        row[i] = row[i] ^ 1;
      }
    }
    Matrix pixel = new Matrix(row.length, row.length);

    pixel.matrix[0] = (int[])row.clone();
    pixel.matrix[1] = (int[])row.clone();
    return pixel;
  }

  public static Matrix generatePixelMap(Matrix secret, int party, int partyNum) {
    //FIXME: hard-coded for two-party squaring 
    Matrix pixelMap = new Matrix(secret.row * 2/*hard-coded for two party*/, secret.col * 2/*hard-coded for two party*/);
    for (int i=0; i<secret.row; i++) {
      for (int j=0; j<secret.col; j++) {
        Matrix pixel = generatePixel(secret.matrix[i][j], party, partyNum);
        // Merging of matrices using pixel
        for (int pxlRow=0, pxlMapRow=i*2; pxlRow<pixel.row; pxlRow++) {
          for (int pxlCol=0, pxlMapCol=j*2; pxlCol<pixel.col; pxlCol++) {
            pixelMap.matrix[pxlMapRow][pxlMapCol] = pixel.matrix[pxlRow][pxlCol];
          }
        }
      }
    }
    return pixelMap;
  }

  public void generatePermutations(int n, int m) {}

  public static Matrix generateSecret() {
    Matrix secret = new Matrix(6,6);
    int[][] tmp = {{0,0,1,1,0,0},
                   {0,1,1,1,1,0},
                   {1,1,0,0,1,1},
                   {1,1,0,0,1,1},
                   {0,1,1,0,1,1},
                   {0,0,1,1,0,0}};
    secret.matrix = tmp;
    return secret;
  }
  
  public static void main(String args[]) throws IOException {
    Matrix secret = generateSecret();
    Matrix alpha = Matrix.random(secret.row, secret.col);
    Matrix beta = alpha.XOR(secret);

    Matrix alphaPxlMap = generatePixelMap(alpha, 0, 2);
    Matrix betaPxlMap = generatePixelMap(beta, 1, 2);
    /*DEBUG
    alphaPxlMap.print();
    System.out.println("\n");
    betaPxlMap.print();
    */
    Matrix secretPxlMap = alphaPxlMap.XOR(betaPxlMap);
    Matrix overlayedPxlMap = alphaPxlMap.OR(betaPxlMap);

    Bitmap alphaBmp = new Bitmap(alphaPxlMap);
    Bitmap betaBmp = new Bitmap(betaPxlMap);
    Bitmap secretBmp = new Bitmap(secretPxlMap);
    Bitmap overlayedBmp = new Bitmap(overlayedPxlMap);
    
    alphaBmp.write("alpha.bmp");
    betaBmp.write("beta.bmp");
    secretBmp.write("secret.bmp");
    overlayedBmp.write("overlayed.bmp");
  }
}

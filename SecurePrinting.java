import java.io.IOException;

class SecurePrinting {

  public static Matrix generatePixelMap(Matrix secret, int partyIdx, int partyNum) {
    BasisMatrix basis = new BasisMatrix(partyNum);
    int[] dim = basis.pxlDim();

    Matrix pixelMap = new Matrix(secret.row * dim[0], secret.col * dim[1]);
    for (int i=0; i<secret.row; i++) {
      for (int j=0; j<secret.col; j++) {
        Matrix pixel = basis.retrieve(partyIdx, secret.matrix[i][j]);
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

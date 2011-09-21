import java.io.IOException;
import java.util.ArrayList;

class SecurePrinting {

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
  
  public static void main(String args[]) throws IOException {
    int partyNum = 0;

    switch (args.length) {
      case 0: partyNum = 2;                         break;
      case 1: partyNum = Integer.parseInt(args[0]); break;
      default: 
        System.err.println("Usage: java SecurePrinting [partyNum] --partyNum is an optional param specifying the number of shares generated");
        System.exit(1);
    }

    Matrix secret = generateSecret();

    /* Generate n-1 random shares and create last share as result of bitwise XOR*/
    ArrayList<Matrix> shares = new ArrayList<Matrix>();
    for (int i=0; i<partyNum-1; i++) shares.add(Matrix.random(secret.row, secret.col));
    shares.add(Matrix.XOR(secret, shares));

    /* Generate the pixel maps corresponding to all n shares*/
    ArrayList<Matrix> pxlmaps = new ArrayList<Matrix>();
    int idx = 0;
    for (Matrix share : shares) {
      pxlmaps.add(generatePixelMap(share, idx, partyNum));
      idx++;
    }
    
    Matrix secretPxlMap = Matrix.XOR(pxlmaps);
    Matrix overlayedPxlMap = Matrix.OR(pxlmaps);

    //Generating bitmaps for all shares
    idx = 0;
    for (Matrix pxlmap : pxlmaps)  {
      Bitmap bmp = new Bitmap(pxlmap);
      bmp.write("share-" + idx + ".bmp");
      idx++;
    }
    //Generating bitmaps for secret and overlayed pixel maps
    Bitmap secretBmp = new Bitmap(secretPxlMap);
    secretBmp.write("secret.bmp");
    Bitmap overlayedBmp = new Bitmap(overlayedPxlMap);
    overlayedBmp.write("overlayed.bmp");
  }
}

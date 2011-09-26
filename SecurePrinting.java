import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

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

  public static Matrix generatePixelMap(Matrix secret, int partyIdx, int partyNum, String method) {
    BasisMatrix basis = new BasisMatrix(partyNum, method);
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
  
  
  private static void usage() {
    System.err.println(
        "Usage: java SecurePrinting [filename [partyNum [method]]]\n"
      + "  - filename: name of monochrome BMP file to be used as secret\n"
      + "  - partyNum: number of printers used\n"
      + "  - method: pixel squaring method");
    System.exit(1);
  }

  private static void checkFilename(String filename) {
    File file = new File(filename);
    if (!file.exists()) {
      System.err.println("ERROR: file " + filename + "does not exist");
      usage();
    }
  }

  private static void checkMethod(String method) {
    if (method != BasisMatrix.SQUARE_COMPLETION && method != BasisMatrix.NON_SQUARE && method != BasisMatrix.RECTANGLE_COMPLETE) {
      System.err.println("ERROR: method " + method + "does not exist");
      usage();
    }
  }

  /* Main driver for multi-party printing 
   * Usage: java SecurePrinting [filename [partyNum [method]]]
   * filename: name of a monochrome BMP file to be used as secret
   * partyNum: number of printers used
   * method: pixel squaring method 
   */
  public static void main(String args[]) throws IOException {
    int partyNum = 2;
    String filename = "";
    String method = BasisMatrix.RECTANGLE_COMPLETE;

    switch (args.length) {
      case 0: 
        break;
      case 1: 
        filename = args[0];
        checkFilename(filename);
        break;
      case 2:
        filename = args[0];
        partyNum = Integer.parseInt(args[1]); 
        checkFilename(filename);
        break;
      case 3:
        filename = args[0];
        partyNum = Integer.parseInt(args[1]); 
        method = args[2];
        checkFilename(filename);
        checkMethod(method);
        break;
      default: 
        usage();
    }

    Matrix secret = generateSecret();
    if (filename != "") secret = Bitmap.read(filename);

    /* Generate n-1 random shares and create last share as result of bitwise XOR*/
    ArrayList<Matrix> shares = new ArrayList<Matrix>();
    for (int i=0; i<partyNum-1; i++) shares.add(Matrix.random(secret.row, secret.col));
    shares.add(Matrix.XOR(secret, shares));

    /* Generate the pixel maps corresponding to all n shares*/
    ArrayList<Matrix> pxlmaps = new ArrayList<Matrix>();
    int idx = 0;
    for (Matrix share : shares) {
      pxlmaps.add(generatePixelMap(share, idx, partyNum, method));
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

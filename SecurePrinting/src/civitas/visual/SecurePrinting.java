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
                   {0,1,1,1,1,0},
                   {0,0,1,1,0,0}};
    secret.matrix = tmp;
    return secret;
  }

  public static void generatePixelMap(Matrix share, int partyIdx, int partyNum, String method) throws IOException {
    BasisMatrix basis = new BasisMatrix(partyNum, method);
    int[] pxlDim = basis.pxlDim();
    Matrix pixel = new Matrix(pxlDim[0], pxlDim[1]);

    int height = share.row * pxlDim[0], width = share.col * pxlDim[1];
    Matrix pixelMap = new Matrix(height, width);
    for (int rowIdx=0; rowIdx<share.row; rowIdx++) {
      for (int colIdx=0; colIdx<share.col; colIdx++) {
        basis.retrieve(partyIdx, share.matrix[rowIdx][colIdx], pixel);
        pixelMap.insert(rowIdx*pxlDim[0], colIdx*pxlDim[1], pixel);
      }
    }

    Bitmap bmp = new Bitmap(pixelMap.toRGBArray(), width, height);
    bmp.write("share-" + partyIdx + ".bmp");
  }

  public static void generateOutcome(ArrayList<Matrix> shares, int partyNum, String method, Matrix secret) throws IOException {
    BasisMatrix basis = new BasisMatrix(partyNum, method);
    int[] pxlDim = basis.pxlDim();
    Matrix pixel = new Matrix(pxlDim[0], pxlDim[1]);

    int height = secret.row * pxlDim[0], width = secret.col * pxlDim[1];
    Matrix secretPxlMap = new Matrix(height,width);
    Matrix overlayedPxlMap = new Matrix(height,width);

    ArrayList<Matrix> pixels = new ArrayList<Matrix>();
    for (int rowIdx=0; rowIdx<secret.row; rowIdx++) {
      for (int colIdx=0; colIdx<secret.col; colIdx++) {
        pixels.clear();
        for (int partyIdx=0; partyIdx<partyNum; partyIdx++) { pixels.add(basis.retrieve(partyIdx, shares.get(partyIdx).matrix[rowIdx][colIdx])); }
        secretPxlMap.insert(rowIdx*pxlDim[0], colIdx*pxlDim[1], Matrix.XOR(pixels));
        overlayedPxlMap.insert(rowIdx*pxlDim[0], colIdx*pxlDim[1], Matrix.OR(pixels));
      }
    }

    Bitmap secretBmp = new Bitmap(secretPxlMap.toRGBArray(), width, height);
    secretBmp.write("secret.bmp");

    Bitmap overlayedBmp = new Bitmap(overlayedPxlMap.toRGBArray(), width, height);
    overlayedBmp.write("overlayed.bmp");
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
      System.err.println("ERROR: file " + filename + " does not exist");
      usage();
    }
  }

  private static void checkMethod(String method) {
    if (!method.equals(BasisMatrix.SQUARE_COMPLETION) && !method.equals(BasisMatrix.NON_SQUARE) && !method.equals(BasisMatrix.RECTANGLE_COMPLETE)) {
      System.err.println("ERROR: method " + method + " does not exist");
      System.err.println("Expecting: " + BasisMatrix.SQUARE_COMPLETION + ", " + BasisMatrix.NON_SQUARE + ", or " + BasisMatrix.RECTANGLE_COMPLETE);
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
    int idx = 0;
    for (Matrix share : shares) {
      generatePixelMap(share, idx, partyNum, method);
      idx++;
    }

    generateOutcome(shares, partyNum, method, secret);
  }
}

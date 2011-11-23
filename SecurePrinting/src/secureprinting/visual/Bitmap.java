package secureprinting.visual;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.util.Arrays;

//TODO: refactor into its own package
public final class Bitmap { 
  private final int width;
  private final int height;
  private final int[] rgbArray;

  public static final int WHITE_PXL = (255<<24)|(255<<16)|(255<<8)|255;
  public static final int BLACK_PXL = 255<<24;
  
  public Bitmap(Matrix matrix) {
    height = matrix.rowSize;
    width = matrix.colSize;
    rgbArray = matrix.toRGBArray();
  }

  public Bitmap(int[] rgbArray, int width, int height) {
    this.width = width;
    this.height = height;
    this.rgbArray = rgbArray;
  }

  public void write(String filename) throws IOException {
    BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY); 
    bf.setRGB(0, 0, width, height, rgbArray, 0, width);
    ImageIO.write(bf, "bmp", new File(filename));
  }

  /*
  public static Matrix read(String filename) throws IOException {
    BufferedImage image = ImageIO.read(new File(filename));
    int width = image.getWidth();
    int height = image.getHeight();
    int[] rgbArray = image.getRGB(0, 0, width, height, null, 0, width);
    return toMatrix(width, height, rgbArray);
  }

  public static Matrix toMatrix(int width, int height, int[] rgbArray) {
    Matrix imgData = new Matrix(height, width);
    for (int idx=0, rowIdx=0, colIdx=0; idx<rgbArray.length; idx++, colIdx++) {
      if (colIdx != 0 && colIdx % width == 0) { colIdx = 0; rowIdx++; }
      imgData.matrix[rowIdx][colIdx] = (rgbArray[idx] == WHITE_PXL) ? 0 : 1;
    }
    return imgData;
  }
  */

  public void print() throws IOException {
    BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY); 
    bf.setRGB(0, 0, width, height, rgbArray, 0, width);
    System.out.println(bf.toString());
  }
}


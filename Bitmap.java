import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.util.Arrays;

public class Bitmap { 
  private final int width;
  private final int height;
  private final int[] imgData;
  
  public Bitmap(Matrix bitArray) {
    width = bitArray.row;
    height = bitArray.col;
    imgData = new int[width*height];
    int idx = 0;
    for (int i=0; i<bitArray.row; i++) {
      for (int j=0; j<bitArray.col; j++) {
        imgData[idx] = (bitArray.matrix[i][j] == 0) ? (255<<24)|(255<<16)|(255<<8)|255 : 255<<24;
        idx++;
      }
    }
  }

  public void write(String filename) throws IOException {
    BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY); 
    bf.setRGB(0, 0, width, height, imgData, 0, width);
    ImageIO.write(bf, "bmp", new File(filename));
    //FIXME: debugging statement
    System.out.println(filename + " - width:" + width + ", height:" + height);
    System.out.println(Arrays.toString(imgData));
  }

  public static Matrix read(String filename) throws IOException {
    BufferedImage image = ImageIO.read(new File(filename));
    int width = image.getWidth();
    int height = image.getHeight();
    int[] array = image.getRGB(0, 0, width, height, null, 0, width);
    //FIXME: debugging statement 
    System.out.println(filename);
    System.out.println(Arrays.toString(array));
    return null;
  }

  public void print() throws IOException {
    BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY); 
    bf.setRGB(0, 0, width, height, imgData, 0, width);
    System.out.println(bf.toString());
  }
}


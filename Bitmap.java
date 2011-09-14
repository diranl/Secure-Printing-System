import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;

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
    BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
    bf.setRGB(0, 0, width, height, imgData, 0, width);
    ImageIO.write(bf, "bmp", new File(filename));
  }
}


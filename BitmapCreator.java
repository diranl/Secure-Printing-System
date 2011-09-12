import java.util.*;
import java.io.*;

public class BitmapCreator 
{
  private List < String > bitMapFileHeader = new ArrayList< String >();
  private List < String > bitMapInfoHeader = new ArrayList< String >();
  private List < String > rgbQuadArray = new ArrayList < String >();
  private List < String > pixels = new ArrayList < String >();

  private final static int BMP_WIDTH = 4;
  private final static int BMP_HEIGHT = 4;
  private final static int BMP_NUM_BITS_FOR_COLORS = 24;
  private final static int BMP_PALETTE_SIZE = (int)Math.pow(2.0, (double)BitmapCreator.BMP_NUM_BITS_FOR_COLORS);
  private final static int BMP_IMAGE_SIZE = BMP_HEIGHT * BMP_WIDTH;
  private final static int BMP_FILE_SIZE = BMP_IMAGE_SIZE + 40 + 14;

  public BitmapCreator()
  {
    setUpFileHeader();
    setUpInfoHeader();
    //setUpRgbQuadArray();
    addPixels(null);
  }

  public void setUpInfoHeader() 
  {
    //for bi size
    bitMapInfoHeader.add(convertIntToBytes(40, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    //bitMapInfoHeader.add(convertIntToBytes(40, 8));

    //for bi width
    bitMapInfoHeader.add(convertIntToBytes(BitmapCreator.BMP_WIDTH, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    //bitMapInfoHeader.add(convertIntToBytes(BitmapCreator.BMP_WIDTH, 8));

    //for bi height
    bitMapInfoHeader.add(convertIntToBytes(BitmapCreator.BMP_HEIGHT, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    //bitMapInfoHeader.add(convertIntToBytes(BitmapCreator.BMP_HEIGHT, 8));

    //for bi planes 
    bitMapInfoHeader.add(convertIntToBytes(1, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    //bitMapInfoHeader.add(convertIntToBytes(1, 8));

    //for bi bit count
    bitMapInfoHeader.add(convertIntToBytes(BitmapCreator.BMP_NUM_BITS_FOR_COLORS, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    //bitMapInfoHeader.add(convertIntToBytes(BitmapCreator.BMP_NUM_BITS_FOR_COLORS, 8));

    //for bi compression
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));

    //for bi size image
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));

    //for bi x pixels per meter
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));

    //for bi y pixels per meter
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));

    //for bi clr used
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));

    //for bi clr important
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
  }

  public void setUpFileHeader() 
  {
    //for bfType- should be BM for bitmap- two byte equivalent of ASCII 'B''M'
    bitMapFileHeader.add(convertIntToBytes(77, 8)); //'M' reverse order??
    bitMapFileHeader.add(convertIntToBytes(66, 8)); //'B'
    //bitMapFileHeader.add(convertIntToBytes(19778, 16)); 

    //for bfSize- size of file in bytes
    bitMapFileHeader.add(convertIntToBytes(BitmapCreator.BMP_FILE_SIZE, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    //bitMapFileHeader.add(convertIntToBytes(BitmapCreator.BMP_FILE_SIZE, 8));

    //for bfReserved 1 and 2- unused should be 0
    bitMapFileHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));

    bitMapFileHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));

    //for bfOffBits- offset into the real data
    bitMapFileHeader.add(convertIntToBytes(118, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    bitMapInfoHeader.add(convertIntToBytes(0, 8));
    //bitMapFileHeader.add(convertIntToBytes(118, 8));
  }

  public void setUpRgbQuadArray()
  {
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
    rgbQuadArray.add("11111111111111111111111100000000");
    rgbQuadArray.add("00000000000000000000000000000000");
  }
  public void addPixels(List < Byte > ps)
  {
    for(int i = 0;i < BitmapCreator.BMP_HEIGHT;i++)
    {
      for(int j = 0;j < BitmapCreator.BMP_WIDTH;j++)
      {
        //
        //pixels.add(convertIntToBytes((i * j) % 16, 4));
        pixels.add(convertIntToBytes(128, 8));
        pixels.add(convertIntToBytes(128, 8));
        pixels.add(convertIntToBytes(128, 8));
      }
    }
  }

  public void writeToFile(String filename)
  {
    try 
    {
      //create a File object representing the file
      File outputFile = new File(filename);
      FileOutputStream out = new FileOutputStream(outputFile);

      StringBuffer bits = new StringBuffer(); 

      System.out.print("File Header: ");
      //add the bit map header file bits
      for(int i = 0;i < bitMapFileHeader.size();i++)
      {
        System.out.print(bitMapFileHeader.get(i) + " ");
        bits.append(bitMapFileHeader.get(i));
      }
      System.out.println();

      System.out.print("Info Header: ");
      //add the bit map info header bits
      for(int i = 0;i < bitMapInfoHeader.size();i++)
      {
        bits.append(bitMapInfoHeader.get(i));
        System.out.print(bitMapInfoHeader.get(i) + " ");
      }
      System.out.println();

      /*
         System.out.print("Quad array: ");
      //add the bit map info header bits
      for(int i = 0;i < rgbQuadArray.size();i++)
      {
      System.out.print(rgbQuadArray.get(i) + " ");
      bits.append(rgbQuadArray.get(i));
      }
      System.out.println();
       */

      System.out.print("Pixels: ");
      //add the bit map info header bits
      for(int i = 0;i < pixels.size();i++)
      {
        System.out.print(pixels.get(i) + " ");
        bits.append(pixels.get(i));
      }
      System.out.println();

      String bits2 = bits.toString();

      for(int i = 0;i < bits2.length();i = i + 8)
      {
        StringBuffer b = new StringBuffer();

        for(int j = 0;j < 8;j++)
        {
          b.append(bits2.charAt(i + j));
        }

        System.out.println(Integer.parseInt(b.toString(), 2));
        out.write(Integer.parseInt(b.toString(), 2));
      }

      out.close();
    }
    catch (Exception e) 
    {
      e.printStackTrace();
    }

  }
  public String convertIntToBytes(int number, int numBits)
  {
    //list of bytes for the given int
    String retVal = null;

    //add 31 0's and the binary string of the number passed in (the number passed in
    //will produce at least one additional bit to make 32 total bits)
    StringBuffer numberBuffer = new StringBuffer("0000000000000000000000000000000" + Integer.toBinaryString(number));

    //reverse the buffer, convert to a string, and pull out the first numBits bits
    String revBits = numberBuffer.reverse().toString().substring(0, numBits);

    //create a new buffer with the reverse bits
    numberBuffer = new StringBuffer(revBits);

    //reverse them back
    numberBuffer.reverse();

    //create a string from them
    retVal = numberBuffer.toString();

    return retVal;
  }
  public static void main(String[] args) 
  {
    BitmapCreator bm = new BitmapCreator();
    bm.writeToFile("markfile.bmp");

  }
}

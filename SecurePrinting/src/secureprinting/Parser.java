package secureprinting;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;
import secureprinting.visualcrypto.Bitmap;

public final class Parser {

  /**
   * Parses CSV file into a matrix instance
   */
  public static Matrix parseCsv(String filename) throws FileNotFoundException {
    File file = new File(filename);
    Scanner scanner = new Scanner(new FileReader(file));
    List<BitSet> list = new ArrayList<BitSet>();
    int colSize = 0;
    try {
      while (scanner.hasNextLine()) {
        colSize = process(scanner.nextLine(), list);
      }
    } finally {
      scanner.close();
    }
    int rowSize = list.size(); 
    return new Matrix(rowSize, colSize, flatten(list, rowSize, colSize));
  }
  private static int process(String line, List<BitSet> list) {
    BitSet bitset = new BitSet();
    Scanner scanner = new Scanner(line);
    scanner.useDelimiter(",");
    int val, idx=0;
    while (scanner.hasNext()) {
      val = scanner.nextInt();
      if (val == 1) bitset.set(idx);
      idx++;
    }
    list.add(bitset);
    return idx;
  }
  
  /**
   * Converts a list of BitSet into a single BitSet
   */
  private static BitSet flatten(List<BitSet> matrix, int rowSize, int colSize) {
    BitSet bitSet = new BitSet(rowSize*colSize);
    for (int rowIdx=0; rowIdx<rowSize; rowIdx++) {
      for (int colIdx=0; colIdx<colSize; colIdx++) {
        if (matrix.get(rowIdx).get(colIdx)) bitSet.set(rowIdx*colSize + colIdx);
      }
    }
    return bitSet;
  }

  public static void main(String args[]) throws IOException {
    if (args.length == 0) System.exit(1);
    Matrix overlay = null, input;
    for (int idx=0; idx<args.length; idx++) {
      input = Bitmap.read(args[idx]);
      if (idx == 0) overlay = input;
      else          overlay.or(input, Matrix.OVERWRITE);
    }
    System.out.println("overlay:");
    overlay.print();
    overlay.write("overlay");
    System.out.println("Overlay written.");
  }
}

package secureprinting.mixnet;

import secureprinting.Matrix;
import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

public class Parser {

  public static Matrix parse(String filename) throws FileNotFoundException {
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
    return (new Matrix(rowSize, colSize, flatten(list, rowSize, colSize)));
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
  private static BitSet flatten(List<BitSet> matrix, int rowSize, int colSize) {
    BitSet bitSet = new BitSet(rowSize*colSize);
    for (int idx=0, rowIdx=0; rowIdx<rowSize; rowIdx++) {
      for (int colIdx=0; colIdx<colSize; colIdx++) {
        if (matrix.get(rowIdx).get(colIdx)) bitSet.set(rowIdx*colSize + colIdx);
      }
    }
    return bitSet;
  }
}

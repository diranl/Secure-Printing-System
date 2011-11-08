package civitas.mixnet;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

public class Parser {

  public static Matrix parse(String filename) throws FileNotFoundException {
    File file = new File(filename);
    Scanner scanner = new Scanner(new FileReader(file));
    List list = new ArrayList();
    int colSize = 0;
    try {
      while (scanner.hasNextLine()) {
        colSize = process(scanner.nextLine(), list);
      }
    } finally {
      scanner.close();
    }
    int rowSize = list.size(); 

    BitSet[] matrix = new BitSet[rowSize];
    list.toArray(matrix);
    return (new Matrix(rowSize, colSize, matrix));
  }

  private static int process(String line, List list) {
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
}

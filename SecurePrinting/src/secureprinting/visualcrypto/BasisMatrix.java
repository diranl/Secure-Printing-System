package secureprinting.visualcrypto;

import secureprinting.Matrix;
import java.util.BitSet;
import java.util.Stack;

public class BasisMatrix extends Matrix {
  public static final String SQUARE_COMPLETION = "-squarecomplete";
  public static final String NON_SQUARE = "-nonsquare";
  public static final String RECTANGLE_COMPLETE = "-rectcomplete";

  public static final String DEFAULT_METHOD = RECTANGLE_COMPLETE;

  private String method;

  public BasisMatrix(int n, String method) {
    super(n, (int)Math.pow(2, n-1));
    this.method = method;
    permute();
  }
  public BasisMatrix(int n) {
    this(n, DEFAULT_METHOD);
  }

  private void permute() {
    Stack<Integer> stack = new Stack<Integer>();

    /* Loop through with powers of two increments to generate the permuations
     * according to the definition of basis matrices
     */
    int colIdx = 1; // Keep first column of zeros, start inserting into second 
    for (int inserts=2 /*Start at 2 inserts*/; inserts<=super.rowSize; inserts*=2) {
      int pivot;

      /* Step 1: Initialization
       * initialize stack with pivot values, i.e. push positions 0 to insert-1 
       * aggregate first set of permutations into basis matrix, i.e. 
       * fixing 1's to initial pivots and varying the last 1 down 
       * the remaining positions
       */
      for (pivot=0; pivot<inserts-1; pivot++) { stack.push(pivot); } // Init stack

      for (int variant=pivot; variant<super.rowSize; variant++) {
        super.set(variant, colIdx, 1);
        for (Integer rowIdx : stack) super.set(rowIdx, colIdx, 1);
        colIdx++;
      }

      /* Step 2: Recusing by changing the pivot positions
       * uses stack to simulate recursion
       */
      while (true) {
        /* Remove values from the stack until a pivot value, which after increment,
         * leaves enough room for the rest of the inserts OR until the stack is empty
         */
        while (true) {
          if   (stack.empty() || (stack.peek()+1) + (inserts-stack.size()) /*position of smallest insert*/ < super.rowSize) break;
          else stack.pop();
        }
        if (stack.empty()) break;

        /* Increment top pivot of the stack AND push in the subsequent pivots*/
        pivot = stack.pop();
        pivot++;
        stack.push(pivot);
        while (stack.size() < inserts-1) {
          pivot++;
          stack.push(pivot);
        }

        /* Aggregate permutations into basis matrix*/
        for (int variant=pivot+1; variant<super.rowSize; variant++) {
          super.set(variant, colIdx, 1);
          for (Integer rowIdx : stack) super.set(rowIdx, colIdx, 1);
          colIdx++;
        }
      }
    }
  }

  private Matrix rectcomplete(BitSet row) {
    int _rowSize = 1, _colSize = super.colSize;
    for (; _rowSize*2 <= _colSize/2; _rowSize*=2, _colSize/=2) {}
    Matrix matrix = new Matrix(_rowSize, _colSize);

    if (_rowSize == _colSize) {
      // Perfect square 
      for (int rowIdx=0, colIdx=0, idx=0; idx<row.size(); colIdx++, idx++) {
        if (colIdx == _colSize) { colIdx = 0; rowIdx++; }
        matrix.set(rowIdx, colIdx, row.get(idx) ? 1 : 0);
      }
    } else {
      // Needs squaring through doubling
      for (int rowIdx=0, colIdx=0, idx=0; idx<row.size()*2; colIdx++, idx++) {
        if (colIdx == _colSize)  { colIdx = 0; rowIdx++; }
        matrix.set(rowIdx, colIdx, row.get(idx % row.size()) ? 1 : 0);
      }
    }
    return matrix;
  }

  private Matrix squarecomplete(BitSet row) {
    int[] dim = pxlDim();
    int _rowSize = dim[0], _colSize = dim[1];
    Matrix matrix = new Matrix(_rowSize, _colSize);

    int rowIdx=0, colIdx=0;
    /* Transcribe the array elements into matrix */
    for (int idx=0; idx<row.size(); idx++, colIdx++) {
      if (colIdx == _colSize) { colIdx = 0; rowIdx++; }
      matrix.set(rowIdx, colIdx, row.get(idx) ? 1 : 0);
    }
    /* Insert padding if necessary */
    for (int idx=row.size(); idx<_rowSize*_colSize; idx++, colIdx++) {
      if (colIdx == _colSize) { colIdx = 0; rowIdx++; }
      matrix.set(rowIdx, colIdx, 1);
    }
    return matrix;
  }

  public Matrix retrieve(int rowIdx, int bit) {
    BitSet row = super.extractRow(rowIdx);
    if (bit == 1) row.flip(0, row.size());

    if (method.equals(SQUARE_COMPLETION)) {
      return squarecomplete(row);
    } else {
      // DEFAULT SETTING
      return rectcomplete(row);
    }
  }

  public int[] pxlDim() {
    int _rowSize = 1, _colSize = super.colSize;
    if (this.method.equals(RECTANGLE_COMPLETE)) {
      for (; _rowSize*2 <= _colSize/2; _rowSize*=2, _colSize/=2) {}
      if (_rowSize != _colSize) _rowSize *= 2;
    } else if (this.method.equals(SQUARE_COMPLETION)) {
      int sideLen = (int)Math.sqrt(super.colSize);
      if (sideLen*sideLen < super.colSize) sideLen += 1;
      _rowSize = _colSize = sideLen;
    }
    int[] dim = {_rowSize, _colSize};
    return dim;
  }
}

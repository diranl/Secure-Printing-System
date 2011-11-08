import java.util.Stack;

public class BasisMatrix extends Matrix {
  public static final String SQUARE_COMPLETION = "-squarecomplete";
  public static final String NON_SQUARE = "-nonsquare";
  public static final String RECTANGLE_COMPLETE = "-rectcomplete";

  private String method;

  public BasisMatrix(int n, String method) {
    super(n, (int)Math.pow(2, n-1));
    this.method = method;
    permute();
  }

  private void permute() {
    Stack<Integer> stack = new Stack<Integer>();

    /* Loop through with powers of two increments to generate the permuations
     * according to the definition of basis matrices
     */
    int colIdx = 1; // Keep first column of zeros, start inserting into second 
    for (int inserts=2 /*Start at 2 inserts*/; inserts<=super.row; inserts*=2) {
      int pivot;

      /* Step 1: Initialization
       * initialize stack with pivot values, i.e. push positions 0 to insert-1 
       * aggregate first set of permutations into basis matrix, i.e. 
       * fixing 1's to initial pivots and varying the last 1 down 
       * the remaining positions
       */
      for (pivot=0; pivot<inserts-1; pivot++) { stack.push(pivot); } // Init stack

      for (int variant=pivot; variant<super.row; variant++) {
        super.matrix[variant][colIdx] = 1;
        for (Integer rowIdx : stack) super.matrix[rowIdx][colIdx] = 1;
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
          if   (stack.empty() || (stack.peek()+1) + (inserts-stack.size()) /*position of smallest insert*/ < super.row) break;
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
        for (int variant=pivot+1; variant<super.row; variant++) {
          super.matrix[variant][colIdx] = 1;
          for (Integer rowIdx : stack) super.matrix[rowIdx][colIdx] = 1;
          colIdx++;
        }
      }
    }
  }

  private void _rectcomplete(int[] row, Matrix container) {
    int rowSize = 1, colSize = super.col;
    for (; rowSize*2 <= colSize/2; rowSize*=2, colSize/=2) {}

    if (rowSize == colSize) {
      // Perfect square 
      for (int rowIdx=0, colIdx=0, idx=0; idx<row.length; colIdx++, idx++) {
        if (colIdx == colSize) { colIdx = 0; rowIdx++; }
        container.matrix[rowIdx][colIdx] = row[idx];
      }
    } else {
      // Needs squaring through doubling
      for (int rowIdx=0, colIdx=0, idx=0; idx<row.length*2; colIdx++, idx++) {
        if (colIdx == colSize)  { colIdx = 0; rowIdx++; }
        container.matrix[rowIdx][colIdx] = row[idx % row.length];
      }
    }
  }

  private void _squarecomplete(int[] row, Matrix container) {
    int[] dim = pxlDim();
    int rowSize = dim[0], colSize = dim[1];

    int rowIdx=0, colIdx=0;
    /* Transcribe the array elements into matrix */
    for (int idx=0; idx<row.length; idx++, colIdx++) {
      if (colIdx == colSize) { colIdx = 0; rowIdx++; }
      container.matrix[rowIdx][colIdx] = row[idx];
    }
    /* Insert padding if necessary */
    for (int idx=row.length; idx<rowSize*colSize; idx++, colIdx++) {
      if (colIdx == colSize) { colIdx = 0; rowIdx++; }
      container.matrix[rowIdx][colIdx] = 1;
    }
  }

  public void retrieve(int rowIdx, int bit, Matrix container) {
    int[] row = super.matrix[rowIdx].clone();
    if (bit == 1) { for (int i=0; i<row.length; i++) row[i] = row[i] ^ 1; }

    if (method.equals(SQUARE_COMPLETION)) {
      _squarecomplete(row, container);
    } else {
      _rectcomplete(row, container);
    }
  }

  public Matrix retrieve(int rowIdx, int bit) {
    int[] dim = this.pxlDim();
    Matrix container = new Matrix(dim[0], dim[1]);

    int[] row = super.matrix[rowIdx].clone();
    if (bit == 1) { for (int i=0; i<row.length; i++) row[i] = row[i] ^ 1; }

    if (method.equals(SQUARE_COMPLETION)) {
      _squarecomplete(row, container);
    } else {
      _rectcomplete(row, container);
    }
    return container;
  }

  public int[] pxlDim() {
    int rowSize = 1, colSize = super.col;
    if (this.method.equals(RECTANGLE_COMPLETE)) {
      for (; rowSize*2 <= colSize/2; rowSize*=2, colSize/=2) {}
      if (rowSize != colSize) rowSize *= 2;
    } else if (this.method.equals(SQUARE_COMPLETION)) {
      int sideLen = (int)Math.sqrt(super.col);
      if (sideLen*sideLen < super.col) sideLen += 1;
      rowSize = colSize = sideLen;
    }
    int[] dim = {rowSize, colSize};
    return dim;
  }
}

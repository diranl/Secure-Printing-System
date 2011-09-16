import java.util.Stack;

public class BasisMatrix extends Matrix {
  public BasisMatrix(int n) {
    super(n, (int)Math.pow(2, n-1));
  }

  public void permute() {
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
}

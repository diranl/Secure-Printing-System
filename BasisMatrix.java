import java.util.Stack;

public class BasisMatrix extends Matrix {
  public BasisMatrix(int n) {
    super(n, (int)Math.pow(2, n-1));
  }

  public void permute() {
    Stack<Integer> stack = new Stack<Integer>();

    int colIdx = 1; /* Keep first column of zeros, start inserting into second */
    for (int inserts=2 /*Start at 2 inserts*/; inserts<=super.row; inserts*=2) {
      int pivot = 0;
      for (pivot=0; pivot<inserts-1; pivot++) { stack.push(pivot); } // Init stack
      // First set of loop
      for (int variant=pivot; variant<super.row; variant++) {
        // add values into matrix
        super.matrix[variant][colIdx] = 1;
        for (Integer rowIdx : stack) super.matrix[rowIdx][colIdx] = 1;
        colIdx++;
      }

      while (true) {
        while (true) {
          if   (stack.empty() || (stack.peek()+1) + (inserts-stack.size()) /*position of smallest insert*/ < super.row) break;
          else stack.pop();
        }

        if (stack.empty()) break;
        pivot = stack.pop();
        pivot++;
        stack.push(pivot);

        while (stack.size() < inserts-1) stack.push(pivot++);

        for (int variant=pivot+1; variant<super.row; variant++) {
          // add values into matrix
          super.matrix[variant][colIdx] = 1;
          for (Integer rowIdx : stack) super.matrix[rowIdx][colIdx] = 1;
          colIdx++;
        }
      }
    }
  }
}


class SecurePrinting {
  public Matrix basisMatrix(int n) {
    //TODO: Generalize to higher dimensions
    Matrix basis = new Matrix(2,2);
    basis.matrix = {{0,1}, 
                    {0,1}};
    return basis;
  }

  public Matrix generate_pixel(int bit, int party, int partyNum) {
    Matrix basis = basisMatrix(2);
    short[] row = basis[party];
    if (bit == 1) {
      
    }
  }

  public static void main(String args[]) {
    Matrix A = Matrix.random(2,2);
    A.print();
  }
}

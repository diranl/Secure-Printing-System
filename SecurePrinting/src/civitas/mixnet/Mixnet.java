package civitas.mixnet;

public class Mixnet {
  
  protected class Server {
    protected final TranslationTable translation;
    protected final FactorTable factorTable;
    protected final Permutation permutation;

    // 1. Generates random factors (for ElGamal reencryption)
    // 2. Generate permutations
    // 3. Commit to random factors
    // 4. Commit to permutations
    // 5. Perform randomize() and permute() methods for TranslationTable
    protected Server(TranslationTable inputTable) {
      this.translation = new TranslationTable(inputTable);
      this.factorTable = new FactorTable(translation);
      this.permutation = new Permutation(translation.size);
      mix();
    }
    private void mix() {
      translation.randomize(factorTable);
      translation.permute(permutation);
    }
  }

  public final TranslationTable table;

  public Mixnet(int serverNum) {
    // Generate serverNum instances of Server
    table = null;
  }

  public TranslationTable execute() {
    // Performs the mixing, server by server in a serial fashion, as described by part 1. of Sub-protocol 1.1
    // Produces the final mixed table
    return null;
  }

  public static void main(String args[]) {
    // 1. Initialize Mixnet with desired amount of servers
    // 2. Perform the serial/chain computation to yield the mixed result
    // 3. TODO (change): given an input, encrypt, perform PET to yield a CipherMessage
    // 4. Perform the multi-party printing scheme
  }
}


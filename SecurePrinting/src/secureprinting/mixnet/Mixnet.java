package secureprinting.mixnet;

import civitas.crypto.ElGamalKeyPairShare;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Mixnet {
  public final int serverNum; 
  public TranslationTable mixedTable;
  public List<Server> serverLst;

  public static int CHALLENGE_NUM = 3;

  public Mixnet(int serverNum, ElGamalKeyPairShare share) {
    this.serverNum = serverNum;
  }

  public void execute(ElGamalKeyPairShare share) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException {
    // Performs the mixing, server by server in a serial fashion, as described by part 1. of Sub-protocol 1.1
    // Produces the final mixed table
    this.serverLst = new ArrayList<Server>(serverNum);
    TranslationTable initialTbl = TranslationTable.initTable(share);
    //TODO(crucial!): check that deep copies are performed rather than shallow
    Server newSvr = null;
    for (int idx=0; idx<serverNum; idx++) {
      System.out.println("Processing server: " + idx);
      if (idx == 0) newSvr = new Server(initialTbl);
      else newSvr = new Server(serverLst.get(idx-1));
      serverLst.add(newSvr);
    }
    this.mixedTable = newSvr.outputTbl;
  }

  public void validate() throws NoSuchAlgorithmException, NoSuchProviderException {
    for (Server server : serverLst) {
      System.out.println(".server:");
      Random rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
      for (int idx=0; idx<CHALLENGE_NUM; idx++) {
        server.challenge();
        ChallengeProof proof = server.reveal(rand.nextInt(2)==0 ? true : false);
        if (!ChallengeProof.verifyProof(proof)) throw new RuntimeException("did not verify");
      }
    }
  }

}


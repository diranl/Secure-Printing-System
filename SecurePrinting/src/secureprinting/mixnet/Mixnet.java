package secureprinting.mixnet;

import civitas.crypto.ElGamalKeyPairShare;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mixnet class instantiates and coordinates Server classes
 * <Note>Performs mixing of TranslationTable and verification through Shadow Mix</Note>
 */
public final class Mixnet {
  public final int serverNum; 
  public TranslationTable mixedTable;
  public List<Server> serverLst;

  public static int CHALLENGE_NUM = 3;

  public Mixnet(int serverNum, ElGamalKeyPairShare share) {
    this.serverNum = serverNum;
  }

  /**
   * execute: Mixes TranslationTable as described by part 1 of Sub-protocol 1.1
   */
  public void execute(ElGamalKeyPairShare share) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException {
    this.serverLst = new ArrayList<Server>(serverNum);
    TranslationTable initialTbl = TranslationTable.initTable(share);
    Server newSvr = null;
    for (int idx=0; idx<serverNum; idx++) {
      System.out.println("Processing server: " + idx);
      if (idx == 0) newSvr = new Server(initialTbl);
      else newSvr = new Server(serverLst.get(idx-1));
      serverLst.add(newSvr);
    }
    this.mixedTable = newSvr.outputTbl;
  }

  /**
   * validate: Challenges each server and checks against proof of validation
   * <Note> As described by Shadow Mix</Note>
   */
  public void validate() throws NoSuchAlgorithmException, NoSuchProviderException {
    for (Server server : serverLst) {
      System.out.println(".server:");
      Random rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
      for (int idx=0; idx<CHALLENGE_NUM; idx++) {
        server.challenge();
        ChallengeProof proof = server.reveal(rand.nextInt(2)==0 ? true : false);
        if (!ChallengeProof.verifyProof(proof)) throw new RuntimeException("ChallengeProof did not verify");
      }
    }
  }

}


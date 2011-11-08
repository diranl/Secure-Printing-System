package civitas.mixnet;

import civitas.crypto.ElGamalReencryptFactor;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalParametersC;

import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

public class FactorTable implements Serializable {
  private final List< List<ElGamalReencryptFactor> > factorTable; 
  protected final int size;

  public FactorTable(TranslationTable table) {
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)table.share.params;
    this.factorTable = new ArrayList< List<ElGamalReencryptFactor> >(table.size);
    this.size = table.size;

    List factors;
    Message msg;
    for (int i=0; i<size; i++) {
      msg = table.msgLst.get(i);
      factors = new ArrayList(msg.length+1);
      for (int j=0; j<=msg.length/*create a rand factor for pixels and msg key*/; j++) {
        factors.add(factory.generateElGamalReencryptFactor(params));
      }
      factorTable.add(factors);
    }
  }

  protected ElGamalReencryptFactor get(int row, int col) {
    return factorTable.get(row).get(col);
  }

  public void toFile(String filename) {
    try {
      FileOutputStream fos = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(this);
      out.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  public static FactorTable fromFile(String filename) {
    FactorTable factorTable = null;
    try {
      FileInputStream fis = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(fis);
      factorTable = (FactorTable)in.readObject();
      in.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return factorTable;
  }
}

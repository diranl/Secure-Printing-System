package secureprinting.mixnet;

import civitas.crypto.ElGamalReencryptFactor;
import civitas.crypto.concrete.CryptoFactoryC;
import civitas.crypto.concrete.ElGamalParametersC;
import civitas.crypto.concrete.ElGamalReencryptFactorC;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;

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
    this.factorTable = new ArrayList< List<ElGamalReencryptFactor> >(table.size);
    this.size = table.size;
    CryptoFactoryC factory = CryptoFactoryC.singleton();
    ElGamalParametersC params = (ElGamalParametersC)table.share.params;

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
  public FactorTable(List< List<ElGamalReencryptFactor> > factorTable, int size) {
    this.factorTable = factorTable;
    this.size = size;
  }

  protected ElGamalReencryptFactor get(int rowIdx, int colIdx) {
    return factorTable.get(rowIdx).get(colIdx);
  }
  protected List<ElGamalReencryptFactor> extractRow(int rowIdx) {
    return factorTable.get(rowIdx);
  }
  protected void set(int rowIdx, int colIdx, ElGamalReencryptFactor factor) {
    factorTable.get(rowIdx).set(colIdx, factor);
  }

  protected FactorTable invert(FactorTable shadowTbl, ElGamalParametersC params) {
    // For each original factors r_o and shadow factors r_s
    // compute r_o - r_s (mod p-1), where p-1 is the order of <g>
    List< List<ElGamalReencryptFactor> > _factorTable = new ArrayList< List<ElGamalReencryptFactor> >(size);
    for (int rowIdx=0, colSize=this.extractRow(rowIdx).size(); rowIdx<size; rowIdx++) {
      colSize=this.extractRow(rowIdx).size();
      List<ElGamalReencryptFactor> _row = new ArrayList<ElGamalReencryptFactor>(colSize);
      for (int colIdx=0; colIdx<colSize; colIdx++) {
        ElGamalReencryptFactor inverse = ((ElGamalReencryptFactorC)this.get(rowIdx, colIdx)).subtract((ElGamalReencryptFactorC)shadowTbl.get(rowIdx, colIdx), params.p);
        _row.add(inverse);
      }
      _factorTable.add(_row);
    }
    return (new FactorTable(_factorTable, size));
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    out.writeObject(this);
    out.close();
    return baos.toByteArray();
  }

  /**
   * Serializes the object into a JSON equivalence using the GSON project by Google
   */
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
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

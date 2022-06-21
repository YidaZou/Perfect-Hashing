package hash341;

import java.io.Serializable;

public class secondaryTable implements Serializable {
    public int hashesTried;
    public Hash24 finalHash;   //final hash with no collisiosn
    public City[] table;

    public secondaryTable(int hashesTried, Hash24 finalHash, City[] table) {
        this.hashesTried = hashesTried;
        this.finalHash = finalHash;
        this.table = table;
    }

    public int getHashesTried() {
        return hashesTried;
    }

    public void setHashesTried(int hashesTried) {
        this.hashesTried = hashesTried;
    }

    public Hash24 getFinalHash() {
        return finalHash;
    }

    public void setFinalHash(Hash24 finalHash) {
        this.finalHash = finalHash;
    }

    public City[] getTable() {
        return table;
    }

    public void setTable(City[] table) {
        this.table = table;
    }
}

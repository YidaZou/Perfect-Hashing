package hash341;

import java.io.*;
import java.util.*;

public class CityTable implements Serializable{
    private Hash24 hashFunction;
    private int numCities;
    private int tableSize;
    private int maxCollisions;
    private int maxCollisionsIndex; //index with most collisions
    LinkedList<City>[] table;  //initial parse table
    secondaryTable[] table2;  //final table

    //getters & setters
    public Hash24 getHashFunction() {
        return hashFunction;
    }
    public void setHashFunction(Hash24 hashFunction) {
        this.hashFunction = hashFunction;
    }
    public int getNumCities() {
        return numCities;
    }
    public void setNumCities(int numCities) {
        this.numCities = numCities;
    }
    public int getTableSize() {
        return tableSize;
    }
    public void setTableSize(int tableSize) {
        this.tableSize = tableSize;
    }
    public int getMaxCollisions() {
        return maxCollisions;
    }
    public void setMaxCollisions(int maxCollisions) {
        this.maxCollisions = maxCollisions;
    }
    public int getMaxCollisionsIndex() {
        return maxCollisionsIndex;
    }
    public void setMaxCollisionsIndex(int maxCollisionsIndex) {
        maxCollisionsIndex = maxCollisionsIndex;
    }
    public LinkedList[] getTable() {
        return table;
    }
    public void setTable(LinkedList[] table) {
        this.table = table;
    }

    public CityTable(String fname, int tsize){  //CityTable Constructor
        //initialize variables
        maxCollisions = 0;
        numCities = 0;
        tableSize = tsize;
        hashFunction = new Hash24();
        table = new LinkedList[tableSize];
        table2 = new secondaryTable[tableSize];

        readCitiesFromFile(fname);    //read cities from file

        //dump of hash functions used
        System.out.println("Primary hash table hash function:");
        getHashFunction().dump();

        //print primary hash table stats
        System.out.println("\nPrimary hash table statistics: ");
        System.out.println("\tNumber of cities: " + getNumCities());
        System.out.println("\tTable size: " + getTableSize());
        System.out.println("\tMax Collisions = " + getMaxCollisions());
        printNumCollisions();  //print # of slots with i cities
        printMaxCollisionSlot(); //print cities in slot with the most collisions

        //make secondary table
        makeSecondaryTable();
        // print hash function statistics
        printSecondaryTableStats();
    }

    public void printSecondaryTableStats() {
        System.out.println("\nSecondary hash table statistics: ");
        int countTotal = 0; //secondary hash tables with more than 1 item
        double average = 0; //average # of hash functions tried;
        for (int i=1; i<=20; i++) {
            int count = 0;
            for (int j=0; j<tableSize; j++) {
                if (table2[j].hashesTried == i) {
                    count++;
                }
            }
            System.out.println("\t# of secondary hash tables trying " + i + " hash functions = " + count);
            countTotal += count;
            average += (count*i);
        }
        average /= countTotal;
        System.out.println();
        System.out.println("\nNumber of secondary hash tables with more than one item: " + countTotal);
        System.out.println("\nAverage # of hash functions tried = " + average);
    }

    public void printNumCollisions() {  //function to print # of slots with i collisions
        for (int i=1; i<=24; i++) { //number of collisions
            int count = 0;
            for (int j=0; j<tableSize; j++) {   //go through all slots
                if(table[j] != null && table[j].size() == i){
                    count++;
                }
            }
            System.out.println("\t # of primary slots with " + i + " cities = " + count);
        }
    }

    public void readCitiesFromFile(String fname) {
        Scanner infile = null;
        //open file
        try {
            infile = new Scanner(new FileReader(fname));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
            System.exit(0);
        }

        while (infile.hasNextLine()) {
            String key = infile.nextLine(); //City, State
            String coordinates = infile.nextLine();
            //split and convert coordinates into lat. and long. floats
            String[] splitLatLong = coordinates.split("\\s+");
            float latitude = Float.parseFloat(splitLatLong[0]);
            float longitude = Float.parseFloat(splitLatLong[1]);
            //get hash index from key
            int index = hashFunction.hash(key) % tableSize;
            //push city into linked list at index;
            if(table[index] == null){   //if index hasn't been used yet
                table[index] = new LinkedList<City>();
            }
            table[index].add(new City(key, latitude, longitude));
            numCities++;    //increment number of cities

            if (table[index].size() > maxCollisions) {  //check if maxCollisions is changed after adding new element
                maxCollisions = table[index].size();
                maxCollisionsIndex = index;
            }
        }
        infile.close();
    }

    void printMaxCollisionSlot() {
        System.out.println("\n*** Cities in the slot with the most collisions ***");
        for (int i=0; i<table[maxCollisionsIndex].size(); i++) {
            City out = table[maxCollisionsIndex].get(i);
            System.out.println("\t" + out.name + " (" + out.latitude + ", " + out.longitude + " )");
        }
    }

    void makeSecondaryTable(){
        for (int i=0; i<tableSize; i++) {
            //initialize attributes of secondary table;
            int hashes = 0; //keep count of how many hashes were tried;
            City[] tryTable = null;
            Hash24 tryHash = null;
            if (table[i] != null && table[i].size() > 1) {    //if collision occurred
                boolean noCollision = false;    //check if a collision occurs in secondary table
                while (!noCollision) {
                    noCollision = true;
                    tryHash = new Hash24(); //get new hash function
                    hashes++;
                    tryTable = new City[table[i].size() * table[i].size()];    //create secondary table of size t^2
                    for (City city : table[i]) {
                        int index = tryHash.hash(city.name) % (table[i].size() * table[i].size());
                        if(tryTable[index] == null){
                            tryTable[index] = city;
                        }else{
                            noCollision = false;
                            //break;
                        }
                    }
                }

            }else if(table[i] != null && table[i].size() == 1) {    //for indexes with no collisions
                tryTable = new City[] {table[i].get(0)};
            }
            table2[i] = new secondaryTable(hashes, tryHash, tryTable);  //add to new table
        }
    }

    public City find(String cName) {    //finds city given name ("City, State")
        int index1 = hashFunction.hash(cName) % tableSize;  //index in primary tree;
        if(table[index1] == null){
            return null;
        }else if(table2[index1].table.length == 1){ //1 element, no secondary table
            City out = table2[index1].table[0];
            if(cName.equals(out.name))   //check if name matches; hashes could match without being actually the same name
                return out;
        }else if(table2[index1].table.length > 1){    //secondary table
            int index2 = table2[index1].finalHash.hash(cName) % (table2[index1].table.length);  //use hash of secondary table
            City out = table2[index1].table[index2];
            if(out != null && cName.equals(out.name))   //check if secondaryTable index exists and name matches
                return out;
        }
        return null;
    }

    public void writeToFile(String fName){
        try {
            FileOutputStream outFile = new FileOutputStream(fName);
            ObjectOutputStream outObject = new ObjectOutputStream(outFile);
            outObject.writeObject(this);
            outObject.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //read CityTable back from file
    public static CityTable readFromFile(String fName){
        try {
            FileInputStream inFile = new FileInputStream(fName);
            ObjectInputStream inObject = new ObjectInputStream(inFile);
            CityTable cities = (CityTable) inObject.readObject();
            return cities;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

package hash341;

public class Program1 {

    public static void main(String[] args) {
        // create table and try to read in the file
        CityTable table = new CityTable("US_Cities_LL.txt", 16000);
        table.writeToFile("US_Cities_LL.ser");

    }
}

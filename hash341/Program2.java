package hash341;

import java.util.Scanner;

public class Program2 {

    public static void main(String[] args) {

        CityTable US_Cities = CityTable.readFromFile("US_Cities_LL.ser");
        String cName;
        String input = null;
        Scanner reader = new Scanner(System.in);
        while(true){
            System.out.println("Enter City, State (or 'quit'): ");
            input = reader.nextLine();
            if(input.equals("quit")){
                break;
            }else{
                City city = US_Cities.find(input);
                if(city != null){
                    System.out.println(city.toString());
                }else{
                    System.out.println("Could not find '" + input + "'");
                }
            }
        }
    }
}

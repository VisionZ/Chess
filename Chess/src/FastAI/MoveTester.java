package FastAI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MoveTester {
    
    
    
    //live link, from GUI get a board and convert it into a bitboard
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(new File("src/FastAI/TestBoard.dat"));
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            System.out.println(line);
        }
        scan.close();
    }
}

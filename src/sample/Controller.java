package sample;

import Exceptions.InvalidFileSyntax;

import java.io.*;

public class Controller {

    public static boolean validateFile(String fileName) throws Exception {
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        int lineNumber = 1;
        if (!line.matches("\\d+")) {
            throw new InvalidFileSyntax(lineNumber);
        }
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (!line.matches("\\d+\\s\\d+")) {
                throw new InvalidFileSyntax(lineNumber);
            }
        }
        return true;
    }
}

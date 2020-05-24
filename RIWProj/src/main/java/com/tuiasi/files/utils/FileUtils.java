package com.tuiasi.files.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtils {
    public static String readFromFile(String filePath) throws FileNotFoundException {
        Scanner myReader = new Scanner(new File(filePath));
        StringBuilder sb = new StringBuilder();
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
        }
        myReader.close();
        return sb.toString();
    }
}

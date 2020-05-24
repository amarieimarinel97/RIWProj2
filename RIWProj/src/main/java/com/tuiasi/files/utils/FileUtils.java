package com.tuiasi.files.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class FileUtils {
    public static String readFromFile(String filePath) throws FileNotFoundException {
        Scanner myReader = new Scanner(new File(filePath));
        StringBuilder sb = new StringBuilder();
        while (myReader.hasNextLine()) {
            sb.append(myReader.nextLine()).append("\n");
        }
        myReader.close();
        return sb.toString();
    }

    public static boolean checkIfStringIsNotEmpty(String input) {
        return !(Objects.isNull(input) || input.isEmpty() || input.isBlank());
    }
}

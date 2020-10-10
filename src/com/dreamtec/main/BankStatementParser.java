package com.dreamtec.main;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.dreamtec.bsp.BSPEngine;
import com.dreamtec.bsp.utils.Utils;

/**
 * Main entry point that uses the BankStatementParser Engine.<br>
 * 
 * @author chinphek
 */
public class BankStatementParser {
    public static void main(final String[] args) {
        System.out.println("BankStatementParser started");
        File dir = new File("");
        System.out.println("Current directory: '" + dir.getAbsolutePath() + "'.");

        final List<String> EXTENSIONS = Arrays.asList(".csv", ".xls");
        System.out.println("    Searching for files with extensions '" + EXTENSIONS + "'.");
        final List<File> list = Utils.getFilesWithExtension(dir.getAbsolutePath() + "/mystatements", EXTENSIONS);
        System.out.println("    Found '" + list.size() + "' file(s) with extensions '" + EXTENSIONS + "'.");

        final BSPEngine bsp = new BSPEngine();
        for (final File file : list) {
            final String type = bsp.add(file);
            if(type == null) {
                System.out.println("        " + file.getName() + " => No handler found.");
            } else {
                System.out.println("        " + file.getName() + " => " + type);
            }
        }

        System.out.println("    Processing statements");
        bsp.process();

        System.out.println("BankStatementParser ended");
    }
}

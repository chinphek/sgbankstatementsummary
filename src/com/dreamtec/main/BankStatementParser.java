package com.dreamtec.main;

import java.io.File;
import java.io.FileNotFoundException;
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

        final List<File> list = getStatementFilelist();
        if(list == null || list.size() == 0) {
            System.out.println("    Please place statement files into folder '" + dir.getAbsolutePath()+ "'.");
            return;
        }

        final BSPEngine bsp = new BSPEngine();
        for (final File file : list) {
            final String type = bsp.add(file);
            if (type == null) {
                System.out.println("        " + file.getName() + " => No handler found.");
            } else {
                System.out.println("        " + file.getName() + " => " + type);
            }
        }

        System.out.println("    Processing Accounts");
        bsp.process();
        try {
            File summary = new File (dir.getAbsolutePath() + "/mystatements/out/summary.xlsx");
            System.out.println("    Saving all transactions into '" + summary.getAbsolutePath() + "'.");

            File out_dir = summary.getParentFile();
            if(!out_dir.exists()) {
                out_dir.mkdirs();
            }
            bsp.save(summary.getAbsolutePath());
            System.out.println("        Saved successfully.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("BankStatementParser ended");
    }

    /**
     * Get list of statement files from subdirectory './mystatements'.
     * @return
     */
    private static List<File> getStatementFilelist() {
        File dir = new File(new File("").getAbsolutePath() + "/mystatements");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        final List<String> EXTENSIONS = Arrays.asList(".csv", ".xls");
        System.out.println("    Searching for files with extensions '" + EXTENSIONS + "'.");
        final List<File> list = Utils.getFilesWithExtension(dir.getAbsolutePath(), EXTENSIONS);
        System.out.println("    Found '" + list.size() + "' file(s) with extensions '" + EXTENSIONS + "'.");

        return list;
    }
}

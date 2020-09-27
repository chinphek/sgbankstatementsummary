package com.dreamtec.bsp;

import java.io.File;
import java.util.List;

/**
 * Bank Statement Parser Engine.<br>
 * <br>
 * Process all .csv files in the specific folder.<br>
 * 
 * @author chinphek
 */
public class BSPEngine {
    private static final String EXT = ".csv";
    private File dir;

    public BSPEngine(final String dirname) {
        dir = new File(dirname);

        if(dir == null || !dir.exists()) {
            throw new IllegalArgumentException("Directory '" + dirname + "' does not exists.");
        }

        if(!dir.isDirectory()) {
            throw new IllegalArgumentException("'" + dir.getAbsolutePath() + "' exists but is not a directory.");
        }

        System.out.println("BankStatementParser Engine initialized for directory '" + dir.getAbsolutePath() + "'.");
    }

    public void process() {
        System.out.println("Searching for files with extension '" + EXT + "'.");
        List<File> list = Utils.getFilesWithExtension(dir, EXT);
        System.out.println("Found '" + list.size() + "' file(s) with extension '" + EXT + "'.");
        for(File f : list) {
            System.out.println("    " + f.getAbsolutePath());
        }
    }
}

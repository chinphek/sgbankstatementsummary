package com.dreamtec.main;

import java.io.File;

import com.dreamtec.bsp.BSPEngine;

/**
 * Main entry point that uses the BankStatementParser Engine.<br>
 * 
 * @author chinphek
 */
public class BankStatementParser {
    public static void main(String[] args) {
        System.out.println("BankStatementParser started");
        File dir = new File("");
        System.out.println("Current directory: '" + dir.getAbsolutePath() + "'.");

        BSPEngine bsp = new BSPEngine(dir.getAbsolutePath());
        bsp.process();

        System.out.println("BankStatementParser ended");
    }
}

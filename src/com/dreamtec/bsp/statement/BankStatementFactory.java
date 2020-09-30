package com.dreamtec.bsp.statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BankStatementFactory {

    public static IBankStatement getHandler(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file)); // creates a buffering character input stream
            String line = br.readLine();
            if(line.contains("Account details for:,360 Account")) {
                return new OCBC_360Savings_Statement();
            } else if(line.contains("Account details for:,OCBC 365 Credit Card")) {
                return new OCBC_365CreditCard_Statement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }    
}

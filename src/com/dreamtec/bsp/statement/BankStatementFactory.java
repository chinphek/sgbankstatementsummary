package com.dreamtec.bsp.statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.dreamtec.bsp.utils.Utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Bank statement factory.<br>
 * 
 * @author chinphek
 */
public class BankStatementFactory {

    public static IBankStatement getHandler(final File file) {
        if (file.getName().endsWith(".csv")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file)); // creates a buffering character input stream
                final String line = br.readLine();
                // System.out.println(line);
                if (line.contains("Account details for:,360 Account")) {
                    return new OCBC_360Savings_Statement(file);
                } else if (line.contains("Account details for:,OCBC 365 Credit Card")) {
                    return new OCBC_365CreditCard_Statement(file);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (file.getName().endsWith(".xls")) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                Workbook wb = WorkbookFactory.create(stream);
                
                if(Utils.workbookStringValue(wb, 0, 0, 0).startsWith("United Overseas Bank Limited.")) {
                    if(Utils.workbookStringValue(wb, 0, 5, 1).equals("FlexiDeposit")) {
                        return new UOB_FlexiDepositSavings_Statement(file);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }    
}

package com.dreamtec.bsp.statement.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

import com.dreamtec.bsp.statement.AbstractBankStatement;
import com.dreamtec.bsp.statement.AccountType;
import com.dreamtec.bsp.statement.Transaction;
import com.dreamtec.bsp.utils.ConsoleColors;
import com.dreamtec.bsp.utils.Utils;

/**
 * Handles OCBC Bonus+ savings account.<br>
 * Copied from OCBC_360Savings_Statement.java on 30-Dec-2024
 * 
 * @author chinphek
 */
public class OCBC_Bonus_Statement extends AbstractBankStatement {
    private BufferedReader br = null;
    private String line = null;
    private String[] cells = null;
    private static final String DATE_FORMAT = "dd/MM/uuuu";

    public OCBC_Bonus_Statement(final File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * Checks whether input file is OCBC Bonus+ Savings account statement.
     * 
     * @param file
     * @return
     */
    public static boolean isThisType(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file)); // creates a buffering character input stream
            final String line = br.readLine();
            if (line.contains("Bonus +")) {
                return true;
            }
        } catch (final Exception e) {
            // do nothing
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    @Override
    protected void processFileHeader() {
        accountName = ConsoleColors.RED_BRIGHT + "OCBC Bonus+ Savings" + ConsoleColors.RESET;
        accountType = AccountType.SAVINGS;
        
        try {
            //Open the file for line by line reading
            br = new BufferedReader(new FileReader(file));

            line = br.readLine();
            if(line.startsWith("\'")) {
                // 2022-09-18: OCBC 360 csv statement contains an additional prefix of "'"
                accountNumber = line.substring(35);
            } else {
                accountNumber = line.substring(33);
            }
            

            while((line = br.readLine()) != null) {
                cells = Utils.splitCSV(line);
                if(cells.length > 1 && Utils.isDate(cells[1], DATE_FORMAT)) {
                    // 07-Sep-2024 OCBC changes the format of their CSV
                    // It is now split into 2 lines. The cell length changed from 5 to 3
                    if(cells.length == 3) {
                        line += " " + br.readLine();
                        cells = Utils.splitCSV(line);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        accountKey = "OCBC Bonus " + accountNumber;
    }
    
    @Override
    protected Transaction getNextTransaction() {
        if(line == null) {
            return null;
        }

        Transaction t = new Transaction();
        //[0]: Transaction date
        //[1]: Value date
        LocalDate date = Utils.toDate(cells[1], DATE_FORMAT);
        t.setDate(date);
        //[2]: Description
        t.setDescription(cells[2]);
        //[3]: Withdrawals (SGD)
        String v3 = cells[3];
        t.setOut(v3 == null || v3.isBlank() ? 0 : Utils.toAmount(v3));
        //[4]: Deposits (SGD)
        if(cells.length > 4) {
            String v4 = cells[4];
            t.setIn(v4 == null || v4.isBlank() ? 0 : Utils.toAmount(v4));
        } 
        
        try {
            while ((line = br.readLine()) != null) {
                cells = Utils.splitCSV(line);
                if (cells.length > 1 && Utils.isDate(cells[1], DATE_FORMAT)) {
                    // 07-Sep-2024 OCBC changes the format of their CSV
                    // It is now split into 2 lines. The cell length changed from 5 to 3
                    if(cells.length == 3) {
                        line += " " + br.readLine();
                        cells = Utils.splitCSV(line);
                    }
                    break;
                } else if(cells.length > 2) {
                    t.setDescription(t.getDescription() + " " + cells[2]);
                }
            }
        } catch (IOException e) {
            line = null;
            e.printStackTrace();
        }
        
        return t;
    }

    
}

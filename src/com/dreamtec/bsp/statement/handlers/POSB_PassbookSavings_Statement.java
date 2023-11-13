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
 * Handles POSB Passbook Savings in the form of .csv
 * 
 * @author chinphek
 */
public class POSB_PassbookSavings_Statement extends AbstractBankStatement {
    private BufferedReader br = null;
    private String line = null;
    private String[] cells = null;
    private static final String DATE_FORMAT = "dd LLL uuuu";

    public POSB_PassbookSavings_Statement(File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * Checks whether input file is POSB Passbook Savings account statement.
     * 
     * @param file
     * @return
     */
    public static boolean isThisType(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file)); // creates a buffering character input stream

            // Skip the first 11 lines as they are blank
            for(int i=0; i<11; i++) {
                br.readLine();
            }

            String line = br.readLine();
            return line.contains("Account Details For:,POSB Savings");
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
        accountName = ConsoleColors.YELLOW_BRIGHT + "POSB Savings" + ConsoleColors.RESET;
        accountType = AccountType.SAVINGS;
        
        try {
            //Open the file for line by line reading
            br = new BufferedReader(new FileReader(file));

            // Skip the first 11 lines as they are blank
            for(int i=0; i<11; i++) {
                br.readLine();
            }

            line = br.readLine();
            accountNumber = line.substring(34);

            while((line = br.readLine()) != null) {
                cells = Utils.splitCSV(line);
                if(cells.length > 0 && Utils.isDate(cells[0], DATE_FORMAT)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        accountKey = "POSB Savings " + accountNumber;
    }

    @Override
    protected Transaction getNextTransaction() {
        if(line == null) {
            return null;
        }

        Transaction t = new Transaction();
        //[0]: Transaction date
        LocalDate date = Utils.toDate(cells[0], DATE_FORMAT);
        t.setDate(date);
        //[1]: Reference, [4][5][6]: Transaction Ref1/2/3
        t.setDescription(cells[1] + " " + cells[4] + " " + cells[5] + " " + cells[6]);
        //[2]: Withdrawals (SGD)
        String v2 = cells[2];
        t.setOut(v2 == null || v2.isBlank() ? 0 : Utils.toAmount(v2));
        //[3]: Deposits (SGD)
        String v3 = cells[3];
        t.setIn(v3 == null || v3.isBlank() ? 0 : Utils.toAmount(v3));

        try {
            while ((line = br.readLine()) != null) {
                cells = Utils.splitCSV(line);
                if (cells.length > 0 && Utils.isDate(cells[0], DATE_FORMAT)) {
                    break;
                }
            }
        } catch (IOException e) {
            line = null;
            e.printStackTrace();
        }
        
        return t;
    }
}

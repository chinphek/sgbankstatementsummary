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
 * Handles OCBC 365 credit card statement.<br>
 * 
 * @author chinphek
 */
public class OCBC_365CreditCard_Statement extends AbstractBankStatement {
    private BufferedReader br = null;
    private String line = null;
    private String[] cells = null;
    private static final String DATE_FORMAT = "dd/MM/uuuu";

    public OCBC_365CreditCard_Statement(final File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * Checks whether input file is OCBC 365 Credit Card account statement.
     * 
     * @param file
     * @return
     */
    public static boolean isThisType(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file)); // creates a buffering character input stream
            final String line = br.readLine();
            if (line.contains("Account details for:,OCBC 365 Credit Card")) {
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
        accountName = ConsoleColors.RED_BRIGHT + "OCBC 365 Credit Card" + ConsoleColors.RESET;
        accountType = AccountType.CREDITCARD;
        
        try {
            //Open the file for line by line reading
            br = new BufferedReader(new FileReader(file));

            line = br.readLine();
            accountNumber = "xxxx-xxxx-xxxx" + line.substring(56);

            while((line = br.readLine()) != null) {
                cells = Utils.splitCSV(line);
                if(cells.length > 0 && Utils.isDate(cells[0], DATE_FORMAT)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        accountKey = "OCBC 365 " + accountNumber;
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
        //[1]: Description
        t.setDescription(cells[1]);
        //[2]: Withdrawals (SGD)
        String v2 = cells[2];
        t.setOut(v2 == null || v2.isBlank() ? 0 : Utils.toAmount(v2));
        //[3]: Deposits (SGD)
        if(cells.length > 3) {
            String v3 = cells[3];
            t.setIn(v3 == null || v3.isBlank() ? 0 : Utils.toAmount(v3));
        } 
        
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

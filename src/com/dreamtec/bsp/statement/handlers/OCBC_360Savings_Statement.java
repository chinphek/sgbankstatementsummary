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
 * Handles OCBC 360 savings account.<br>
 * 
 * @author chinphek
 */
public class OCBC_360Savings_Statement extends AbstractBankStatement {
    private BufferedReader br = null;
    private String line = null;
    private String[] cells = null;
    private static final String DATE_FORMAT = "dd/MM/uuuu";

    public OCBC_360Savings_Statement(final File file) throws FileNotFoundException {
        super(file);
    }

    @Override
    protected void processFileHeader() {
        accountName = ConsoleColors.RED_BRIGHT + "OCBC 360 Savings" + ConsoleColors.RESET;
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
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        accountKey = "OCBC 360 " + accountNumber;
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

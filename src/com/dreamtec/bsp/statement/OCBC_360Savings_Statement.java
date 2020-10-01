package com.dreamtec.bsp.statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.dreamtec.bsp.utils.ConsoleColors;

/**
 * Handles OCBC 360 savings account.<br>
 * 
 * @author chinphek
 */
public class OCBC_360Savings_Statement extends AbstractBankStatement {

    public OCBC_360Savings_Statement(final File file) throws FileNotFoundException {
        super(file);
        accountName = ConsoleColors.RED_BRIGHT + "OCBC 360 Savings Statement" + ConsoleColors.RESET;
        accountType = AccountType.SAVINGS;
    }

    @Override
    protected void processFileHeader() {
        try {
            final String line = br.readLine();
            accountNumber = line.substring(33);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected Transaction getNextTransaction() {
        return null;
    }

    
}

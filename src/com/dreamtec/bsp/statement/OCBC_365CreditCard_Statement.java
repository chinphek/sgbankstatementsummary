package com.dreamtec.bsp.statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.dreamtec.bsp.utils.ConsoleColors;

/**
 * Handles OCBC 365 credit card statement.<br>
 * 
 * @author chinphek
 */
public class OCBC_365CreditCard_Statement extends AbstractBankStatement {

    public OCBC_365CreditCard_Statement(final File file) throws FileNotFoundException {
        super(file);
        accountName = ConsoleColors.RED_BRIGHT + "OCBC 365 Credit Card Statement" + ConsoleColors.RESET;
        accountType = AccountType.CREDITCARD;
    }

    @Override
    protected void processFileHeader() {
        try {
            final String line = br.readLine();
            accountNumber = "xxxx-xxxx-xxxx" + line.substring(56);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Transaction getNextTransaction() {
        return null;
    }
    
}

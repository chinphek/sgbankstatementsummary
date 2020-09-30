package com.dreamtec.bsp.statement;

import java.io.File;

import com.dreamtec.bsp.utils.ConsoleColors;

public class OCBC_365CreditCard_Statement extends AbstractBankStatement implements IBankStatement {

    public OCBC_365CreditCard_Statement(final File file) {
        super(file);
    }

    @Override
    public String getType() {
        return ConsoleColors.RED_BRIGHT + "OCBC 365 Credit Card Statement" + ConsoleColors.RESET;
    }
    
}

package com.dreamtec.bsp.statement;

import java.io.File;

import com.dreamtec.bsp.utils.ConsoleColors;

public class OCBC_360Savings_Statement extends AbstractBankStatement implements IBankStatement {

    public OCBC_360Savings_Statement(final File file) {
        super(file);
    }

    @Override
    public String getType() {
        return ConsoleColors.RED_BRIGHT + "OCBC 360 Savings Statement" + ConsoleColors.RESET;
    }
    
}

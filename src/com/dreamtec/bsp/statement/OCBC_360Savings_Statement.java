package com.dreamtec.bsp.statement;

import com.dreamtec.bsp.utils.ConsoleColors;

;

public class OCBC_360Savings_Statement implements IBankStatement {

    @Override
    public String getType() {
        return ConsoleColors.RED_BRIGHT + "OCBC 360 Savings Statement" + ConsoleColors.RESET;
    }
    
}

package com.dreamtec.bsp.statement;

import com.dreamtec.bsp.utils.ConsoleColors;

public class OCBC_365CreditCard_Statement implements IBankStatement {

    @Override
    public String getType() {
        return ConsoleColors.RED_BRIGHT + "OCBC 365 Credit Card Statement" + ConsoleColors.RESET;
    }
    
}

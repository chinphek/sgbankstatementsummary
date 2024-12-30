package com.dreamtec.bsp.statement;

import java.io.File;

import com.dreamtec.bsp.statement.handlers.OCBC_360Savings_Statement;
import com.dreamtec.bsp.statement.handlers.OCBC_365CreditCard_Statement;
import com.dreamtec.bsp.statement.handlers.OCBC_Bonus_Statement;
import com.dreamtec.bsp.statement.handlers.POSB_PassbookSavings_Statement;
import com.dreamtec.bsp.statement.handlers.UOB_FlexiDepositSavings_Statement;

/**
 * Bank statement factory.<br>
 * 
 * @author chinphek
 */
public class BankStatementFactory {

    public static IBankStatement getHandler(final File file) {
        //--if(file.getName().equals("OCBC 2022-06.csv")) {
        //--    System.out.println("Debugging " + file.getName());
        //--}

        try {
            if(OCBC_360Savings_Statement.isThisType(file)) {
                return new OCBC_360Savings_Statement(file);
            } else if(OCBC_365CreditCard_Statement.isThisType(file)) {
                return new OCBC_365CreditCard_Statement(file);
            } else if(OCBC_Bonus_Statement.isThisType(file)) {
                return new OCBC_Bonus_Statement(file);
            } else if (UOB_FlexiDepositSavings_Statement.isThisType(file)) {
                return new UOB_FlexiDepositSavings_Statement(file);
            } else if(POSB_PassbookSavings_Statement.isThisType(file)) {
                return new POSB_PassbookSavings_Statement(file);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }    
}

package com.dreamtec.bsp.statement.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;

import com.dreamtec.bsp.statement.AbstractBankStatement;
import com.dreamtec.bsp.statement.AccountType;
import com.dreamtec.bsp.statement.Transaction;
import com.dreamtec.bsp.utils.ConsoleColors;
import com.dreamtec.bsp.utils.Utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class UOB_FlexiDepositSavings_Statement extends AbstractBankStatement {
    private FileInputStream stream = null;
    private Workbook wb;
    private int sheet;
    private int row;
    private static final String DATE_FORMAT = "dd MMM uuuu";

    public UOB_FlexiDepositSavings_Statement(File file) throws FileNotFoundException {
        super(file);
    }

    @Override
    protected void processFileHeader() {
        accountName = ConsoleColors.BLUE_BRIGHT + "UOB FlexiDeposit Savings" + ConsoleColors.RESET;
        accountType = AccountType.SAVINGS;
        
        try {
            stream = new FileInputStream(file);
            wb = WorkbookFactory.create(stream);

            accountNumber = Utils.workbookStringValue(wb, 0, 4, 1);
            sheet = 0;
            // For UOB, the latest transactions are at the top.
            // The first transaction is at the last row.
            row = wb.getSheetAt(sheet).getLastRowNum();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        accountKey = "UOB Flexi " + accountNumber;
    }

    @Override
    protected Transaction getNextTransaction() {
        // Row 8 is the latest transaction
        if(row < 8) {
            return null;
        }

        Transaction t = new Transaction();
        //[0]: Transaction Date
        LocalDate date = Utils.toDate(Utils.workbookStringValue(wb, sheet, row, 0), DATE_FORMAT);
        t.setDate(date);
        //[1]: Transaction Description
        t.setDescription(Utils.workbookStringValue(wb, sheet, row, 1).replace("\n", " "));
        //[2]: Withdrawal
        t.setOut(Utils.workbookNumericValue(wb, sheet, row, 2));
        //[3]: Deposit
        t.setIn(Utils.workbookNumericValue(wb, sheet, row, 3));
        //[4]: Available Balance
        t.setBalance(Utils.workbookNumericValue(wb, sheet, row, 4));
        row--;
        return t;
    }
    
}

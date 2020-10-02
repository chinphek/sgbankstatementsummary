package com.dreamtec.bsp.statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for bank statement.<br>
 * 
 * @author chinphek
 */
public abstract class AbstractBankStatement implements IBankStatement {
    protected File file;
    protected BufferedReader br = null;
    protected String accountName;
    protected AccountType accountType;
    protected String accountNumber = "?";

    public String getAccountName() { return accountName; }
    public AccountType getAccountType() { return accountType; }
    public String getAccountNumber() { return accountNumber; }

    public AbstractBankStatement(File file) throws FileNotFoundException{
        this.file = file;
    }

    public List<Transaction> process() {
        List<Transaction> list = new ArrayList<>();
        try {
            //Open the file for line by line reading
            br = new BufferedReader(new FileReader(file));

            //Process the file header
            System.out.println("        Processing " + accountName);
            processFileHeader();
            System.out.println("            Account Number: " + accountNumber);

            //Process the transactions
            Transaction t;
            while((t = getNextTransaction()) != null) {
                list.add(t);
                System.out.println("            " + t);
            }
            System.out.println("            Found '" + list.size() + "' transaction(s).");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    protected abstract void processFileHeader();
    protected abstract Transaction getNextTransaction();

}

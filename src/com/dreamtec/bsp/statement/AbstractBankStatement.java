package com.dreamtec.bsp.statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for bank statement.<br>
 * 
 * @author chinphek
 */
public abstract class AbstractBankStatement implements IBankStatement {
    protected File file;
    protected String accountName;
    protected String accountShortName;
    protected AccountType accountType;
    protected String accountNumber = "?";

    public String getAccountName() { return accountName; }
    public AccountType getAccountType() { return accountType; }
    public String getAccountShortName() { return accountShortName; }
    public String getAccountNumber() { return accountNumber; }

    public AbstractBankStatement(File file) throws FileNotFoundException{
        this.file = file;
    }

    public String getFilename() {
        return file.getName();
    }

    @Override
	public int compareTo(IBankStatement that) {
		return this.accountShortName.compareTo(that.getAccountShortName());
	}

    public List<Transaction> process() {
        List<Transaction> list = new ArrayList<>();
        
        System.out.println("            Processing file " + file.getName());
        System.out.println("                Account Name: " + accountName);
        System.out.println("                Account Number: " + accountNumber);

        // Process the transactions
        Transaction t;
        while ((t = getNextTransaction()) != null) {
            list.add(t);
            // System.out.println("            " + t);
            // System.out.println(t.getOut() + "\t" + t.getIn());
        }
        System.out.println("                Found '" + list.size() + "' transaction(s).");

        return list;
    }

    protected abstract Transaction getNextTransaction();

}

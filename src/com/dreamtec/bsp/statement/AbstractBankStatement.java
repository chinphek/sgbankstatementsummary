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
    protected AccountType accountType;
    protected String accountName;
    protected String accountNumber;
    protected String accountKey;

    public AccountType getAccountType() { return accountType; }
    public String getAccountName() { return accountName; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountKey() { return accountKey; }

    public AbstractBankStatement(File file) throws FileNotFoundException{
        this.file = file;
        processFileHeader();
    }

    public String getFilename() {
        return file.getName();
    }

    @Override
	public int compareTo(IBankStatement that) {
		return this.accountKey.compareTo(that.getAccountKey());
	}

    public List<Transaction> process() {
        processFileHeader();

        List<Transaction> list = new ArrayList<>();

        //--if (file.getName().equals("OCBC 2022-06.csv")) {
        //--    System.out.println("Debugging " + file.getName());
        //--}
        
        System.out.println("            Processing file " + file.getName());
        System.out.println("                Account Name: " + accountName);
        System.out.println("                Account Number: " + accountNumber);

        // Process the transactions
        Transaction t;
        while ((t = getNextTransaction()) != null) {
            t.setAccountkey(getAccountKey());
            list.add(t);
            // System.out.println("            " + t);
            // System.out.println(t.getOut() + "\t" + t.getIn());
        }
        System.out.println("                Found '" + list.size() + "' transaction(s).");

        return list;
    }

    protected abstract void processFileHeader();
    protected abstract Transaction getNextTransaction();

}

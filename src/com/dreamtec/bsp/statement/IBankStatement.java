package com.dreamtec.bsp.statement;

import java.util.List;

/**
 * Interface to various bank statement handlers.<br>
 * 
 * @author chinphek
 */
public interface IBankStatement extends Comparable<IBankStatement> {
    public String getFilename();
    public AccountType getAccountType();
    public String getAccountName();
    public String getAccountNumber();
    public String getAccountKey();
    public List<Transaction> process();
}

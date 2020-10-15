package com.dreamtec.bsp.statement;

import java.util.List;

/**
 * Interface to various bank statement handlers.<br>
 * 
 * @author chinphek
 */
public interface IBankStatement extends Comparable<IBankStatement> {
    public String getFilename();
    public String getAccountName();
    public String getAccountShortName();
    public AccountType getAccountType();
    public String getAccountNumber();
    public List<Transaction> process();
}

package com.dreamtec.bsp.statement;

import java.util.List;

/**
 * Interface to various bank statement handlers.<br>
 * 
 * @author chinphek
 */
public interface IBankStatement {
    public String getAccountName();
    public AccountType getAccountType();
    public String getAccountNumber();
    public List<Transaction> process();
}

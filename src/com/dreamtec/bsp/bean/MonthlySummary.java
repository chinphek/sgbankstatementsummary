package com.dreamtec.bsp.bean;

import java.time.LocalDate;

public class MonthlySummary implements Comparable<MonthlySummary>{
    private String accountKey;
    private LocalDate month;
    private String refIn;
    private String refOut;
    private String refBalance;

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public String getRefIn() {
        return refIn;
    }

    public void setRefIn(String refIn) {
        this.refIn = refIn;
    }

    public String getRefOut() {
        return refOut;
    }

    public void setRefOut(String refOut) {
        this.refOut = refOut;
    }

    public String getRefBalance() {
        return refBalance;
    }

    public void setRefBalance(String refBalance) {
        this.refBalance = refBalance;
    }

    @Override
    public int compareTo(MonthlySummary that) {
        int res = this.month.compareTo(that.month);
        if(res == 0) {
            return this.accountKey.compareTo(that.accountKey);
        }

        return this.month.compareTo(that.month);
    }

}

package com.dreamtec.bsp.statement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A common data format for transaction record for all bank statements.<br>
 * 
 * @author chinphek
 */
public class Transaction implements Comparable<Transaction> {
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");

	private String accountKey = null;
    private LocalDate date = null;
    private String description = null;
    private Double out = null;
    private Double in = null;
	private Double balance = null;
	
	@Override
	public String toString() {
		return accountKey
		+ "," + date.format(dateFormatter)
		+ "," + description
		+ "," + out
		+ "," + in
		+ "," + balance;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountkey(String accountKey) {
		this.accountKey = accountKey;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getOut() {
		return out;
	}

	public void setOut(Double out) {
		this.out = out;
	}

	public Double getIn() {
		return in;
	}

	public void setIn(Double in) {
		this.in = in;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Override
	public int compareTo(Transaction that) {
		return this.date.compareTo(that.date);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}

		if(!(obj instanceof Transaction)) {
			return super.equals(obj);
		}

		Transaction that = (Transaction) obj;

		if(!this.accountKey.equals(that.accountKey)) {
			return false;
		}

		if(!this.date.equals(that.date)) {
			return false;
		}

		if(!this.description.equals(that.description)) {
			return false;
		}

		if(this.in != that.in || this.out != that.out || this.balance != that.balance) {
			return false;
		}

		return true;
	}
	
}

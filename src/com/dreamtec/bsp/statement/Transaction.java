package com.dreamtec.bsp.statement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A common data format for transaction record for all bank statements.<br>
 * 
 * @author chinphek
 */
public class Transaction {
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private LocalDate date;
    private String description;
    private double out;
    private double in;
	private double balance;
	
	@Override
	public String toString() {
		return date.format(dateFormatter)
		+ "," + description
		+ "," + out
		+ "," + in
		+ "," + balance;
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

	public double getOut() {
		return out;
	}

	public void setOut(double out) {
		this.out = out;
	}

	public double getIn() {
		return in;
	}

	public void setIn(double in) {
		this.in = in;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
}

package com.shashi.beans;

import java.io.Serializable;
import java.sql.Timestamp;

import com.shashi.utility.IDUtil;

/**
 * A JavaBean representing a financial transaction.
 */
@SuppressWarnings("serial")
public class TransactionBean implements Serializable {

	private String transactionId;

	private String userName;

	private Timestamp transDateTime;

	private double transAmount;

	public TransactionBean() {
		super();
		this.transactionId = IDUtil.generateTransId();
		this.transDateTime = new Timestamp(System.currentTimeMillis());
	}

	public TransactionBean(String userName, double transAmount) {
		super();
		this.userName = userName;
		this.transAmount = transAmount;
		this.transactionId = IDUtil.generateTransId();
		this.transDateTime = new Timestamp(System.currentTimeMillis());

	}

	public TransactionBean(String transactionId, String userName, double transAmount) {
		super();
		this.transactionId = transactionId;
		this.userName = userName;
		this.transAmount = transAmount;
		this.transDateTime = new Timestamp(System.currentTimeMillis());
	}

	public TransactionBean(String userName, Timestamp transDateTime, double transAmount) {
		super();
		this.userName = userName;
		this.transDateTime = transDateTime;
		this.transactionId = IDUtil.generateTransId();
		this.transAmount = transAmount;
	}

	public TransactionBean(String transactionId, String userName, Timestamp transDateTime, double transAmount) {
		super();
		this.transactionId = transactionId;
		this.userName = userName;
		this.transDateTime = transDateTime;
		this.transAmount = transAmount;

	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Timestamp getTransDateTime() {
		return transDateTime;
	}

	public void setTransDateTime(Timestamp transDateTime) {
		this.transDateTime = transDateTime;
	}

	public double getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(double transAmount) {
		this.transAmount = transAmount;
	}

}

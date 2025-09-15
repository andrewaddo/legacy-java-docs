package com.shashi.beans;

import java.io.Serializable;

/**
 * A JavaBean representing a single item within an order.
 */
@SuppressWarnings("serial")
public class OrderBean implements Serializable {

	private String transactionId;
	private String productId;
	private int quantity;
	private Double amount;
	private int shipped;

	public OrderBean() {
		super();
	}

	public OrderBean(String transactionId, String productId, int quantity, Double amount) {
		super();
		this.transactionId = transactionId;
		this.productId = productId;
		this.quantity = quantity;
		this.amount = amount;
		this.shipped = 0; // By default, an order is not shipped
	}

	public OrderBean(String transactionId, String productId, int quantity, Double amount, int shipped) {
		super();
		this.transactionId = transactionId;
		this.productId = productId;
		this.quantity = quantity;
		this.amount = amount;
		this.shipped = shipped;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public int getShipped() {
		return shipped;
	}

	public void setShipped(int shipped) {
		this.shipped = shipped;
	}

}

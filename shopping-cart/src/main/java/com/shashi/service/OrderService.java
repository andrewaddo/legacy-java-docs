package com.shashi.service;

import java.util.List;

import com.shashi.beans.OrderBean;
import com.shashi.beans.OrderDetails;
import com.shashi.beans.TransactionBean;

/**
 * Service interface for managing order and transaction operations.
 */
public interface OrderService {

	/**
	 * Processes a successful payment and creates corresponding order and transaction records.
	 * @param userName The username of the customer.
	 * @param paidAmount The total amount paid.
	 * @return A status message indicating the success or failure of the order placement.
	 */
	public String paymentSuccess(String userName, double paidAmount);

	/**
	 * Adds a single order record to the database.
	 * @param order The OrderBean object to be added.
	 * @return true if the order was successfully added, false otherwise.
	 */
	public boolean addOrder(OrderBean order);

	/**
	 * Adds a transaction record to the database.
	 * @param transaction The TransactionBean object to be added.
	 * @return true if the transaction was successfully added, false otherwise.
	 */
	public boolean addTransaction(TransactionBean transaction);

	/**
	 * Counts the total number of times a specific product has been sold.
	 * @param prodId The ID of the product.
	 * @return The total quantity of the product sold.
	 */
	public int countSoldItem(String prodId);

	/**
	 * Retrieves a list of all orders from the database.
	 * @return A list of OrderBean objects.
	 */
	public List<OrderBean> getAllOrders();

	/**
	 * Retrieves all orders placed by a specific user.
	 * @param emailId The email ID of the user.
	 * @return A list of OrderBean objects for the specified user.
	 */
	public List<OrderBean> getOrdersByUserId(String emailId);

	/**
	 * Retrieves detailed information about all orders for a specific user.
	 * @param userEmailId The email ID of the user.
	 * @return A list of OrderDetails objects, containing comprehensive order information.
	 */
	public List<OrderDetails> getAllOrderDetails(String userEmailId);

	/**
	 * Marks a specific order item as shipped.
	 * @param orderId The ID of the order.
	 * @param prodId The ID of the product within the order.
	 * @return A status message indicating the success or failure of the shipping update.
	 */
	public String shipNow(String orderId, String prodId);
}

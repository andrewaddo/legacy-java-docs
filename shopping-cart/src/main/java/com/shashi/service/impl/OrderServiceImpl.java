package com.shashi.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shashi.beans.CartBean;
import com.shashi.beans.OrderBean;
import com.shashi.beans.OrderDetails;
import com.shashi.beans.TransactionBean;
import com.shashi.service.OrderService;
import com.shashi.utility.DBUtil;
import com.shashi.utility.MailMessage;

/**
 * Implementation of the OrderService interface.
 * This class handles all business logic related to orders and payments.
 */
public class OrderServiceImpl implements OrderService {

	/**
	 * Processes a successful payment by creating orders for all items in the user's cart.
	 * This is a complex, high-level method that orchestrates several other services.
	 * NOTE: This method is currently untestable due to its tight coupling with other service implementations.
	 *
	 * @param userName The username of the customer.
	 * @param paidAmount The total amount paid.
	 * @return A string indicating the status of the order placement.
	 */
	@Override
	public String paymentSuccess(String userName, double paidAmount) {
		String status = "Order Placement Failed!";

		// Get all items from the user's cart
		List<CartBean> cartItems = new CartServiceImpl().getAllCartItems(userName);

		if (cartItems.isEmpty())
			return status;

		TransactionBean transaction = new TransactionBean(userName, paidAmount);
		boolean ordered = false;

		String transactionId = transaction.getTransactionId();

		// Iterate through cart items to create individual orders
		for (CartBean item : cartItems) {

			double amount = new ProductServiceImpl().getProductPrice(item.getProdId()) * item.getQuantity();

			OrderBean order = new OrderBean(transactionId, item.getProdId(), item.getQuantity(), amount);

			// Add the order to the database
			ordered = addOrder(order);
			if (!ordered)
				break; // Stop processing if any order fails
			else {
				// Remove the item from the cart after ordering
				ordered = new CartServiceImpl().removeAProduct(item.getUserId(), item.getProdId());
			}

			if (!ordered)
				break;
			else
				// Decrement the stock quantity of the sold product
				ordered = new ProductServiceImpl().sellNProduct(item.getProdId(), item.getQuantity());

			if (!ordered)
				break;
		}

		// If all orders were placed successfully, record the transaction
		if (ordered) {
			ordered = new OrderServiceImpl().addTransaction(transaction);
			if (ordered) {

				// Send a confirmation email
				MailMessage.transactionSuccess(userName, new UserServiceImpl().getFName(userName),
						transaction.getTransactionId(), transaction.getTransAmount());

				status = "Order Placed Successfully!";
			}
		}

		return status;
	}

	/**
	 * Adds a single order record to the database.
	 *
	 * @param order The OrderBean to be added.
	 * @return true if the order was added successfully, false otherwise.
	 */
	@Override
	public boolean addOrder(OrderBean order) {
		boolean flag = false;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("insert into orders values(?,?,?,?,?)");

			ps.setString(1, order.getTransactionId());
			ps.setString(2, order.getProductId());
			ps.setInt(3, order.getQuantity());
			ps.setDouble(4, order.getAmount());
			ps.setInt(5, 0); // Shipped status defaults to 0 (not shipped)

			int k = ps.executeUpdate();

			if (k > 0)
				flag = true;

		} catch (SQLException e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * Adds a transaction record to the database.
	 *
	 * @param transaction The TransactionBean to be added.
	 * @return true if the transaction was added successfully, false otherwise.
	 */
	@Override
	public boolean addTransaction(TransactionBean transaction) {
		boolean flag = false;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("insert into transactions values(?,?,?,?)");

			ps.setString(1, transaction.getTransactionId());
			ps.setString(2, transaction.getUserName());
			ps.setTimestamp(3, transaction.getTransDateTime());
			ps.setDouble(4, transaction.getTransAmount());

			int k = ps.executeUpdate();

			if (k > 0)
				flag = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * Counts the total number of times a product has been sold.
	 *
	 * @param prodId The product ID.
	 * @return The total quantity of the product sold.
	 */
	@Override
	public int countSoldItem(String prodId) {
		int count = 0;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select sum(quantity) from orders where prodid=?");

			ps.setString(1, prodId);

			rs = ps.executeQuery();

			if (rs.next())
				count = rs.getInt(1);

		} catch (SQLException e) {
			count = 0;
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return count;
	}

	/**
	 * Retrieves a list of all orders from the database.
	 *
	 * @return A list of all OrderBean objects.
	 */
	@Override
	public List<OrderBean> getAllOrders() {
		List<OrderBean> orderList = new ArrayList<OrderBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select * from orders");

			rs = ps.executeQuery();

			while (rs.next()) {

				OrderBean order = new OrderBean(rs.getString("orderid"), rs.getString("prodid"), rs.getInt("quantity"),
						rs.getDouble("amount"), rs.getInt("shipped"));

				orderList.add(order);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return orderList;
	}

	/**
	 * Retrieves all orders for a specific user.
	 *
	 * @param emailId The user's email address.
	 * @return A list of OrderBean objects for that user.
	 */
	@Override
	public List<OrderBean> getOrdersByUserId(String emailId) {
		List<OrderBean> orderList = new ArrayList<OrderBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement(
					"SELECT * FROM orders o inner join transactions t on o.orderid = t.transid where username=?");
			ps.setString(1, emailId);
			rs = ps.executeQuery();

			while (rs.next()) {

				OrderBean order = new OrderBean(rs.getString("t.transid"), rs.getString("t.prodid"),
						rs.getInt("quantity"), rs.getDouble("t.amount"), rs.getInt("shipped"));

				orderList.add(order);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return orderList;
	}

	/**
	 * Retrieves detailed information about all orders for a specific user.
	 *
	 * @param userEmailId The user's email address.
	 * @return A list of OrderDetails objects, containing product and transaction information.
	 */
	@Override
	public List<OrderDetails> getAllOrderDetails(String userEmailId) {
		List<OrderDetails> orderList = new ArrayList<OrderDetails>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement(
					"SELECT  p.pid as prodid, o.orderid as orderid, o.shipped as shipped, p.image as image, p.pname as pname, o.quantity as qty, o.amount as amount, t.time as time FROM orders o, product p, transactions t where o.orderid=t.transid and o.orderid = t.transid and p.pid=o.prodid and t.username=?");
			ps.setString(1, userEmailId);
			rs = ps.executeQuery();

			while (rs.next()) {

				OrderDetails order = new OrderDetails();
				order.setOrderId(rs.getString("orderid"));
				order.setProdImage(rs.getAsciiStream("image"));
				order.setProdName(rs.getString("pname"));
				order.setQty(rs.getString("qty"));
				order.setAmount(rs.getString("amount"));
				order.setTime(rs.getTimestamp("time"));
				order.setProductId(rs.getString("prodid"));
				order.setShipped(rs.getInt("shipped"));
				orderList.add(order);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return orderList;
	}

	/**
	 * Marks a specific item within an order as shipped.
	 *
	 * @param orderId The ID of the order.
	 * @param prodId The ID of the product within the order.
	 * @return A string indicating the status of the operation.
	 */
	@Override
	public String shipNow(String orderId, String prodId) {
		String status = "FAILURE";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("update orders set shipped=1 where orderid=? and prodid=? and shipped=0");

			ps.setString(1, orderId);
			ps.setString(2, prodId);

			int k = ps.executeUpdate();

			if (k > 0) {
				status = "Order Has been shipped successfully!!";
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}

}

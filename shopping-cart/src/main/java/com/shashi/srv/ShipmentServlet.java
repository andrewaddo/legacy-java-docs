package com.shashi.srv;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shashi.service.impl.OrderServiceImpl;
import com.shashi.service.impl.UserServiceImpl;
import com.shashi.utility.MailMessage;

/**
 * Servlet implementation class ShipmentServlet
 * 
 * This servlet handles the action of marking an order item as shipped.
 */
@WebServlet("/ShipmentServlet")
public class ShipmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ShipmentServlet() {
		super();
	}

	/**
	 * Handles the HTTP GET request to ship an order item.
	 * 
	 * @param request The HttpServletRequest object.
	 * @param response The HttpServletResponse object.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userType = (String) session.getAttribute("usertype");
		// The user must be logged in to access this servlet.
		if (userType == null) {

			response.sendRedirect("login.jsp?message=Access Denied, Login Again!!");
			return;
		}

		String orderId = request.getParameter("orderid");
		String prodId = request.getParameter("prodid");
		String userName = request.getParameter("userid");
		Double amount = Double.parseDouble(request.getParameter("amount"));

		// This servlet is untestable because it directly instantiates multiple service classes.
		String status = new OrderServiceImpl().shipNow(orderId, prodId);
		
		String pagename = "shippedItems.jsp";
		if ("FAILURE".equalsIgnoreCase(status)) {
			pagename = "unshippedItems.jsp";
		} else {
			// If shipping is successful, send a notification email.
			MailMessage.orderShipped(userName, new UserServiceImpl().getFName(userName), orderId, amount);
		}
		PrintWriter pw = response.getWriter();
		response.setContentType("text/html");

		RequestDispatcher rd = request.getRequestDispatcher(pagename);

		rd.include(request, response);

		// Use JavaScript to display the status message on the included page.
		pw.println("<script>document.getElementById('message').innerHTML='" + status + "'</script>");
	}

	/**
	 * Handles the HTTP POST request by delegating to the doGet method.
	 * 
	 * @param request The HttpServletRequest object.
	 * @param response The HttpServletResponse object.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}

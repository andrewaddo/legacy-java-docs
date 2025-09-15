package com.shashi.srv;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shashi.service.impl.ProductServiceImpl;

/**
 * Servlet implementation class RemoveProductSrv
 * 
 * This servlet handles the removal of a product by an admin.
 */
@WebServlet("/RemoveProductSrv")
public class RemoveProductSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RemoveProductSrv() {
		super();

	}

	/**
	 * Handles the HTTP GET request for removing a product.
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
		String userName = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");

		// Admin authentication check
		if (userType == null || !userType.equals("admin")) {

			response.sendRedirect("login.jsp?message=Access Denied, Login As Admin!!");
			return;

		}

		else if (userName == null || password == null) {

			response.sendRedirect("login.jsp?message=Session Expired, Login Again!!");
			return;
		}

		// Get product ID from the request
		String prodId = request.getParameter("prodid");

		ProductServiceImpl product = new ProductServiceImpl();

		String status = product.removeProduct(prodId);

		RequestDispatcher rd = request.getRequestDispatcher("removeProduct.jsp?message=" + status);

		rd.forward(request, response);

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

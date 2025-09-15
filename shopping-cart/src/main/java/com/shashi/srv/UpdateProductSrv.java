package com.shashi.srv;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shashi.beans.ProductBean;
import com.shashi.service.impl.ProductServiceImpl;

/**
 * Servlet implementation class UpdateProductSrv
 * 
 * This servlet handles updating a product's details (without updating the image).
 */
@WebServlet("/UpdateProductSrv")
public class UpdateProductSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UpdateProductSrv() {
		super();

	}

	/**
	 * Handles the HTTP GET request for updating a product.
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

		} else if (userName == null || password == null) {

			response.sendRedirect("login.jsp?message=Session Expired, Login Again!!");
			return;
		}


		String prodId = request.getParameter("pid");
		String prodName = request.getParameter("name");
		String prodType = request.getParameter("type");
		String prodInfo = request.getParameter("info");
		Double prodPrice = Double.parseDouble(request.getParameter("price"));
		Integer prodQuantity = Integer.parseInt(request.getParameter("quantity"));

		ProductBean product = new ProductBean();
		product.setProdId(prodId);
		product.setProdName(prodName);
		product.setProdInfo(prodInfo);
		product.setProdPrice(prodPrice);
		product.setProdQuantity(prodQuantity);
		product.setProdType(prodType);

		// This servlet is untestable because it directly instantiates the ProductServiceImpl
		// and calls a complex, untestable method within that service.
		ProductServiceImpl dao = new ProductServiceImpl();

		String status = dao.updateProductWithoutImage(prodId, product);

		RequestDispatcher rd = request
				.getRequestDispatcher("updateProduct.jsp?prodid=" + prodId + "&message=" + status);
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

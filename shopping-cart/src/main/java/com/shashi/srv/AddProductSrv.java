package com.shashi.srv;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.shashi.service.impl.ProductServiceImpl;

/**
 * Servlet implementation class AddProductSrv
 * 
 * This servlet handles the addition of new products by an admin.
 * It is configured to handle multipart/form-data requests for file uploads.
 */
@WebServlet("/AddProductSrv")
@MultipartConfig(maxFileSize = 16177215) // 16MB max file size
public class AddProductSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Handles the HTTP GET request by delegating to doPost.
	 * 
	 * @param request The HttpServletRequest object.
	 * @param response The HttpServletResponse object.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// In this case, GET requests are handled by the same logic as POST,
		// though typically GET should be used for idempotent operations.
		doPost(request, response);
	}

	/**
	 * Handles the HTTP POST request for adding a new product.
	 * 
	 * @param request The HttpServletRequest object.
	 * @param response The HttpServletResponse object.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userType = (String) session.getAttribute("usertype");
		String userName = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");

		// Admin authentication check
		if (userType == null || !userType.equals("admin")) {

			response.sendRedirect("login.jsp?message=Access Denied!");
			return;

		}

		else if (userName == null || password == null) {

			response.sendRedirect("login.jsp?message=Session Expired, Login Again to Continue!");
			return;
		}

		String status = "Product Registration Failed!";
		
		// Retrieve all product details from the request
		String prodName = request.getParameter("name");
		String prodType = request.getParameter("type");
		String prodInfo = request.getParameter("info");
		double prodPrice = Double.parseDouble(request.getParameter("price"));
		int prodQuantity = Integer.parseInt(request.getParameter("quantity"));

		// Retrieve the image part from the multipart request
		Part part = request.getPart("image");

		InputStream inputStream = part.getInputStream();

		InputStream prodImage = inputStream;

		// This servlet directly instantiates the service, which is not ideal for testing or maintenance.
		ProductServiceImpl product = new ProductServiceImpl();

		status = product.addProduct(prodName, prodType, prodInfo, prodPrice, prodQuantity, prodImage);

		RequestDispatcher rd = request.getRequestDispatcher("addProduct.jsp?message=" + status);
		rd.forward(request, response);

	}

}

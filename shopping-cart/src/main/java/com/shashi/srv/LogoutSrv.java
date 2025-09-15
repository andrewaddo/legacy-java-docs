package com.shashi.srv;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LogoutSrv
 * 
 * This servlet handles the user logout process.
 */
@WebServlet("/LogoutSrv")
public class LogoutSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public LogoutSrv() {
		super();

	}

	/**
	 * Handles the HTTP GET request for logging out.
	 * It invalidates the current session attributes and forwards the user to the login page.
	 * 
	 * @param request The HttpServletRequest object.
	 * @param response The HttpServletResponse object.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

		HttpSession session = request.getSession();

		// Invalidate session attributes by setting them to null
		session.setAttribute("username", null);
		session.setAttribute("password", null);
		session.setAttribute("usertype", null);
		session.setAttribute("userdata", null);

		RequestDispatcher rd = request.getRequestDispatcher("login.jsp?message=Successfully Logged Out!");

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

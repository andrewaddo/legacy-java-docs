package com.shashi.srv;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shashi.utility.MailMessage;

/**
 * Servlet implementation class FansMessage
 * 
 * This servlet handles messages submitted by users through a contact/feedback form.
 */
@WebServlet("/fansMessage")
public class FansMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Handles the HTTP GET request for submitting a fan message.
	 * 
	 * @param request The HttpServletRequest object.
	 * @param response The HttpServletResponse object.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String comments = request.getParameter("comments");

		// Construct the HTML email body
		String htmlTextMessage = "" + "<html>" + "<body>"
				+ "<h2 style='color:green;'>Message to Ellison Electronics</h2>" + ""
				+ "Fans Message Received !!<br/><br/> Name: " + name + "," + "<br/><br/> Email Id: " + email
				+ "<br><br/>" + "Comment: " + "<span style='color:grey;'>" + comments + "</span>"
				+ "<br/><br/>We are glad that fans are choosing us! <br/><br/>Thanks & Regards<br/><br/>Auto Generated Mail"
				+ "</body>" + "</html>";
		
		// Send the email using the MailMessage utility
		String message = MailMessage.sendMessage("ellison.alumni@gmail.com", "Fans Message | " + name + " | " + email,
				htmlTextMessage);
		
		// Set a status message based on the mail sending result
		if ("SUCCESS".equals(message)) {
			message = "Comments Sent Successfully";
		} else {
			message = "Failed: Please Configure mailer.email and password in application.properties first";
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");

		rd.include(request, response);

		// Display an alert to the user with the status message.
		response.getWriter().print("<script>alert('" + message + "')</script>");

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

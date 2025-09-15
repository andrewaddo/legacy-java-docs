package com.shashi.srv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shashi.service.impl.ProductServiceImpl;

/**
 * Servlet implementation class ShowImage
 * 
 * This servlet retrieves and displays a product image from the database.
 */
@WebServlet("/ShowImage")
public class ShowImage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ShowImage() {
		super();
	}

	/**
	 * Handles the HTTP GET request to display an image.
	 * 
	 * @param request The HttpServletRequest object.
	 * @param response The HttpServletResponse object.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String prodId = request.getParameter("pid");

		// This servlet is untestable because it directly instantiates the ProductServiceImpl.
		ProductServiceImpl dao = new ProductServiceImpl();

		byte[] image = dao.getImage(prodId);

		// If no image is found in the database, a default "noimage.jpg" is served.
		if (image == null) {
			File fnew = new File(request.getServletContext().getRealPath("images/noimage.jpg"));
			BufferedImage originalImage = ImageIO.read(fnew);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", baos);
			image = baos.toByteArray();
		}

		ServletOutputStream sos = null;

		sos = response.getOutputStream();

		// Write the image bytes to the response output stream.
		sos.write(image);

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

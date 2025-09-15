package com.shashi.utility;

import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Utility class for sending emails using the Jakarta Mail API.
 * Configured specifically for sending emails via Gmail SMTP.
 */
public class JavaMailUtil {

	/**
	 * Sends a simple welcome email.
	 * 
	 * @param recipientMailId The recipient's email address.
	 * @throws MessagingException if there is an error during email sending.
	 */
	public static void sendMail(String recipientMailId) throws MessagingException {

		System.out.println("Preparing to send Mail");
		Properties properties = new Properties();
		String host = "smtp.gmail.com";
		properties.put("mail.smtp.host", host);
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", "587");

		// Load email credentials from application.properties file
		ResourceBundle rb = ResourceBundle.getBundle("application");
		String emailId = rb.getString("mailer.email");
		String passWord = rb.getString("mailer.password");

		properties.put("mail.user", emailId);
		properties.put("mail.password", passWord);

		// Create a session with an authenticator
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailId, passWord);
			}

		});

		Message message = prepareMessage(session, emailId, recipientMailId);

		Transport.send(message);

		System.out.println("Message Sent Successfully!");

	}

	/**
	 * Prepares a simple plain text welcome message.
	 * 
	 * @param session The mail session.
	 * @param myAccountEmail The sender's email address.
	 * @param recipientEmail The recipient's email address.
	 * @return A prepared Message object.
	 */
	private static Message prepareMessage(Session session, String myAccountEmail, String recipientEmail) {

		try {

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(myAccountEmail));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
			message.setSubject("Welcome to Ellison Electronics");
			message.setText("Hey! " + recipientEmail + ", Thanks  for Signing Up with us!");
			return message;

		} catch (Exception exception) {
			Logger.getLogger(JavaMailUtil.class.getName()).log(Level.SEVERE, null, exception);
		}
		return null;

	}

	/**
	 * Sends a custom HTML email.
	 * 
	 * @param recipient The recipient's email address.
	 * @param subject The subject of the email.
	 * @param htmlTextMessage The HTML content of the email.
	 * @throws MessagingException if there is an error during email sending.
	 */
	public static void sendMail(String recipient, String subject, String htmlTextMessage) throws MessagingException {

		System.out.println("Preparing to send Mail");
		Properties properties = new Properties();
		String host = "smtp.gmail.com";
		properties.put("mail.smtp.host", host);
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", "587");

		ResourceBundle rb = ResourceBundle.getBundle("application");

		String emailId = rb.getString("mailer.email");
		String passWord = rb.getString("mailer.password");

		properties.put("mail.user", emailId);
		properties.put("mail.password", passWord);

		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailId, passWord);
			}

		});

		Message message = prepareMessage(session, emailId, recipient, subject, htmlTextMessage);

		Transport.send(message);

		System.out.println("Message Sent Successfully!");

	}

	/**
	 * Prepares a MimeMessage with HTML content.
	 * 
	 * @param session The mail session.
	 * @param myAccountEmail The sender's email address.
	 * @param recipientEmail The recipient's email address.
	 * @param subject The subject of the email.
	 * @param htmlTextMessage The HTML content of the email.
	 * @return A prepared Message object with HTML content.
	 */
	private static Message prepareMessage(Session session, String myAccountEmail, String recipientEmail, String subject,
			String htmlTextMessage) {

		try {

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(myAccountEmail));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
			message.setSubject(subject);
			message.setContent(htmlTextMessage, "text/html");
			return message;

		} catch (Exception exception) {
			Logger.getLogger(JavaMailUtil.class.getName()).log(Level.SEVERE, null, exception);
		}
		return null;

	}
}

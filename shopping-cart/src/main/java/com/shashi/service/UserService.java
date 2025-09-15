package com.shashi.service;

import com.shashi.beans.UserBean;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {

	/**
	 * Registers a new user with individual details.
	 * @param userName User's full name.
	 * @param mobileNo User's mobile number.
	 * @param emailId User's email address.
	 * @param address User's address.
	 * @param pinCode User's pincode.
	 * @param password User's password.
	 * @return A status message indicating success or failure.
	 */
	public String registerUser(String userName, Long mobileNo, String emailId, String address, int pinCode,
			String password);

	/**
	 * Registers a new user using a UserBean object.
	 * @param user The UserBean object containing user details.
	 * @return A status message indicating success or failure.
	 */
	public String registerUser(UserBean user);

	/**
	 * Checks if a user is already registered with the given email ID.
	 * @param emailId The email ID to check.
	 * @return true if the user is registered, false otherwise.
	 */
	public boolean isRegistered(String emailId);

	/**
	 * Validates user credentials.
	 * @param emailId The user's email ID.
	 * @param password The user's password.
	 * @return "valid" if credentials are correct, otherwise an error message.
	 */
	public String isValidCredential(String emailId, String password);

	/**
	 * Retrieves user details based on email ID and password.
	 * @param emailId The user's email ID.
	 * @param password The user's password.
	 * @return A UserBean object containing user details, or null if not found.
	 */
	public UserBean getUserDetails(String emailId, String password);

	/**
	 * Retrieves the first name of a user based on their email ID.
	 * @param emailId The user's email ID.
	 * @return The first name of the user.
	 */
	public String getFName(String emailId);

	/**
	 * Retrieves the address of a user based on their user ID (email ID).
	 * @param userId The user's ID (email ID).
	 * @return The address of the user.
	 */
	public String getUserAddr(String userId);

}

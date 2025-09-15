package com.shashi.beans;

import java.io.Serializable;

/**
 * A JavaBean representing a user in the shopping system.
 */
@SuppressWarnings("serial")
public class UserBean implements Serializable {

	private String name;
	private Long mobile;
	private String email;
	private String address;
	private int pinCode;
	private String password;

	public UserBean() {
	}

	/**
	 * Constructs a new UserBean with the specified details.
	 *
	 * @param userName The user's full name.
	 * @param mobileNo The user's mobile number.
	 * @param emailId The user's email address.
	 * @param address The user's address.
	 * @param pinCode The user's postal code.
	 * @param password The user's password.
	 */
	public UserBean(String userName, Long mobileNo, String emailId, String address, int pinCode, String password) {
		super();
		this.name = userName;
		this.mobile = mobileNo;
		this.email = emailId;
		this.address = address;
		this.pinCode = pinCode;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPinCode() {
		return pinCode;
	}

	public void setPinCode(int pinCode) {
		this.pinCode = pinCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

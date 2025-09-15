package com.shashi.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for generating unique IDs based on timestamps.
 */
public class IDUtil {

	/**
	 * Generates a unique product ID.
	 * The ID is prefixed with "P" followed by a timestamp (yyyyMMddhhmmss).
	 * 
	 * @return A unique product ID string.
	 */
	public static String generateId() {
		String pId = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		pId = sdf.format(new Date());
		pId = "P" + pId;

		return pId;
	}

	/**
	 * Generates a unique transaction ID.
	 * The ID is prefixed with "T" followed by a timestamp (yyyyMMddhhmmss).
	 * 
	 * @return A unique transaction ID string.
	 */
	public static String generateTransId() {
		String tId = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		tId = sdf.format(new Date());
		tId = "T" + tId;

		return tId;
	}
}

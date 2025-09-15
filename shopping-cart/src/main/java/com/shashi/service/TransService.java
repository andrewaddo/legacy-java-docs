package com.shashi.service;

/**
 * Service interface for managing transaction-related operations.
 */
public interface TransService {

	/**
	 * Retrieves the user ID associated with a given transaction ID.
	 * @param transId The transaction ID.
	 * @return The user ID (email) of the user who made the transaction.
	 */
	public String getUserId(String transId);
}

package com.shashi.service;

import java.util.List;

import com.shashi.beans.DemandBean;

/**
 * Service interface for managing product demand (for out-of-stock items).
 */
public interface DemandService {

	/**
	 * Adds a product to the user's demand list.
	 * @param userId The ID of the user.
	 * @param prodId The ID of the product.
	 * @param demandQty The quantity of the product demanded.
	 * @return true if the demand was successfully added, false otherwise.
	 */
	public boolean addProduct(String userId, String prodId, int demandQty);

	/**
	 * Adds a product to the user's demand list using a DemandBean.
	 * @param userDemandBean The DemandBean object containing user and product demand details.
	 * @return true if the demand was successfully added, false otherwise.
	 */
	public boolean addProduct(DemandBean userDemandBean);

	/**
	 * Removes a product from a user's demand list.
	 * @param userId The ID of the user.
	 * @param prodId The ID of the product to remove from demand.
	 * @return true if the product was successfully removed from demand, false otherwise.
	 */
	public boolean removeProduct(String userId, String prodId);

	/**
	 * Retrieves a list of all users who have demanded a specific product.
	 * @param prodId The ID of the product.
	 * @return A list of DemandBean objects representing the demands for the product.
	 */
	public List<DemandBean> haveDemanded(String prodId);

}

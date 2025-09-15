package com.shashi.service;

import java.util.List;

import com.shashi.beans.CartBean;

/**
 * Service interface for managing shopping cart operations.
 */
public interface CartService {

	/**
	 * Adds a product to the user's cart.
	 * @param userId The ID of the user.
	 * @param prodId The ID of the product.
	 * @param prodQty The quantity of the product to add.
	 * @return A status message indicating success or failure.
	 */
	public String addProductToCart(String userId, String prodId, int prodQty);

	/**
	 * Updates the quantity of a product in the user's cart, or adds it if it doesn't exist.
	 * @param userId The ID of the user.
	 * @param prodId The ID of the product.
	 * @param prodQty The new quantity of the product in the cart.
	 * @return A status message indicating success or failure.
	 */
	public String updateProductToCart(String userId, String prodId, int prodQty);

	/**
	 * Retrieves all items currently in a user's cart.
	 * @param userId The ID of the user.
	 * @return A list of CartBean objects representing the cart items.
	 */
	public List<CartBean> getAllCartItems(String userId);

	/**
	 * Gets the total count of items (sum of quantities) in a user's cart.
	 * @param userId The ID of the user.
	 * @return The total count of items in the cart.
	 */
	public int getCartCount(String userId);

	/**
	 * Gets the quantity of a specific item in a user's cart.
	 * @param userId The ID of the user.
	 * @param itemId The ID of the item (product).
	 * @return The quantity of the specified item in the cart.
	 */
	public int getCartItemCount(String userId, String itemId);

	/**
	 * Removes one unit of a product from the user's cart. If quantity becomes zero, the product is removed.
	 * @param userId The ID of the user.
	 * @param prodId The ID of the product to remove.
	 * @return A status message indicating success or failure.
	 */	
	public String removeProductFromCart(String userId, String prodId);

	/**
	 * Removes a product entirely from the user's cart, regardless of quantity.
	 * @param userId The ID of the user.
	 * @param prodId The ID of the product to remove.
	 * @return true if the product was successfully removed, false otherwise.
	 */
	public boolean removeAProduct(String userId, String prodId);

}

package com.shashi.service;

import java.io.InputStream;
import java.util.List;

import com.shashi.beans.ProductBean;

/**
 * Service interface for managing product-related operations.
 */
public interface ProductService {

	/**
	 * Adds a new product to the system.
	 * @param prodName Name of the product.
	 * @param prodType Type or category of the product.
	 * @param prodInfo Information or description of the product.
	 * @param prodPrice Price of the product.
	 * @param prodQuantity Initial quantity of the product.
	 * @param prodImage InputStream of the product's image.
	 * @return A status message indicating success or failure.
	 */
	public String addProduct(String prodName, String prodType, String prodInfo, double prodPrice, int prodQuantity,
			InputStream prodImage);

	/**
	 * Adds a new product to the system using a ProductBean object.
	 * @param product The ProductBean object containing product details.
	 * @return A status message indicating success or failure.
	 */
	public String addProduct(ProductBean product);

	/**
	 * Removes a product from the system.
	 * @param prodId The ID of the product to remove.
	 * @return A status message indicating success or failure.
	 */
	public String removeProduct(String prodId);

	/**
	 * Updates an existing product's details.
	 * @param prevProduct The original ProductBean before updates.
	 * @param updatedProduct The ProductBean with updated details.
	 * @return A status message indicating success or failure.
	 */
	public String updateProduct(ProductBean prevProduct, ProductBean updatedProduct);

	/**
	 * Updates the price of a specific product.
	 * @param prodId The ID of the product to update.
	 * @param updatedPrice The new price of the product.
	 * @return A status message indicating success or failure.
	 */
	public String updateProductPrice(String prodId, double updatedPrice);

	/**
	 * Retrieves a list of all products in the system.
	 * @return A list of ProductBean objects.
	 */
	public List<ProductBean> getAllProducts();

	/**
	 * Retrieves a list of products filtered by their type.
	 * @param type The type or category of products to retrieve.
	 * @return A list of ProductBean objects matching the specified type.
	 */
	public List<ProductBean> getAllProductsByType(String type);

	/**
	 * Searches for products based on a search term across product type, name, or info.
	 * @param search The search term.
	 * @return A list of ProductBean objects matching the search criteria.
	 */
	public List<ProductBean> searchAllProducts(String search);

	/**
	 * Retrieves the image data for a specific product.
	 * @param prodId The ID of the product.
	 * @return A byte array containing the product image data.
	 */
	public byte[] getImage(String prodId);

	/**
	 * Retrieves all details for a specific product.
	 * @param prodId The ID of the product.
	 * @return A ProductBean object containing all details of the specified product.
	 */
	public ProductBean getProductDetails(String prodId);

	/**
	 * Updates a product's details without changing its image.
	 * @param prevProductId The ID of the product to update.
	 * @param updatedProduct The ProductBean with updated details (excluding image).
	 * @return A status message indicating success or failure.
	 */
	public String updateProductWithoutImage(String prevProductId, ProductBean updatedProduct);

	/**
	 * Retrieves the price of a specific product.
	 * @param prodId The ID of the product.
	 * @return The price of the product.
	 */
	public double getProductPrice(String prodId);

	/**
	 * Decrements the quantity of a product by a specified number (e.g., after a sale).
	 * @param prodId The ID of the product.
	 * @param n The number of units to sell.
	 * @return true if the operation was successful, false otherwise.
	 */
	public boolean sellNProduct(String prodId, int n);

	/**
	 * Retrieves the current quantity of a specific product in stock.
	 * @param prodId The ID of the product.
	 * @return The quantity of the product in stock.
	 */
	public int getProductQuantity(String prodId);
}

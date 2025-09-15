package com.shashi;

import com.shashi.beans.ProductBean;
import com.shashi.service.impl.ProductServiceImpl;
import com.shashi.utility.DBUtil;
import com.shashi.utility.IDUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductServiceImpl productService;

    // Mocks for static utilities
    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private MockedStatic<IDUtil> idUtilMockedStatic;

    // Mocks for JDBC objects
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        productService = new ProductServiceImpl();

        // Mock the static utility classes
        dbUtilMockedStatic = mockStatic(DBUtil.class);
        idUtilMockedStatic = mockStatic(IDUtil.class);

        // Mock the JDBC objects
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        // Define the behavior of the mocked static methods
        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        idUtilMockedStatic.when(IDUtil::generateId).thenReturn("test-prod-id");

        // Define the behavior of the mocked connection
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Define default behavior for executeQuery
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @AfterEach
    void tearDown() {
        // Close the static mocks
        dbUtilMockedStatic.close();
        idUtilMockedStatic.close();
    }

    // =============== Tests for addProduct ===============

    @Test
    void testAddProduct_WhenSuccessful_ShouldReturnSuccessMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);
        ProductBean product = new ProductBean();
        product.setProdId("test-prod-id");

        // Act
        String status = productService.addProduct(product);

        // Assert
        assertEquals("Product Added Successfully with Product Id: test-prod-id", status);
    }

    @Test
    void testAddProduct_WhenDatabaseInsertFails_ShouldReturnFailedMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);
        ProductBean product = new ProductBean();

        // Act
        String status = productService.addProduct(product);

        // Assert
        assertEquals("Product Updation Failed!", status);
    }

    @Test
    void testAddProduct_WhenSqlExceptionOccurs_ShouldReturnErrorMessage() throws SQLException {
        // Arrange
        String exceptionMessage = "SQL error";
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException(exceptionMessage));
        ProductBean product = new ProductBean();

        // Act
        String status = productService.addProduct(product);

        // Assert
        assertEquals("Error: " + exceptionMessage, status);
    }

    // =============== Tests for removeProduct ===============

    @Test
    void testRemoveProduct_WhenSuccessful_ShouldReturnSuccessMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1).thenReturn(1);

        // Act
        String status = productService.removeProduct("any-prod-id");

        // Assert
        assertEquals("Product Removed Successfully!", status);
        verify(preparedStatement, times(2)).executeUpdate();
    }

    @Test
    void testRemoveProduct_WhenProductNotFound_ShouldReturnFailedMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        String status = productService.removeProduct("any-prod-id");

        // Assert
        assertEquals("Product Removal Failed!", status);
    }

    @Test
    void testRemoveProduct_WhenSqlExceptionOccurs_ShouldReturnErrorMessage() throws SQLException {
        // Arrange
        String exceptionMessage = "SQL error on delete";
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException(exceptionMessage));

        // Act
        String status = productService.removeProduct("any-prod-id");

        // Assert
        assertEquals("Error: " + exceptionMessage, status);
    }

    // =============== Tests for updateProduct ===============

    @Test
    void testUpdateProduct_WhenSuccessful_ShouldReturnSuccessMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);
        ProductBean product = new ProductBean();
        product.setProdId("prod-123");

        // Act
        String status = productService.updateProduct(product, product);

        // Assert
        assertEquals("Product Updated Successfully!", status);
    }

    @Test
    void testUpdateProduct_WhenProductIDsMismatch_ShouldReturnMismatchMessage() {
        // Arrange
        ProductBean prevProduct = new ProductBean();
        prevProduct.setProdId("prod-123");
        ProductBean updatedProduct = new ProductBean();
        updatedProduct.setProdId("prod-456");

        // Act
        String status = productService.updateProduct(prevProduct, updatedProduct);

        // Assert
        assertEquals("Both Products are Different, Updation Failed!", status);
    }

    // =============== Tests for updateProductPrice ===============

    @Test
    void testUpdateProductPrice_WhenSuccessful_ShouldReturnSuccessMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        String status = productService.updateProductPrice("prod-123", 99.99);

        // Assert
        assertEquals("Price Updated Successfully!", status);
    }

    @Test
    void testUpdateProductPrice_WhenUpdateFailsInDB_ShouldReturnFailedMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        String status = productService.updateProductPrice("prod-123", 99.99);

        // Assert
        assertEquals("Price Updation Failed!", status);
    }

    @Test
    void testUpdateProductPrice_WhenSqlExceptionOccurs_ShouldReturnErrorMessage() throws SQLException {
        // Arrange
        String exceptionMessage = "SQL error on price update";
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException(exceptionMessage));

        // Act
        String status = productService.updateProductPrice("prod-123", 99.99);

        // Assert
        assertEquals("Error: " + exceptionMessage, status);
    }

    // =============== Tests for getAllProducts ===============

    @Test
    void testGetAllProducts_WhenTwoProductsExist_ShouldReturnListOfTwo() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("p1", "p2");
        when(resultSet.getString(2)).thenReturn("Product 1", "Product 2");

        // Act
        List<ProductBean> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("p1", products.get(0).getProdId());
        assertEquals("Product 2", products.get(1).getProdName());
    }

    @Test
    void testGetAllProducts_WhenNoProductsExist_ShouldReturnEmptyList() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        List<ProductBean> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    // =============== Tests for getProductDetails ===============

    @Test
    void testGetProductDetails_WhenProductFound_ShouldReturnProduct() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("p1");
        when(resultSet.getString(2)).thenReturn("Product 1");

        // Act
        ProductBean product = productService.getProductDetails("p1");

        // Assert
        assertNotNull(product);
        assertEquals("p1", product.getProdId());
        assertEquals("Product 1", product.getProdName());
    }

    @Test
    void testGetProductDetails_WhenProductNotFound_ShouldReturnNull() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        ProductBean product = productService.getProductDetails("p1");

        // Assert
        assertNull(product);
    }

    // =============== Tests for getImage ===============

    @Test
    void testGetImage_WhenImageFound_ShouldReturnByteArray() throws SQLException {
        // Arrange
        byte[] dummyImage = new byte[]{1, 2, 3};
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBytes("image")).thenReturn(dummyImage);

        // Act
        byte[] image = productService.getImage("p1");

        // Assert
        assertNotNull(image);
        assertArrayEquals(dummyImage, image);
    }

    @Test
    void testGetImage_WhenImageNotFound_ShouldReturnNull() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        byte[] image = productService.getImage("p1");

        // Assert
        assertNull(image);
    }

    // =============== Tests for getProductPrice ===============

    @Test
    void testGetProductPrice_WhenProductFound_ShouldReturnPrice() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getDouble("pprice")).thenReturn(199.99);

        // Act
        double price = productService.getProductPrice("p1");

        // Assert
        assertEquals(199.99, price);
    }

    @Test
    void testGetProductPrice_WhenProductNotFound_ShouldReturnZero() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        double price = productService.getProductPrice("p1");

        // Assert
        assertEquals(0.0, price);
    }

    // =============== Tests for getProductQuantity ===============

    @Test
    void testGetProductQuantity_WhenProductFound_ShouldReturnQuantity() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("pquantity")).thenReturn(10);

        // Act
        int quantity = productService.getProductQuantity("p1");

        // Assert
        assertEquals(10, quantity);
    }

    @Test
    void testGetProductQuantity_WhenProductNotFound_ShouldReturnZero() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        int quantity = productService.getProductQuantity("p1");

        // Assert
        assertEquals(0, quantity);
    }

    // =============== Tests for sellNProduct ===============

    @Test
    void testSellNProduct_WhenSuccessful_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = productService.sellNProduct("p1", 5);

        // Assert
        assertTrue(result);
    }

    @Test
    void testSellNProduct_WhenUpdateFails_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        boolean result = productService.sellNProduct("p1", 5);

        // Assert
        assertFalse(result);
    }

    @Test
    void testSellNProduct_WhenSqlExceptionOccurs_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("DB error"));

        // Act
        boolean result = productService.sellNProduct("p1", 5);

        // Assert
        assertFalse(result);
    }
}

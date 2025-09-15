package com.shashi;

import com.shashi.beans.CartBean;
import com.shashi.service.impl.CartServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    private CartServiceImpl cartService;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        cartService = new CartServiceImpl();

        dbUtilMockedStatic = mockStatic(DBUtil.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @AfterEach
    void tearDown() {
        dbUtilMockedStatic.close();
    }

    // =============== Tests for getAllCartItems ===============

    @Test
    void testGetAllCartItems_WhenItemsExist_ShouldReturnCartList() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("username")).thenReturn("user1");
        when(resultSet.getString("prodid")).thenReturn("prod1", "prod2");
        when(resultSet.getString("quantity")).thenReturn("2", "3");

        // Act
        List<CartBean> cartItems = cartService.getAllCartItems("user1");

        // Assert
        assertNotNull(cartItems);
        assertEquals(2, cartItems.size());
        assertEquals("prod1", cartItems.get(0).getProdId());
        assertEquals(3, cartItems.get(1).getQuantity());
    }

    @Test
    void testGetAllCartItems_WhenNoItemsExist_ShouldReturnEmptyList() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        List<CartBean> cartItems = cartService.getAllCartItems("user1");

        // Assert
        assertNotNull(cartItems);
        assertTrue(cartItems.isEmpty());
    }

    // =============== Tests for getCartCount ===============

    @Test
    void testGetCartCount_WhenItemsExist_ShouldReturnSum() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(5);

        // Act
        int count = cartService.getCartCount("user1");

        // Assert
        assertEquals(5, count);
    }

    @Test
    void testGetCartCount_WhenNoItemsExist_ShouldReturnZero() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        int count = cartService.getCartCount("user1");

        // Assert
        assertEquals(0, count);
    }

    // =============== Tests for removeProductFromCart ===============

    @Test
    void testRemoveProductFromCart_WhenItemNotInCart_ShouldReturnNotAvailable() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        String status = cartService.removeProductFromCart("user1", "prod1");

        // Assert
        assertEquals("Product Not Available in the cart!", status);
    }

    @Test
    void testRemoveProductFromCart_WhenQuantityGreaterThanOne_ShouldDecrementQuantity() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("quantity")).thenReturn(3);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        String status = cartService.removeProductFromCart("user1", "prod1");

        // Assert
        assertEquals("Product Successfully removed from the Cart!", status);
        verify(preparedStatement).setInt(1, 2); // 3 - 1 = 2
    }

    @Test
    void testRemoveProductFromCart_WhenQuantityIsOne_ShouldDeleteProduct() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("quantity")).thenReturn(1);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        String status = cartService.removeProductFromCart("user1", "prod1");

        // Assert
        assertEquals("Product Successfully removed from the Cart!", status);
        // Verifies that the 'delete' statement was prepared and executed
        verify(connection).prepareStatement("delete from usercart where username=? and prodid=?");
        verify(preparedStatement).executeUpdate();
    }

    // =============== Tests for removeAProduct ===============

    @Test
    void testRemoveAProduct_WhenSuccessful_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = cartService.removeAProduct("user1", "prod1");

        // Assert
        assertTrue(result);
    }

    @Test
    void testRemoveAProduct_WhenFails_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        boolean result = cartService.removeAProduct("user1", "prod1");

        // Assert
        assertFalse(result);
    }

}

package com.shashi;

import com.shashi.beans.OrderBean;
import com.shashi.beans.OrderDetails;
import com.shashi.beans.TransactionBean;
import com.shashi.service.impl.OrderServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        orderService = new OrderServiceImpl();

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

    // =============== Tests for addOrder ===============

    @Test
    void testAddOrder_WhenSuccessful_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);
        OrderBean order = new OrderBean("trans1", "prod1", 1, 100.0, 0);

        // Act
        boolean result = orderService.addOrder(order);

        // Assert
        assertTrue(result);
    }

    @Test
    void testAddOrder_WhenInsertFails_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);
        OrderBean order = new OrderBean("trans1", "prod1", 1, 100.0, 0);

        // Act
        boolean result = orderService.addOrder(order);

        // Assert
        assertFalse(result);
    }

    @Test
    void testAddOrder_WhenSqlExceptionOccurs_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());
        OrderBean order = new OrderBean("trans1", "prod1", 1, 100.0, 0);

        // Act
        boolean result = orderService.addOrder(order);

        // Assert
        assertFalse(result);
    }

    // =============== Tests for addTransaction ===============

    @Test
    void testAddTransaction_WhenSuccessful_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);
        TransactionBean transaction = new TransactionBean("user1", 100.0);

        // Act
        boolean result = orderService.addTransaction(transaction);

        // Assert
        assertTrue(result);
    }

    @Test
    void testAddTransaction_WhenInsertFails_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);
        TransactionBean transaction = new TransactionBean("user1", 100.0);

        // Act
        boolean result = orderService.addTransaction(transaction);

        // Assert
        assertFalse(result);
    }

    @Test
    void testAddTransaction_WhenSqlExceptionOccurs_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());
        TransactionBean transaction = new TransactionBean("user1", 100.0);

        // Act
        boolean result = orderService.addTransaction(transaction);

        // Assert
        assertFalse(result);
    }

    // =============== Tests for countSoldItem ===============

    @Test
    void testCountSoldItem_WhenItemFound_ShouldReturnCount() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(42);

        // Act
        int count = orderService.countSoldItem("prod1");

        // Assert
        assertEquals(42, count);
    }

    @Test
    void testCountSoldItem_WhenItemNotFound_ShouldReturnZero() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        int count = orderService.countSoldItem("prod1");

        // Assert
        assertEquals(0, count);
    }

    // =============== Tests for getAllOrders ===============

    @Test
    void testGetAllOrders_WhenOrdersExist_ShouldReturnOrderList() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true, true, false);

        // Act
        List<OrderBean> orders = orderService.getAllOrders();

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    // =============== Tests for shipNow ===============

    @Test
    void testShipNow_WhenSuccessful_ShouldReturnSuccessMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        String status = orderService.shipNow("order1", "prod1");

        // Assert
        assertEquals("Order Has been shipped successfully!!", status);
    }

    @Test
    void testShipNow_WhenUpdateFails_ShouldReturnFailureMessage() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        String status = orderService.shipNow("order1", "prod1");

        // Assert
        assertEquals("FAILURE", status);
    }
}

package com.shashi;

import com.shashi.beans.OrderBean;
import com.shashi.beans.TransactionBean;
import com.shashi.service.impl.OrderServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private Connection connection;
    private PreparedStatement preparedStatement;

    @BeforeEach
    void setUp() throws SQLException {
        orderService = new OrderServiceImpl();

        dbUtilMockedStatic = mockStatic(DBUtil.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);

        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
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
}

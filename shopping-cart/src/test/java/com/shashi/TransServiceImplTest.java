package com.shashi;

import com.shashi.service.impl.TransServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TransServiceImplTest {

    private TransServiceImpl transService;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        transService = new TransServiceImpl();

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

    // =============== Tests for getUserId ===============

    @Test
    void testGetUserId_WhenTransIdFound_ShouldReturnUserId() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("user123");

        // Act
        String userId = transService.getUserId("trans123");

        // Assert
        assertEquals("user123", userId);
    }

    @Test
    void testGetUserId_WhenTransIdNotFound_ShouldReturnEmptyString() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        String userId = transService.getUserId("trans123");

        // Assert
        assertEquals("", userId);
    }

    @Test
    void testGetUserId_WhenSqlExceptionOccurs_ShouldReturnEmptyString() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenThrow(new SQLException());

        // Act
        String userId = transService.getUserId("trans123");

        // Assert
        assertEquals("", userId);
    }
}

package com.shashi;

import com.shashi.beans.DemandBean;
import com.shashi.service.impl.DemandServiceImpl;
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

class DemandServiceImplTest {

    private DemandServiceImpl demandService;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        demandService = new DemandServiceImpl();

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

    // =============== Tests for addProduct (String version) ===============

    @Test
    void testAddProduct_WhenDemandAlreadyExists_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);

        // Act
        boolean result = demandService.addProduct("user1", "prod1", 5);

        // Assert
        assertTrue(result);
        verify(preparedStatement, never()).executeUpdate(); // Ensure insert is not called
    }

    @Test
    void testAddProduct_WhenDemandIsNewAndInsertSucceeds_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = demandService.addProduct("user1", "prod1", 5);

        // Assert
        assertTrue(result);
    }

    @Test
    void testAddProduct_WhenDemandIsNewAndInsertFails_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        boolean result = demandService.addProduct("user1", "prod1", 5);

        // Assert
        assertFalse(result);
    }

    // =============== Tests for removeProduct ===============

    @Test
    void testRemoveProduct_WhenProductExists_ShouldDeleteAndReturnTrue() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = demandService.removeProduct("user1", "prod1");

        // Assert
        assertTrue(result);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testRemoveProduct_WhenProductDoesNotExist_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        boolean result = demandService.removeProduct("user1", "prod1");

        // Assert
        assertTrue(result);
        verify(preparedStatement, never()).executeUpdate(); // Ensure delete is not called
    }

    @Test
    void testRemoveProduct_WhenSqlExceptionOccurs_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(resultSet.next()).thenThrow(new SQLException());

        // Act
        boolean result = demandService.removeProduct("user1", "prod1");

        // Assert
        assertFalse(result);
    }

    // =============== Tests for haveDemanded ===============

    @Test
    void testHaveDemanded_WhenDemandsExist_ShouldReturnDemandList() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("username")).thenReturn("user1", "user2");
        when(resultSet.getString("prodid")).thenReturn("prod1");
        when(resultSet.getInt("quantity")).thenReturn(2, 3);

        // Act
        List<DemandBean> demandList = demandService.haveDemanded("prod1");

        // Assert
        assertNotNull(demandList);
        assertEquals(2, demandList.size());
        assertEquals("user1", demandList.get(0).getUserName());
        assertEquals(3, demandList.get(1).getDemandQty());
    }

    @Test
    void testHaveDemanded_WhenNoDemandsExist_ShouldReturnEmptyList() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        List<DemandBean> demandList = demandService.haveDemanded("prod1");

        // Assert
        assertNotNull(demandList);
        assertTrue(demandList.isEmpty());
    }
}

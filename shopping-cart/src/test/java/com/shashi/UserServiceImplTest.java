
package com.shashi;

import com.shashi.beans.UserBean;
import com.shashi.service.impl.UserServiceImpl;
import com.shashi.utility.DBUtil;
import com.shashi.utility.MailMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;

    // Mocks for static utilities
    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private MockedStatic<MailMessage> mailMessageMockedStatic;

    // Mocks for JDBC objects
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        userService = new UserServiceImpl();

        // Mock the static utility classes
        dbUtilMockedStatic = mockStatic(DBUtil.class);
        mailMessageMockedStatic = mockStatic(MailMessage.class);

        // Mock the JDBC objects
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        // Define the behavior of the mocked static methods
        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        mailMessageMockedStatic.when(() -> MailMessage.registrationSuccess(anyString(), anyString())).then(invocation -> null); // Do nothing when called

        // Define the behavior of the mocked connection
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Define the behavior of the mocked prepared statement for query
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @AfterEach
    void tearDown() {
        // Close the static mocks
        dbUtilMockedStatic.close();
        mailMessageMockedStatic.close();
    }

    // =============== Tests for isValidCredential ===============

    @Test
    void testIsValidCredential_WhenCredentialsAreValid_ShouldReturnValid() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);

        // Act
        String status = userService.isValidCredential("test@example.com", "password123");

        // Assert
        assertEquals("valid", status);
    }

    @Test
    void testIsValidCredential_WhenCredentialsAreInvalid_ShouldReturnDeniedMessage() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        String status = userService.isValidCredential("wrong@example.com", "wrongpassword");

        // Assert
        assertEquals("Login Denied! Incorrect Username or Password", status);
    }

    @Test
    void testIsValidCredential_WhenSqlExceptionOccurs_ShouldReturnErrorMessage() throws SQLException {
        // Arrange
        String exceptionMessage = "Database connection failed";
        when(preparedStatement.executeQuery()).thenThrow(new SQLException(exceptionMessage));

        // Act
        String status = userService.isValidCredential("test@example.com", "password");

        // Assert
        assertEquals("Error: " + exceptionMessage, status);
    }

    // =============== Tests for registerUser ===============

    @Test
    void testRegisterUser_WhenEmailAlreadyRegistered_ShouldReturnAlreadyRegisteredMessage() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        UserBean existingUser = new UserBean("Test User", 1234567890L, "test@example.com", "123 Test St", 12345, "password");

        // Act
        String status = userService.registerUser(existingUser);

        // Assert
        assertEquals("Email Id Already Registered!", status);
    }

    @Test
    void testRegisterUser_WhenEmailNotRegistered_ShouldReturnSuccess() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        UserBean newUser = new UserBean("New User", 1234567890L, "new@example.com", "456 New Ave", 54321, "newpassword");

        // Act
        String status = userService.registerUser(newUser);

        // Assert
        assertEquals("User Registered Successfully!", status);
        mailMessageMockedStatic.verify(() -> MailMessage.registrationSuccess(eq("new@example.com"), eq("New")));
    }

    @Test
    void testRegisterUser_WhenDatabaseInsertFails_ShouldReturnFailedMessage() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        UserBean newUser = new UserBean("New User", 1234567890L, "new@example.com", "456 New Ave", 54321, "newpassword");

        // Act
        String status = userService.registerUser(newUser);

        // Assert
        assertEquals("User Registration Failed!", status);
    }

    // =============== Tests for isRegistered ===============

    @Test
    void testIsRegistered_WhenUserExists_ShouldReturnTrue() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);

        // Act
        boolean isRegistered = userService.isRegistered("existing@example.com");

        // Assert
        assertTrue(isRegistered);
    }

    @Test
    void testIsRegistered_WhenUserDoesNotExist_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        boolean isRegistered = userService.isRegistered("nonexisting@example.com");

        // Assert
        assertFalse(isRegistered);
    }

    // =============== Tests for getUserDetails ===============

    @Test
    void testGetUserDetails_WhenUserFound_ShouldReturnUserBean() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("Test User");
        when(resultSet.getLong("mobile")).thenReturn(1234567890L);
        when(resultSet.getString("email")).thenReturn("test@example.com");
        when(resultSet.getString("address")).thenReturn("123 Test St");
        when(resultSet.getInt("pincode")).thenReturn(12345);
        when(resultSet.getString("password")).thenReturn("password");

        // Act
        UserBean user = userService.getUserDetails("test@example.com", "password");

        // Assert
        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(1234567890L, user.getMobile());
    }

    @Test
    void testGetUserDetails_WhenUserNotFound_ShouldReturnNull() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        UserBean user = userService.getUserDetails("nonexisting@example.com", "password");

        // Assert
        assertNull(user);
    }

    // =============== Tests for getFName ===============

    @Test
    void testGetFName_WhenUserFound_ShouldReturnFirstName() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("Test User");

        // Act
        String fName = userService.getFName("test@example.com");

        // Assert
        assertEquals("Test", fName);
    }

    @Test
    void testGetFName_WhenUserNotFound_ShouldReturnEmptyString() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        String fName = userService.getFName("nonexisting@example.com");

        // Assert
        assertEquals("", fName);
    }

    // =============== Tests for getUserAddr ===============

    @Test
    void testGetUserAddr_WhenUserFound_ShouldReturnAddress() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("123 Test St");

        // Act
        String addr = userService.getUserAddr("test@example.com");

        // Assert
        assertEquals("123 Test St", addr);
    }

    @Test
    void testGetUserAddr_WhenUserNotFound_ShouldReturnEmptyString() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);

        // Act
        String addr = userService.getUserAddr("nonexisting@example.com");

        // Assert
        assertEquals("", addr);
    }
}

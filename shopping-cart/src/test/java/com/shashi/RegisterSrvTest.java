package com.shashi;

import com.shashi.srv.RegisterSrv;
import com.shashi.utility.DBUtil;
import com.shashi.utility.MailMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegisterSrvTest {

    private RegisterSrv registerSrv;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private MockedStatic<MailMessage> mailMessageMockedStatic;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        registerSrv = new RegisterSrv();

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        dbUtilMockedStatic = mockStatic(DBUtil.class);
        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Mock the mail utility
        mailMessageMockedStatic = mockStatic(MailMessage.class);
        mailMessageMockedStatic.when(() -> MailMessage.registrationSuccess(anyString(), anyString())).then(invocation -> null);
    }

    @AfterEach
    void tearDown() {
        dbUtilMockedStatic.close();
        mailMessageMockedStatic.close();
    }

    private void invokeDoGet() throws Exception {
        Method method = RegisterSrv.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(registerSrv, request, response);
    }

    private void mockRequestParameters() {
        when(request.getParameter("username")).thenReturn("Test User");
        when(request.getParameter("mobile")).thenReturn("1234567890");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("address")).thenReturn("123 Test St");
        when(request.getParameter("pincode")).thenReturn("12345");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
    }

    @Test
    void testDoGet_SuccessfulRegistration() throws Exception {
        // Arrange
        mockRequestParameters();
        when(resultSet.next()).thenReturn(false); // isRegistered check
        when(preparedStatement.executeUpdate()).thenReturn(1); // insert check

        // Act
        invokeDoGet();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().contains("User Registered Successfully!"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_PasswordMismatch() throws Exception {
        // Arrange
        mockRequestParameters();
        when(request.getParameter("confirmPassword")).thenReturn("wrongpassword");

        // Act
        invokeDoGet();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().contains("Password not matching!"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_EmailAlreadyExists() throws Exception {
        // Arrange
        mockRequestParameters();
        when(resultSet.next()).thenReturn(true); // isRegistered check

        // Act
        invokeDoGet();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().contains("Email Id Already Registered!"));
        verify(requestDispatcher).forward(request, response);
    }
}

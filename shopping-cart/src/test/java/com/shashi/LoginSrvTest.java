package com.shashi;

import com.shashi.beans.UserBean;
import com.shashi.srv.LoginSrv;
import com.shashi.utility.DBUtil;
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
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginSrvTest {

    private LoginSrv loginSrv;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        loginSrv = new LoginSrv();

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        dbUtilMockedStatic = mockStatic(DBUtil.class);
        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @AfterEach
    void tearDown() {
        dbUtilMockedStatic.close();
    }

    private void invokeDoGet() throws Exception {
        Method method = LoginSrv.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(loginSrv, request, response);
    }

    @Test
    void testDoGet_AdminLoginSuccess() throws Exception {
        // Arrange
        when(request.getParameter("username")).thenReturn("admin@gmail.com");
        when(request.getParameter("password")).thenReturn("admin");
        when(request.getParameter("usertype")).thenReturn("admin");

        // Act
        invokeDoGet();

        // Assert
        verify(session).setAttribute("username", "admin@gmail.com");
        verify(session).setAttribute("usertype", "admin");
        verify(request).getRequestDispatcher("adminViewProduct.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_AdminLoginFailure() throws Exception {
        // Arrange
        when(request.getParameter("username")).thenReturn("admin@gmail.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(request.getParameter("usertype")).thenReturn("admin");

        // Act
        invokeDoGet();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertEquals("login.jsp?message=Login Denied! Invalid Username or password.", dispatcherArgument.getValue());
        verify(requestDispatcher).include(request, response);
    }

    @Test
    void testDoGet_CustomerLoginSuccess() throws Exception {
        // Arrange
        when(request.getParameter("username")).thenReturn("customer@example.com");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("usertype")).thenReturn("customer");

        when(resultSet.next()).thenReturn(true).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("Test Customer");

        // Act
        invokeDoGet();

        // Assert
        verify(session).setAttribute(eq("userdata"), any(UserBean.class));
        verify(session).setAttribute("username", "customer@example.com");
        verify(request).getRequestDispatcher("userHome.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_CustomerLoginFailure() throws Exception {
        // Arrange
        when(request.getParameter("username")).thenReturn("customer@example.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(request.getParameter("usertype")).thenReturn("customer");

        when(resultSet.next()).thenReturn(false);

        // Act
        invokeDoGet();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().startsWith("login.jsp?message="));
        verify(requestDispatcher).forward(request, response);
    }
}

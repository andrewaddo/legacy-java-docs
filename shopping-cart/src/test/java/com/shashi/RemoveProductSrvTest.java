package com.shashi;

import com.shashi.srv.RemoveProductSrv;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RemoveProductSrvTest {

    private RemoveProductSrv removeProductSrv;

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

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        removeProductSrv = new RemoveProductSrv();

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        dbUtilMockedStatic = mockStatic(DBUtil.class);
        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        dbUtilMockedStatic.close();
    }

    private void invokeDoGet() throws Exception {
        Method method = RemoveProductSrv.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(removeProductSrv, request, response);
    }

    @Test
    void testDoGet_WhenNotAdmin_ShouldRedirectToLogin() throws Exception {
        // Arrange
        when(session.getAttribute("usertype")).thenReturn("customer");

        // Act
        invokeDoGet();

        // Assert
        verify(response).sendRedirect("login.jsp?message=Access Denied, Login As Admin!!");
    }

    @Test
    void testDoGet_WhenSessionExpired_ShouldRedirectToLogin() throws Exception {
        // Arrange
        when(session.getAttribute("usertype")).thenReturn("admin");
        when(session.getAttribute("username")).thenReturn(null);

        // Act
        invokeDoGet();

        // Assert
        verify(response).sendRedirect("login.jsp?message=Session Expired, Login Again!!");
    }

    @Test
    void testDoGet_WhenRemovalIsSuccessful_ShouldForwardWithSuccessMessage() throws Exception {
        // Arrange
        when(session.getAttribute("usertype")).thenReturn("admin");
        when(session.getAttribute("username")).thenReturn("admin");
        when(session.getAttribute("password")).thenReturn("admin");
        when(request.getParameter("prodid")).thenReturn("p101");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        invokeDoGet();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().contains("Product Removed Successfully!"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_WhenRemovalFails_ShouldForwardWithFailedMessage() throws Exception {
        // Arrange
        when(session.getAttribute("usertype")).thenReturn("admin");
        when(session.getAttribute("username")).thenReturn("admin");
        when(session.getAttribute("password")).thenReturn("admin");
        when(request.getParameter("prodid")).thenReturn("p101");

        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        invokeDoGet();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().contains("Product Removal Failed!"));
        verify(requestDispatcher).forward(request, response);
    }
}

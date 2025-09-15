package com.shashi;

import com.shashi.srv.AddProductSrv;
import com.shashi.utility.DBUtil;
import com.shashi.utility.IDUtil;
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
import javax.servlet.http.Part;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AddProductSrvTest {

    private AddProductSrv addProductSrv;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private Part part;
    @Mock
    private InputStream inputStream;

    private MockedStatic<DBUtil> dbUtilMockedStatic;
    private MockedStatic<IDUtil> idUtilMockedStatic;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        addProductSrv = new AddProductSrv();

        // Mock Servlet API
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getPart("image")).thenReturn(part);
        when(part.getInputStream()).thenReturn(inputStream);

        // Mock Backend
        dbUtilMockedStatic = mockStatic(DBUtil.class);
        idUtilMockedStatic = mockStatic(IDUtil.class);
        dbUtilMockedStatic.when(DBUtil::provideConnection).thenReturn(connection);
        idUtilMockedStatic.when(IDUtil::generateId).thenReturn("test-id");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        dbUtilMockedStatic.close();
        idUtilMockedStatic.close();
    }

    private void invokeDoPost() throws Exception {
        Method method = AddProductSrv.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(addProductSrv, request, response);
    }

    private void mockAdminSession() {
        when(session.getAttribute("usertype")).thenReturn("admin");
        when(session.getAttribute("username")).thenReturn("admin");
        when(session.getAttribute("password")).thenReturn("admin");
    }

    private void mockProductParameters() {
        when(request.getParameter("name")).thenReturn("Test Product");
        when(request.getParameter("type")).thenReturn("Test Type");
        when(request.getParameter("info")).thenReturn("Test Info");
        when(request.getParameter("price")).thenReturn("123.45");
        when(request.getParameter("quantity")).thenReturn("10");
    }

    @Test
    void testDoPost_NotAdmin_ShouldRedirect() throws Exception {
        // Arrange
        when(session.getAttribute("usertype")).thenReturn("customer");

        // Act
        invokeDoPost();

        // Assert
        verify(response).sendRedirect("login.jsp?message=Access Denied!");
    }

    @Test
    void testDoPost_AdminUser_SuccessfulAdd() throws Exception {
        // Arrange
        mockAdminSession();
        mockProductParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        invokeDoPost();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().contains("Product Added Successfully"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPost_AdminUser_FailedAdd() throws Exception {
        // Arrange
        mockAdminSession();
        mockProductParameters();
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        invokeDoPost();

        // Assert
        ArgumentCaptor<String> dispatcherArgument = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(dispatcherArgument.capture());
        assertTrue(dispatcherArgument.getValue().contains("Product Updation Failed!"));
        verify(requestDispatcher).forward(request, response);
    }
}

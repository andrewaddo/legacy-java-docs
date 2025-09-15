package com.shashi;

import com.shashi.srv.LogoutSrv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutSrvTest {

    private LogoutSrv logoutSrv;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        logoutSrv = new LogoutSrv();

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    private void invokeDoGet() throws Exception {
        Method method = LogoutSrv.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(logoutSrv, request, response);
    }

    @Test
    void testDoGet_ShouldInvalidateSessionAndForward() throws Exception {
        // Act
        invokeDoGet();

        // Assert
        verify(session).setAttribute("username", null);
        verify(session).setAttribute("password", null);
        verify(session).setAttribute("usertype", null);
        verify(session).setAttribute("userdata", null);

        verify(request).getRequestDispatcher("login.jsp?message=Successfully Logged Out!");
        verify(requestDispatcher).forward(request, response);
    }
}

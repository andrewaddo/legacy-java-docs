package com.shashi;

import com.shashi.srv.FansMessage;
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
import java.io.PrintWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FansMessageTest {

    private FansMessage fansMessageSrv;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private PrintWriter printWriter;

    private MockedStatic<MailMessage> mailMessageMockedStatic;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        fansMessageSrv = new FansMessage();

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(response.getWriter()).thenReturn(printWriter);

        mailMessageMockedStatic = mockStatic(MailMessage.class);
    }

    @AfterEach
    void tearDown() {
        mailMessageMockedStatic.close();
    }

    private void invokeDoGet() throws Exception {
        Method method = FansMessage.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(fansMessageSrv, request, response);
    }

    private void mockRequestParameters() {
        when(request.getParameter("name")).thenReturn("Test User");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("comments")).thenReturn("Great website!");
    }

    @Test
    void testDoGet_WhenMailSucceeds_ShouldPrintSuccess() throws Exception {
        // Arrange
        mockRequestParameters();
        mailMessageMockedStatic.when(() -> MailMessage.sendMessage(anyString(), anyString(), anyString())).thenReturn("SUCCESS");

        // Act
        invokeDoGet();

        // Assert
        verify(requestDispatcher).include(request, response);
        ArgumentCaptor<String> printWriterArgument = ArgumentCaptor.forClass(String.class);
        verify(printWriter).print(printWriterArgument.capture());
        assertTrue(printWriterArgument.getValue().contains("Comments Sent Successfully"));
    }

    @Test
    void testDoGet_WhenMailFails_ShouldPrintFailure() throws Exception {
        // Arrange
        mockRequestParameters();
        mailMessageMockedStatic.when(() -> MailMessage.sendMessage(anyString(), anyString(), anyString())).thenReturn("FAILURE");

        // Act
        invokeDoGet();

        // Assert
        verify(requestDispatcher).include(request, response);
        ArgumentCaptor<String> printWriterArgument = ArgumentCaptor.forClass(String.class);
        verify(printWriter).print(printWriterArgument.capture());
        assertTrue(printWriterArgument.getValue().contains("Failed: Please Configure mailer.email and password"));
    }
}

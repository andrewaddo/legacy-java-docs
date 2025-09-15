package com.shashi;

import com.shashi.utility.JavaMailUtil;
import com.shashi.utility.MailMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

class MailMessageTest {

    private MockedStatic<JavaMailUtil> javaMailUtilMockedStatic;

    @BeforeEach
    void setUp() {
        javaMailUtilMockedStatic = mockStatic(JavaMailUtil.class);
    }

    @AfterEach
    void tearDown() {
        javaMailUtilMockedStatic.close();
    }

    @Test
    void testRegistrationSuccess() {
        // Arrange
        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        MailMessage.registrationSuccess("test@example.com", "John");

        // Assert
        javaMailUtilMockedStatic.verify(() -> JavaMailUtil.sendMail(recipientCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture()));
        assertEquals("test@example.com", recipientCaptor.getValue());
        assertEquals("Registration Successfull", subjectCaptor.getValue());
        assertTrue(bodyCaptor.getValue().contains("Welcome to Ellison Electronics"));
        assertTrue(bodyCaptor.getValue().contains("Hi John"));
    }

    @Test
    void testTransactionSuccess() {
        // Arrange
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        MailMessage.transactionSuccess("test@example.com", "Jane", "trans123", 250.75);

        // Assert
        javaMailUtilMockedStatic.verify(() -> JavaMailUtil.sendMail(anyString(), subjectCaptor.capture(), bodyCaptor.capture()));
        assertEquals("Order Placed at Ellison Electronics", subjectCaptor.getValue());
        assertTrue(bodyCaptor.getValue().contains("Hey Jane"));
        assertTrue(bodyCaptor.getValue().contains("trans123"));
        assertTrue(bodyCaptor.getValue().contains("250.75"));
    }

    @Test
    void testOrderShipped() {
        // Arrange
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        MailMessage.orderShipped("test@example.com", "Doe", "order456", 199.99);

        // Assert
        javaMailUtilMockedStatic.verify(() -> JavaMailUtil.sendMail(anyString(), subjectCaptor.capture(), bodyCaptor.capture()));
        assertTrue(subjectCaptor.getValue().contains("Your Order has been Shipped"));
        assertTrue(bodyCaptor.getValue().contains("Hey Doe"));
        assertTrue(bodyCaptor.getValue().contains("order456"));
    }

    @Test
    void testProductAvailableNow() {
        // Arrange
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        MailMessage.productAvailableNow("test@example.com", "Sam", "Laptop X", "prod789");

        // Assert
        javaMailUtilMockedStatic.verify(() -> JavaMailUtil.sendMail(anyString(), subjectCaptor.capture(), bodyCaptor.capture()));
        assertTrue(subjectCaptor.getValue().contains("Product Laptop X is Now Available"));
        assertTrue(bodyCaptor.getValue().contains("Hey Sam"));
        assertTrue(bodyCaptor.getValue().contains("prod789"));
    }
}

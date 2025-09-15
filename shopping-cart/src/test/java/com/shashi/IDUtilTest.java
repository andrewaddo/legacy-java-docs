package com.shashi;

import com.shashi.utility.IDUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IDUtilTest {

    @Test
    void testGenerateId_ShouldReturnIdWithP_Prefix() {
        // Act
        String productId = IDUtil.generateId();

        // Assert
        assertNotNull(productId);
        assertTrue(productId.startsWith("P"));
    }

    @Test
    void testGenerateTransId_ShouldReturnIdWithT_Prefix() {
        // Act
        String transId = IDUtil.generateTransId();

        // Assert
        assertNotNull(transId);
        assertTrue(transId.startsWith("T"));
    }
}

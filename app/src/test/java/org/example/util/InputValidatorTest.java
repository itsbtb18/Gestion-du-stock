package org.example.util;

import org.example.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputValidator
 */
class InputValidatorTest {
    
    @Test
    @DisplayName("Should validate non-empty strings correctly")
    void testValidateNotEmpty() {
        // Valid cases
        assertDoesNotThrow(() -> InputValidator.validateNotEmpty("test", "field"));
        assertDoesNotThrow(() -> InputValidator.validateNotEmpty("  test  ", "field"));
        
        // Invalid cases
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateNotEmpty("", "field"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateNotEmpty("   ", "field"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateNotEmpty(null, "field"));
    }
    
    @Test
    @DisplayName("Should validate email format correctly")
    void testValidateEmail() {
        // Valid emails
        assertDoesNotThrow(() -> InputValidator.validateEmail("test@example.com", "email"));
        assertDoesNotThrow(() -> InputValidator.validateEmail("user.name+tag@example.co.uk", "email"));
        
        // Invalid emails
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateEmail("invalid", "email"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateEmail("@example.com", "email"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateEmail("user@", "email"));
    }
    
    @Test
    @DisplayName("Should validate phone numbers correctly")
    void testValidatePhone() {
        // Valid phones
        assertDoesNotThrow(() -> InputValidator.validatePhone("0612345678", "phone"));
        assertDoesNotThrow(() -> InputValidator.validatePhone("+33612345678", "phone"));
        assertDoesNotThrow(() -> InputValidator.validatePhone("06 12 34 56 78", "phone"));
        
        // Invalid phones
        assertThrows(ValidationException.class, 
            () -> InputValidator.validatePhone("123", "phone"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validatePhone("abcdefghij", "phone"));
    }
    
    @Test
    @DisplayName("Should validate positive numbers correctly")
    void testValidatePositive() {
        // Valid
        assertDoesNotThrow(() -> InputValidator.validatePositive(1.0, "price"));
        assertDoesNotThrow(() -> InputValidator.validatePositive(100, "quantity"));
        
        // Invalid
        assertThrows(ValidationException.class, 
            () -> InputValidator.validatePositive(0.0, "price"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validatePositive(-1.0, "price"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validatePositive(0, "quantity"));
    }
    
    @Test
    @DisplayName("Should validate range correctly")
    void testValidateRange() {
        // Valid
        assertDoesNotThrow(() -> InputValidator.validateRange(50.0, "discount", 0.0, 100.0));
        assertDoesNotThrow(() -> InputValidator.validateRange(0.0, "discount", 0.0, 100.0));
        assertDoesNotThrow(() -> InputValidator.validateRange(100.0, "discount", 0.0, 100.0));
        
        // Invalid
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateRange(-1.0, "discount", 0.0, 100.0));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateRange(101.0, "discount", 0.0, 100.0));
    }
    
    @Test
    @DisplayName("Should validate percentage correctly")
    void testValidatePercentage() {
        // Valid
        assertDoesNotThrow(() -> InputValidator.validatePercentage(20.0, "tva"));
        assertDoesNotThrow(() -> InputValidator.validatePercentage(0.0, "tva"));
        assertDoesNotThrow(() -> InputValidator.validatePercentage(100.0, "tva"));
        
        // Invalid
        assertThrows(ValidationException.class, 
            () -> InputValidator.validatePercentage(-1.0, "tva"));
        assertThrows(ValidationException.class, 
            () -> InputValidator.validatePercentage(101.0, "tva"));
    }
    
    @Test
    @DisplayName("Should normalize phone numbers correctly")
    void testNormalizePhone() {
        assertThat(InputValidator.normalizePhone("06 12 34 56 78"))
            .isEqualTo("0612345678");
        assertThat(InputValidator.normalizePhone("(06) 12-34-56-78"))
            .isEqualTo("0612345678");
        assertThat(InputValidator.normalizePhone(null)).isNull();
    }
    
    @Test
    @DisplayName("Should normalize codes correctly")
    void testNormalizeCode() {
        assertThat(InputValidator.normalizeCode("  prod123  "))
            .isEqualTo("PROD123");
        assertThat(InputValidator.normalizeCode("abc-def"))
            .isEqualTo("ABC-DEF");
        assertThat(InputValidator.normalizeCode(null)).isNull();
    }
    
    @Test
    @DisplayName("Should normalize emails correctly")
    void testNormalizeEmail() {
        assertThat(InputValidator.normalizeEmail("  TEST@Example.COM  "))
            .isEqualTo("test@example.com");
        assertThat(InputValidator.normalizeEmail(null)).isNull();
    }
    
    @Test
    @DisplayName("Should validate minimum length")
    void testValidateMinLength() {
        assertDoesNotThrow(() -> InputValidator.validateMinLength("12345", "code", 5));
        assertDoesNotThrow(() -> InputValidator.validateMinLength("123456", "code", 5));
        
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateMinLength("1234", "code", 5));
    }
    
    @Test
    @DisplayName("Should validate maximum length")
    void testValidateMaxLength() {
        assertDoesNotThrow(() -> InputValidator.validateMaxLength("12345", "code", 5));
        assertDoesNotThrow(() -> InputValidator.validateMaxLength("1234", "code", 5));
        assertDoesNotThrow(() -> InputValidator.validateMaxLength(null, "code", 5));
        
        assertThrows(ValidationException.class, 
            () -> InputValidator.validateMaxLength("123456", "code", 5));
    }
}

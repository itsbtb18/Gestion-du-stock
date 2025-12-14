package org.example.util;

import org.example.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * InputValidator - Utility class for input validation
 */
public class InputValidator {
    
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+?[0-9]{1,4})?[0-9]{9,15}$"
    );
    
    private static final Pattern CODE_PATTERN = Pattern.compile(
        "^[A-Z0-9-_]{3,20}$"
    );
    
    /**
     * Validate that a string is not null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, value, "Field cannot be empty");
        }
    }
    
    /**
     * Validate that a string meets minimum length
     */
    public static void validateMinLength(String value, String fieldName, int minLength) {
        validateNotEmpty(value, fieldName);
        if (value.trim().length() < minLength) {
            throw new ValidationException(fieldName, value, 
                String.format("Field must be at least %d characters", minLength));
        }
    }
    
    /**
     * Validate that a string meets maximum length
     */
    public static void validateMaxLength(String value, String fieldName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(fieldName, value,
                String.format("Field must not exceed %d characters", maxLength));
        }
    }
    
    /**
     * Validate email format
     */
    public static void validateEmail(String email, String fieldName) {
        validateNotEmpty(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException(fieldName, email, "Invalid email format");
        }
    }
    
    /**
     * Validate phone number format
     */
    public static void validatePhone(String phone, String fieldName) {
        validateNotEmpty(phone, fieldName);
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new ValidationException(fieldName, phone, "Invalid phone number format");
        }
    }
    
    /**
     * Validate product/client code format
     */
    public static void validateCode(String code, String fieldName) {
        validateNotEmpty(code, fieldName);
        if (!CODE_PATTERN.matcher(code.toUpperCase()).matches()) {
            throw new ValidationException(fieldName, code, 
                "Code must be 3-20 characters (alphanumeric, dash, underscore)");
        }
    }
    
    /**
     * Validate numeric value is positive
     */
    public static void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName, value, "Value must be positive");
        }
    }
    
    /**
     * Validate numeric value is non-negative
     */
    public static void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName, value, "Value cannot be negative");
        }
    }
    
    /**
     * Validate integer value is positive
     */
    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName, value, "Value must be positive");
        }
    }
    
    /**
     * Validate integer value is non-negative
     */
    public static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName, value, "Value cannot be negative");
        }
    }
    
    /**
     * Validate value is within range
     */
    public static void validateRange(double value, String fieldName, double min, double max) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName, value,
                String.format("Value must be between %.2f and %.2f", min, max));
        }
    }
    
    /**
     * Validate percentage (0-100)
     */
    public static void validatePercentage(double value, String fieldName) {
        validateRange(value, fieldName, 0.0, 100.0);
    }
    
    /**
     * Validate discount percentage (with configurable max)
     */
    public static void validateDiscount(double value, String fieldName, double maxDiscount) {
        validateRange(value, fieldName, 0.0, maxDiscount);
    }
    
    /**
     * Validate object is not null
     */
    public static <T> void validateNotNull(T value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, null, "Field cannot be null");
        }
    }
    
    /**
     * Clean and normalize phone number
     */
    public static String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[\\s()-]", "");
    }
    
    /**
     * Clean and normalize code (uppercase, trim)
     */
    public static String normalizeCode(String code) {
        if (code == null) return null;
        return code.trim().toUpperCase();
    }
    
    /**
     * Clean and normalize email (lowercase, trim)
     */
    public static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }
    
    // Private constructor to prevent instantiation
    private InputValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}

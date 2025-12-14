package org.example.exception;

/**
 * ValidationException - Exception thrown when input validation fails
 */
public class ValidationException extends RuntimeException {
    
    private final String fieldName;
    private final Object invalidValue;
    
    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
        this.invalidValue = null;
    }
    
    public ValidationException(String fieldName, Object invalidValue, String message) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
        this.invalidValue = null;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getInvalidValue() {
        return invalidValue;
    }
    
    @Override
    public String getMessage() {
        if (fieldName != null) {
            return String.format("Validation failed for '%s' with value '%s': %s", 
                fieldName, invalidValue, super.getMessage());
        }
        return super.getMessage();
    }
}

package org.example.exception;

/**
 * BusinessException - Exception thrown when business rules are violated
 */
public class BusinessException extends RuntimeException {
    
    private final String businessRule;
    private final Object context;
    
    public BusinessException(String message) {
        super(message);
        this.businessRule = null;
        this.context = null;
    }
    
    public BusinessException(String businessRule, String message) {
        super(message);
        this.businessRule = businessRule;
        this.context = null;
    }
    
    public BusinessException(String businessRule, Object context, String message) {
        super(message);
        this.businessRule = businessRule;
        this.context = context;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.businessRule = null;
        this.context = null;
    }
    
    public String getBusinessRule() {
        return businessRule;
    }
    
    public Object getContext() {
        return context;
    }
    
    @Override
    public String getMessage() {
        if (businessRule != null) {
            return String.format("Business rule violation '%s': %s", businessRule, super.getMessage());
        }
        return super.getMessage();
    }
}

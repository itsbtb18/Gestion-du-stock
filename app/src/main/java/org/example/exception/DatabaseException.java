package org.example.exception;

import java.sql.SQLException;

/**
 * DatabaseException - Exception thrown when database operations fail
 */
public class DatabaseException extends RuntimeException {
    
    private final String operation;
    private final String sqlState;
    private final int errorCode;
    
    public DatabaseException(String message) {
        super(message);
        this.operation = null;
        this.sqlState = null;
        this.errorCode = 0;
    }
    
    public DatabaseException(String operation, String message) {
        super(message);
        this.operation = operation;
        this.sqlState = null;
        this.errorCode = 0;
    }
    
    public DatabaseException(String operation, SQLException cause) {
        super("Database operation failed: " + operation, cause);
        this.operation = operation;
        this.sqlState = cause.getSQLState();
        this.errorCode = cause.getErrorCode();
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.sqlState = null;
        this.errorCode = 0;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getSqlState() {
        return sqlState;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String getMessage() {
        if (operation != null && sqlState != null) {
            return String.format("Database error during '%s' (SQL State: %s, Error Code: %d): %s",
                operation, sqlState, errorCode, super.getMessage());
        }
        return super.getMessage();
    }
}

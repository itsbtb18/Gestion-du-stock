package org.example.util;

import java.util.logging.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized logging utility for the application
 * Provides consistent logging across all components
 */
public class LoggerUtil {
    
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE_PREFIX = "reb7a-pos";
    private static boolean initialized = false;
    
    static {
        try {
            initializeLogging();
        } catch (IOException e) {
            System.err.println("Failed to initialize logging: " + e.getMessage());
        }
    }
    
    /**
     * Get logger for a specific class
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
    
    /**
     * Get logger with specific name
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
    
    /**
     * Initialize the logging system
     */
    private static void initializeLogging() throws IOException {
        if (initialized) {
            return;
        }
        
        // Create logs directory
        Path logDir = Paths.get(LOG_DIR);
        if (!Files.exists(logDir)) {
            Files.createDirectories(logDir);
        }
        
        // Get root logger
        Logger rootLogger = Logger.getLogger("");
        
        // Remove default console handler to avoid duplicate messages
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        
        // Create console handler with custom formatter
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleLogFormatter());
        rootLogger.addHandler(consoleHandler);
        
        // Create file handler with rotation
        String logFileName = String.format("%s/%s-%s.log", 
            LOG_DIR, 
            LOG_FILE_PREFIX,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        FileHandler fileHandler = new FileHandler(logFileName, true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new SimpleLogFormatter());
        rootLogger.addHandler(fileHandler);
        
        // Set root logger level
        rootLogger.setLevel(Level.ALL);
        
        initialized = true;
        Logger.getLogger(LoggerUtil.class.getName()).info("Logging system initialized");
    }
    
    /**
     * Simple log formatter
     */
    private static class SimpleLogFormatter extends Formatter {
        
        private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            
            // Timestamp
            sb.append(LocalDateTime.now().format(DATE_FORMATTER));
            sb.append(" ");
            
            // Level
            sb.append(String.format("%-7s", record.getLevel().getName()));
            sb.append(" ");
            
            // Logger name (shortened)
            String loggerName = record.getLoggerName();
            if (loggerName != null && loggerName.contains(".")) {
                String[] parts = loggerName.split("\\.");
                loggerName = parts[parts.length - 1];
            }
            sb.append(String.format("%-30s", loggerName));
            sb.append(" - ");
            
            // Message
            sb.append(formatMessage(record));
            sb.append("\n");
            
            // Exception
            if (record.getThrown() != null) {
                Throwable thrown = record.getThrown();
                sb.append("    Exception: ").append(thrown.getClass().getName());
                sb.append(": ").append(thrown.getMessage()).append("\n");
                
                // Stack trace (limited to first 10 lines)
                StackTraceElement[] stackTrace = thrown.getStackTrace();
                int maxLines = Math.min(10, stackTrace.length);
                for (int i = 0; i < maxLines; i++) {
                    sb.append("        at ").append(stackTrace[i]).append("\n");
                }
                if (stackTrace.length > maxLines) {
                    sb.append("        ... ").append(stackTrace.length - maxLines).append(" more\n");
                }
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Log severe error with exception
     */
    public static void logError(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).log(Level.SEVERE, message, throwable);
    }
    
    /**
     * Log warning
     */
    public static void logWarning(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.WARNING, message);
    }
    
    /**
     * Log info
     */
    public static void logInfo(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.INFO, message);
    }
    
    /**
     * Log debug (fine level)
     */
    public static void logDebug(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.FINE, message);
    }
}

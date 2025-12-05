package org.example.db;

import org.example.app.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * DatabaseConnection - Singleton for managing database connections
 * Thread-safe implementation with H2 embedded database support
 */
public class DatabaseConnection {
    
    // Singleton instance
    private static volatile DatabaseConnection instance;
    
    // Database connection
    private Connection connection;
    
    // Database configuration
    private String url;
    private String username;
    private String password;
    private String driver;
    
    // Schema initialization flag
    private boolean schemaInitialized = false;
    
    /**
     * Private constructor to prevent direct instantiation
     * Loads database configuration and establishes connection
     */
    private DatabaseConnection() {
        loadConfiguration();
        establishConnection();
    }
    
    /**
     * Get the singleton instance of DatabaseConnection
     * Thread-safe using double-checked locking pattern
     * @return the unique instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Load database configuration from properties file or use defaults
     * Uses H2 embedded database from AppConfig
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                this.url = props.getProperty("db.url", AppConfig.DB_URL);
                this.username = props.getProperty("db.username", AppConfig.DB_USER);
                this.password = props.getProperty("db.password", AppConfig.DB_PASSWORD);
                this.driver = props.getProperty("db.driver", AppConfig.DB_DRIVER);
            } else {
                // Use default H2 configuration from AppConfig
                useDefaultConfiguration();
            }
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            useDefaultConfiguration();
        }
    }
    
    /**
     * Use default H2 embedded database configuration
     */
    private void useDefaultConfiguration() {
        this.url = AppConfig.DB_URL;
        this.username = AppConfig.DB_USER;
        this.password = AppConfig.DB_PASSWORD;
        this.driver = AppConfig.DB_DRIVER;
    }
    
    /**
     * Establish database connection and initialize schema
     */
    private void establishConnection() {
        try {
            // Load JDBC driver
            Class.forName(driver);
            
            // Establish connection
            this.connection = DriverManager.getConnection(url, username, password);
            
            System.out.println("Database connection established successfully");
            
            // Initialize database schema if not already done
            if (!schemaInitialized) {
                initializeSchema();
                schemaInitialized = true;
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            org.example.util.LoggerUtil.logError(DatabaseConnection.class, "JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            org.example.util.LoggerUtil.logError(DatabaseConnection.class, "Database connection failed", e);
        }
    }
    
    /**
     * Initialize database schema from schema.sql file
     */
    private void initializeSchema() {
        // Execute schema files in order
        executeSchemaFile("schema.sql");
        executeSchemaFile("schema_store_config.sql");
        executeSchemaFile("schema_enhancements.sql");
    }
    
    /**
     * Execute a schema SQL file from resources
     */
    private void executeSchemaFile(String filename) {
        try {
            // Read schema file from resources
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(filename);
            
            if (schemaStream == null) {
                System.err.println("Warning: " + filename + " not found in resources");
                return;
            }
            
            // Read SQL content
            String sqlContent = new String(schemaStream.readAllBytes());
            
            // Remove single-line comments (-- comments)
            sqlContent = sqlContent.replaceAll("--[^\n]*", "");
            
            // Remove multi-line comments (/* ... */)
            sqlContent = sqlContent.replaceAll("/\\*.*?\\*/", "");
            
            // Split by semicolon and execute each statement
            String[] statements = sqlContent.split(";");
            
            try (Statement stmt = connection.createStatement()) {
                int successCount = 0;
                
                for (String sql : statements) {
                    sql = sql.trim();
                    
                    // Skip empty statements
                    if (sql.isEmpty()) {
                        continue;
                    }
                    
                    try {
                        stmt.execute(sql);
                        successCount++;
                    } catch (SQLException e) {
                        // Skip errors for DROP TABLE IF EXISTS and other non-critical errors
                        if (!e.getMessage().contains("already exists") && 
                            !e.getMessage().contains("not found")) {
                            System.err.println("Warning executing SQL in " + filename + ": " + e.getMessage());
                            System.err.println("SQL: " + sql.substring(0, Math.min(100, sql.length())));
                        }
                    }
                }
                
                if (successCount > 0) {
                    System.out.println(filename + " initialized successfully (" + successCount + " statements executed)");
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading " + filename + ": " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error initializing " + filename + ": " + e.getMessage());
        }
    }
    
    /**
     * Get the database connection
     * @return the database connection
     * @throws SQLException if connection is closed or null
     */
    public Connection getConnection() throws SQLException {
        // Check if connection is closed or null, reconnect if needed
        if (connection == null || connection.isClosed()) {
            establishConnection();
        }
        return connection;
    }
    
    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is valid, false otherwise
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Prevent cloning of singleton instance
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton is not allowed");
    }
}

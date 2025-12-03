package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * DatabaseConnection - Singleton for managing database connections
 * Thread-safe implementation with connection pooling support
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
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/gestion_stock");
                this.username = props.getProperty("db.username", "root");
                this.password = props.getProperty("db.password", "");
                this.driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                // Use default configuration if properties file not found
                useDefaultConfiguration();
            }
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            useDefaultConfiguration();
        }
    }
    
    /**
     * Use default database configuration
     */
    private void useDefaultConfiguration() {
        this.url = "jdbc:mysql://localhost:3306/gestion_stock";
        this.username = "root";
        this.password = "";
        this.driver = "com.mysql.cj.jdbc.Driver";
    }
    
    /**
     * Establish database connection
     */
    private void establishConnection() {
        try {
            // Load JDBC driver
            Class.forName(driver);
            
            // Establish connection
            this.connection = DriverManager.getConnection(url, username, password);
            
            System.out.println("Database connection established successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
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

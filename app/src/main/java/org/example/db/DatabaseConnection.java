package org.example.db;

import org.example.app.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    
    private static volatile DatabaseConnection instance;
    
    private HikariDataSource dataSource;
    
    @Deprecated
    private Connection connection;
    
    private String url;
    private String username;
    private String password;
    private String driver;
    
    private boolean schemaInitialized = false;
    
    private DatabaseConnection() {
        loadConfiguration();
        establishConnection();
    }
    
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
                
                useDefaultConfiguration();
            }
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            useDefaultConfiguration();
        }
    }
    
    private void useDefaultConfiguration() {
        this.url = AppConfig.DB_URL;
        this.username = AppConfig.DB_USER;
        this.password = AppConfig.DB_PASSWORD;
        this.driver = AppConfig.DB_DRIVER;
    }
    
    private void establishConnection() {
        try {
            
            Class.forName(driver);
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(driver);
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000); 
            config.setIdleTimeout(600000); 
            config.setMaxLifetime(1800000); 
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("Reb7a-DB-Pool");
            
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            this.dataSource = new HikariDataSource(config);
            
            this.connection = dataSource.getConnection();
            
            System.out.println("Database connection pool established successfully");
            System.out.println("Pool size: " + config.getMaximumPoolSize() + " connections");
            
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
    
    private void initializeSchema() {
        
        executeSchemaFile("schema.sql");
        executeSchemaFile("schema_store_config.sql");
        executeSchemaFile("schema_enhancements.sql");
        executeSchemaFile("schema_algerian_data.sql"); 
    }
    
    private void executeSchemaFile(String filename) {
        try {
            
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(filename);
            
            if (schemaStream == null) {
                System.err.println("Warning: " + filename + " not found in resources");
                return;
            }
            
            String sqlContent = new String(schemaStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            
            sqlContent = sqlContent.replaceAll("--[^\n]*", "");
            
            sqlContent = sqlContent.replaceAll("/\\*.*?\\*/", "");
            
            String[] statements = sqlContent.split(";");
            
            try (Statement stmt = connection.createStatement()) {
                int successCount = 0;
                
                for (String sql : statements) {
                    sql = sql.trim();
                    
                    if (sql.isEmpty()) {
                        continue;
                    }
                    
                    try {
                        stmt.execute(sql);
                        successCount++;
                    } catch (SQLException e) {
                        
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
    
    public Connection getConnection() throws SQLException {
        
        if (dataSource != null && !dataSource.isClosed()) {
            return dataSource.getConnection();
        }
        
        if (connection == null || connection.isClosed()) {
            establishConnection();
        }
        return connection;
    }
    
    public HikariDataSource getDataSource() {
        return dataSource;
    }
    
    public void closeConnection() {
        try {
            
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                System.out.println("Database connection pool closed");
            }
            
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton is not allowed");
    }
}

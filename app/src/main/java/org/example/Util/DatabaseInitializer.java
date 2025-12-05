package org.example.util;

import org.example.db.DatabaseConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * DatabaseInitializer - Utility to initialize database with schema enhancements
 */
public class DatabaseInitializer {
    
    public static void main(String[] args) {
        try {
            System.out.println("Initializing database schema...");
            
            // First run base schema
            System.out.println("\n=== Step 1: Running base schema ===");
            runSqlFile("src/main/resources/schema.sql");
            
            // Then run enhancements
            System.out.println("\n=== Step 2: Running schema enhancements ===");
            runSqlFile("src/main/resources/schema_enhancements.sql");
            
            System.out.println("\n✓ Database initialization complete!");
            
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            org.example.util.LoggerUtil.logError(DatabaseInitializer.class, "Database initialization error", e);
        }
    }
    
    private static void runSqlFile(String filePath) throws Exception {
            // Read SQL file
            StringBuilder sql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sql.append(line).append("\n");
                }
            }
            
            // Execute SQL statements
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String[] statements = sql.toString().split(";");
            
            int count = 0;
            try (Statement stmt = conn.createStatement()) {
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        try {
                            stmt.execute(trimmed);
                            count++;
                            System.out.println("Executed statement " + count);
                        } catch (Exception e) {
                            System.err.println("Error executing: " + trimmed.substring(0, Math.min(50, trimmed.length())));
                            System.err.println("Error: " + e.getMessage());
                        }
                    }
                }
            }
            
            System.out.println("\nExecuted " + count + " statements from " + filePath);
        }
    }

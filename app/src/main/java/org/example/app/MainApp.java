package org.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.db.DatabaseConnection;
import org.example.model.service.MagasinService;
import org.example.model.service.StoreService;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * MainApp - JavaFX Application entry point
 * Initializes database, loads login screen, and manages application lifecycle
 */
public class MainApp extends Application {
    
    private static final String APP_TITLE = "Reb7a - Point de Vente";
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize database and verify connection
        initializeDatabase();
        
        // Load login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
        Parent root = loader.load();
        
        // Configure primary stage
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        primaryStage.setTitle(APP_TITLE + " - Connexion");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        // Cleanup: Close database connections
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
        super.stop();
    }
    
    /**
     * Initialize database connection and create tables if needed
     */
    private void initializeDatabase() {
        try {
            // Get database connection (this will initialize tables via DatabaseConnection)
            Connection conn = DatabaseConnection.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database initialized successfully");
                
                // Initialize MagasinService to ensure all DAOs are ready
                MagasinService.getInstance();
                
                // Initialize default store and load store context
                StoreService.getInstance().initializeDefaultStore();
                
                System.out.println("Services initialized successfully");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            org.example.util.LoggerUtil.logError(MainApp.class, "Database initialization failed", e);
            System.exit(1);
        }
    }
    
    /**
     * Main entry point for the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

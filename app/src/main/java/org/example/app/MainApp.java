package org.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.db.DatabaseConnection;
import org.example.model.service.MagasinService;
import org.example.model.service.StoreService;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class MainApp extends Application {
    
    private static final String APP_TITLE = "Reb7a - Point de Vente";
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    
    static {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        initializeDatabase();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
        Parent root = loader.load();
        
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
    
    private void initializeDatabase() {
        try {
            
            Connection conn = DatabaseConnection.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database initialized successfully");
                
                MagasinService.getInstance();
                
                StoreService.getInstance().initializeDefaultStore();
                
                System.out.println("Services initialized successfully");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            org.example.util.LoggerUtil.logError(MainApp.class, "Database initialization failed", e);
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

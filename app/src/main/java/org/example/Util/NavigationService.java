package org.example.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.app.AppConfig;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class NavigationService {
    
    private static NavigationService instance;
    
    private BorderPane mainContainer;
    private StackPane contentArea;
    private Stage primaryStage;
    
    private final Map<String, Parent> viewCache = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();
    
    private final Stack<String> navigationHistory = new Stack<>();
    private String currentView;
    
    private NavigationService() {}
    
    public static synchronized NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }
    
    public void initialize(BorderPane mainContainer, Stage primaryStage) {
        this.mainContainer = mainContainer;
        this.primaryStage = primaryStage;
        if (mainContainer.getCenter() instanceof StackPane) {
            this.contentArea = (StackPane) mainContainer.getCenter();
        }
    }
    
    public void initialize(StackPane contentArea, Stage primaryStage) {
        this.contentArea = contentArea;
        this.primaryStage = primaryStage;
    }
    
    public void navigateTo(String viewName) {
        navigateTo(viewName, true);
    }
    
    public void navigateTo(String viewName, boolean addToHistory) {
        Platform.runLater(() -> {
            try {
                Parent view = loadView(viewName);
                
                if (contentArea != null) {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(view);
                } else if (mainContainer != null) {
                    mainContainer.setCenter(view);
                }
                
                if (addToHistory && currentView != null) {
                    navigationHistory.push(currentView);
                }
                currentView = viewName;
                
                LoggerUtil.logInfo(NavigationService.class, "Navigated to: " + viewName);
                
            } catch (IOException e) {
                LoggerUtil.logError(NavigationService.class, "Failed to navigate to: " + viewName, e);
                AlertUtil.showError("Erreur de navigation", 
                    "Impossible de charger la vue: " + viewName);
            }
        });
    }
    
    public void goBack() {
        if (!navigationHistory.isEmpty()) {
            String previousView = navigationHistory.pop();
            navigateTo(previousView, false);
        }
    }
    
    public boolean canGoBack() {
        return !navigationHistory.isEmpty();
    }
    
    private Parent loadView(String viewName) throws IOException {
        
        if (viewCache.containsKey(viewName)) {
            return viewCache.get(viewName);
        }
        
        String fxmlPath = AppConfig.FXML_PATH + viewName + "_view.fxml";
        URL resource = getClass().getResource(fxmlPath);
        
        if (resource == null) {
            
            fxmlPath = AppConfig.FXML_PATH + viewName + ".fxml";
            resource = getClass().getResource(fxmlPath);
        }
        
        if (resource == null) {
            throw new IOException("FXML resource not found: " + viewName);
        }
        
        FXMLLoader loader = new FXMLLoader(resource);
        Parent view = loader.load();
        
        viewCache.put(viewName, view);
        controllerCache.put(viewName, loader.getController());
        
        return view;
    }
    
    public Parent loadFreshView(String viewName) throws IOException {
        String fxmlPath = AppConfig.FXML_PATH + viewName + "_view.fxml";
        URL resource = getClass().getResource(fxmlPath);
        
        if (resource == null) {
            fxmlPath = AppConfig.FXML_PATH + viewName + ".fxml";
            resource = getClass().getResource(fxmlPath);
        }
        
        if (resource == null) {
            throw new IOException("FXML resource not found: " + viewName);
        }
        
        FXMLLoader loader = new FXMLLoader(resource);
        return loader.load();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getController(String viewName) {
        return (T) controllerCache.get(viewName);
    }
    
    public void clearCache() {
        viewCache.clear();
        controllerCache.clear();
    }
    
    public void clearFromCache(String viewName) {
        viewCache.remove(viewName);
        controllerCache.remove(viewName);
    }
    
    public void navigateToLogin() {
        try {
            
            SessionManager.getInstance().endSession();
            
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(AppConfig.FXML_PATH + "login_view.fxml")
            );
            Parent loginView = loader.load();
            
            Scene scene = new Scene(loginView, AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
            scene.getStylesheets().add(
                getClass().getResource(AppConfig.CSS_PATH + "styles.css").toExternalForm()
            );
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(AppConfig.APP_NAME + " - Connexion");
            primaryStage.setMaximized(false);
            primaryStage.centerOnScreen();
            
            clearCache();
            navigationHistory.clear();
            currentView = null;
            
            LoggerUtil.logInfo(NavigationService.class, "Navigated to login");
            
        } catch (IOException e) {
            LoggerUtil.logError(NavigationService.class, "Failed to navigate to login", e);
            AlertUtil.showError("Erreur", "Impossible de charger l'écran de connexion");
        }
    }
    
    public void navigateToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(AppConfig.FXML_PATH + "main_view.fxml")
            );
            Parent mainView = loader.load();
            
            Scene scene = new Scene(mainView);
            scene.getStylesheets().add(
                getClass().getResource(AppConfig.CSS_PATH + "styles.css").toExternalForm()
            );
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(AppConfig.APP_NAME + " - " + AppConfig.APP_SUBTITLE);
            primaryStage.setMaximized(true);
            
            LoggerUtil.logInfo(NavigationService.class, "Navigated to main view");
            
        } catch (IOException e) {
            LoggerUtil.logError(NavigationService.class, "Failed to navigate to main", e);
            AlertUtil.showError("Erreur", "Impossible de charger l'application principale");
        }
    }
    
    public String getCurrentView() {
        return currentView;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}

package org.example.app;

/**
 * AppConfig - Application-wide configuration and constants
 */
public class AppConfig {
    
    // Application Info
    public static final String APP_NAME = "Reb7a";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_SUBTITLE = "Point de Vente – Système de Gestion Moderne";
    
    // Window Configuration
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final int MIN_WINDOW_WIDTH = 800;
    public static final int MIN_WINDOW_HEIGHT = 600;
    
    // Loading Screen
    public static final double LOADING_DURATION = 2.5; // seconds
    
    // Database Configuration
    public static final String DB_NAME = "gestion_stock_db";
    public static final String DB_URL = "jdbc:h2:file:./" + DB_NAME;
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "";
    public static final String DB_DRIVER = "org.h2.Driver";
    
    // Stock Thresholds
    public static final int DEFAULT_SEUIL_ALERTE = 10;
    public static final int JOURS_AVANCE_EXPIRATION = 30; // Alert 30 days before expiration
    
    // Business Rules
    public static final double TAUX_TVA = 0.20; // 20%
    public static final double REMISE_FIDELE = 0.05; // 5% discount for loyal customers
    public static final int POINTS_PAR_EURO = 10; // 10 points per euro spent
    
    // UI Configuration
    public static final String THEME_COLOR_PRIMARY = "#1e3c72";
    public static final String THEME_COLOR_SECONDARY = "#2a5298";
    public static final String THEME_COLOR_ACCENT = "#3b6fc9";
    
    // Date/Time Formats
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    // CSV Export Configuration
    public static final String CSV_SEPARATOR = ";";
    public static final String CSV_ENCODING = "UTF-8";
    
    // Paths
    public static final String RESOURCES_PATH = "/";
    public static final String FXML_PATH = RESOURCES_PATH + "fxml/";
    public static final String CSS_PATH = RESOURCES_PATH + "css/";
    public static final String IMAGES_PATH = RESOURCES_PATH + "images/";
    public static final String DATA_PATH = RESOURCES_PATH + "data/";
    
    // Private constructor to prevent instantiation
    private AppConfig() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}

package com.supermarket;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Splash Screen / Loading View
 * Shown before the main application with logo and loading animation
 */
public class SplashView {
    
    private Stage primaryStage;
    private Runnable onLoadComplete;
    
    public SplashView(Stage primaryStage, Runnable onLoadComplete) {
        this.primaryStage = primaryStage;
        this.onLoadComplete = onLoadComplete;
    }
    
    public void show() {
        StackPane root = new StackPane();
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #1E3C72, #2A5298);"
        );
        
        // Main content card
        VBox contentCard = createContentCard();
        
        root.getChildren().add(contentCard);
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // Start animations
        animateContent(contentCard);
        
        // Simulate loading and transition to login
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            fadeOutAndTransition(root);
        });
        delay.play();
    }
    
    private VBox createContentCard() {
        VBox card = new VBox(30);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(60));
        card.setMaxWidth(500);
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);"
        );
        
        // Logo container
        StackPane logoContainer = new StackPane();
        logoContainer.setPrefSize(120, 120);
        logoContainer.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #FFB74D, #FFE082);" +
            "-fx-background-radius: 60;"
        );
        
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#FFB74D", 0.6));
        glow.setRadius(30);
        glow.setSpread(0.5);
        logoContainer.setEffect(glow);
        
        Label logoIcon = new Label("🛒");
        logoIcon.setStyle("-fx-font-size: 60;");
        logoContainer.getChildren().add(logoIcon);
        
        // App name
        Label appName = new Label("SuperMarket");
        appName.setFont(Font.font("System", FontWeight.BOLD, 42));
        appName.setTextFill(Color.WHITE);
        
        // Subtitle
        Label subtitle = new Label("Gestion de Stock & Vente");
        subtitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 20));
        subtitle.setTextFill(Color.web("#FFFFFF", 0.9));
        
        // Description
        Label description = new Label("Gérez facilement votre stock et vos ventes");
        description.setFont(Font.font("System", FontWeight.NORMAL, 14));
        description.setTextFill(Color.web("#FFFFFF", 0.7));
        description.setWrapText(true);
        description.setMaxWidth(400);
        description.setAlignment(Pos.CENTER);
        
        // Progress indicator
        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle("-fx-progress-color: white;");
        progress.setPrefSize(50, 50);
        
        Label loadingText = new Label("Chargement...");
        loadingText.setFont(Font.font("System", FontWeight.NORMAL, 13));
        loadingText.setTextFill(Color.web("#FFFFFF", 0.8));
        
        card.getChildren().addAll(
            logoContainer, 
            appName, 
            subtitle, 
            description, 
            progress, 
            loadingText
        );
        
        return card;
    }
    
    private void animateContent(VBox card) {
        // Initial state
        card.setScaleX(0.8);
        card.setScaleY(0.8);
        card.setOpacity(0);
        
        // Scale animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(800), card);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.setInterpolator(Interpolator.EASE_OUT);
        
        // Fade animation
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(800), card);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        
        // Play together
        ParallelTransition parallel = new ParallelTransition(scaleTransition, fadeTransition);
        parallel.play();
    }
    
    private void fadeOutAndTransition(StackPane root) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            if (onLoadComplete != null) {
                onLoadComplete.run();
            }
        });
        fadeOut.play();
    }
}

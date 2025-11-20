package com.supermarket;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Login View - Mock authentication screen
 */
public class LoginView {
    
    private Stage primaryStage;
    private Runnable onLoginSuccess;
    
    public LoginView(Stage primaryStage, Runnable onLoginSuccess) {
        this.primaryStage = primaryStage;
        this.onLoginSuccess = onLoginSuccess;
    }
    
    public void show() {
        StackPane root = new StackPane();
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB);"
        );
        
        // Login card
        VBox loginCard = createLoginCard();
        
        root.getChildren().add(loginCard);
        
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
    private VBox createLoginCard() {
        VBox card = new VBox(25);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(50));
        card.setMaxWidth(450);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);"
        );
        
        // Logo
        StackPane logoContainer = new StackPane();
        logoContainer.setPrefSize(80, 80);
        logoContainer.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB);" +
            "-fx-background-radius: 40;"
        );
        
        Label logoIcon = new Label("🛒");
        logoIcon.setStyle("-fx-font-size: 40;");
        logoContainer.getChildren().add(logoIcon);
        
        // Title
        Label title = new Label("Connexion");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#222222"));
        
        Label subtitle = new Label("Accédez à votre espace de gestion");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#999999"));
        
        // Spacing
        Region spacer1 = new Region();
        spacer1.setPrefHeight(10);
        
        // Username field
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("Nom d'utilisateur");
        usernameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        usernameLabel.setTextFill(Color.web("#555555"));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Entrez votre nom d'utilisateur");
        usernameField.setPrefHeight(45);
        usernameField.setStyle(
            "-fx-background-color: #F5F7FB;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 15;" +
            "-fx-font-size: 14;" +
            "-fx-border-color: transparent;"
        );
        
        usernameField.setOnMouseEntered(e -> {
            usernameField.setStyle(
                usernameField.getStyle() +
                "-fx-border-color: #4E54C8; -fx-border-width: 2; -fx-border-radius: 10;"
            );
        });
        
        usernameField.setOnMouseExited(e -> {
            if (!usernameField.isFocused()) {
                usernameField.setStyle(
                    "-fx-background-color: #F5F7FB;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 10 15;" +
                    "-fx-font-size: 14;" +
                    "-fx-border-color: transparent;"
                );
            }
        });
        
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        passwordLabel.setTextFill(Color.web("#555555"));
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");
        passwordField.setPrefHeight(45);
        passwordField.setStyle(
            "-fx-background-color: #F5F7FB;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 15;" +
            "-fx-font-size: 14;" +
            "-fx-border-color: transparent;"
        );
        
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Remember me checkbox
        CheckBox rememberMe = new CheckBox("Se souvenir de moi");
        rememberMe.setFont(Font.font("System", FontWeight.NORMAL, 13));
        rememberMe.setTextFill(Color.web("#555555"));
        
        // Login button
        Button loginButton = new Button("Se connecter");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(50);
        loginButton.setFont(Font.font("System", FontWeight.BOLD, 15));
        loginButton.setTextFill(Color.WHITE);
        loginButton.setCursor(Cursor.HAND);
        loginButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB);" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(78,84,200,0.3), 15, 0, 0, 5);"
        );
        
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
                loginButton.getStyle() +
                "-fx-scale-x: 1.02; -fx-scale-y: 1.02;"
            );
        });
        
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB);" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(78,84,200,0.3), 15, 0, 0, 5);"
            );
        });
        
        loginButton.setOnAction(e -> {
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        });
        
        // Demo note
        Label demoNote = new Label("Version démo – aucune authentification réelle");
        demoNote.setFont(Font.font("System", FontWeight.NORMAL, 11));
        demoNote.setTextFill(Color.web("#999999"));
        demoNote.setWrapText(true);
        demoNote.setMaxWidth(350);
        demoNote.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(
            logoContainer,
            title,
            subtitle,
            spacer1,
            usernameBox,
            passwordBox,
            rememberMe,
            loginButton,
            demoNote
        );
        
        return card;
    }
}

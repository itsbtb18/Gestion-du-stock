package com.supermarket;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Header Bar Component with title, search, and user info
 */
public class HeaderBar {
    
    private HBox header;
    private Label appTitle;
    
    public HeaderBar() {
        createHeader();
    }
    
    private void createHeader() {
        header = new HBox();
        header.setPrefHeight(80);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        // Left section - App title
        appTitle = new Label("SuperMarket – Gestion de Stock & Vente");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        appTitle.setTextFill(Color.web("#222222"));
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Middle section - Search bar
        TextField searchField = createSearchField();
        
        // Right section - User info
        HBox userSection = createUserSection();
        
        header.getChildren().addAll(appTitle, spacer, searchField, userSection);
    }
    
    /**
     * Update the title text dynamically
     */
    public void updateTitle(String title) {
        if (appTitle != null) {
            appTitle.setText(title);
        }
    }
    
    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Rechercher...");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);
        searchField.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 10 20; " +
            "-fx-font-size: 14; " +
            "-fx-border-color: transparent; " +
            "-fx-prompt-text-fill: #999999;"
        );
        
        searchField.setOnMouseEntered(e -> {
            searchField.setStyle(
                searchField.getStyle() + 
                "-fx-effect: dropshadow(gaussian, rgba(78, 84, 200, 0.3), 10, 0, 0, 0);"
            );
        });
        
        searchField.setOnMouseExited(e -> {
            searchField.setStyle(
                "-fx-background-color: #F5F7FB; " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 10 20; " +
                "-fx-font-size: 14; " +
                "-fx-border-color: transparent; " +
                "-fx-prompt-text-fill: #999999;"
            );
        });
        
        HBox.setMargin(searchField, new Insets(0, 20, 0, 0));
        
        return searchField;
    }
    
    private HBox createUserSection() {
        HBox userSection = new HBox(15);
        userSection.setAlignment(Pos.CENTER_RIGHT);
        
        // Date/Time
        VBox dateTimeBox = new VBox(2);
        dateTimeBox.setAlignment(Pos.CENTER_RIGHT);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        Label dateLabel = new Label(now.format(dateFormatter));
        dateLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        dateLabel.setTextFill(Color.web("#555555"));
        
        Label timeLabel = new Label(now.format(timeFormatter));
        timeLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        timeLabel.setTextFill(Color.web("#999999"));
        
        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);
        
        // User info
        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        
        Label userName = new Label("Admin");
        userName.setFont(Font.font("System", FontWeight.BOLD, 14));
        userName.setTextFill(Color.web("#222222"));
        
        Label userRole = new Label("Administrateur");
        userRole.setFont(Font.font("System", FontWeight.NORMAL, 11));
        userRole.setTextFill(Color.web("#999999"));
        
        userInfo.getChildren().addAll(userName, userRole);
        
        // User avatar
        StackPane avatarContainer = new StackPane();
        Circle avatar = new Circle(22);
        avatar.setFill(Color.web("#4E54C8"));
        avatar.setEffect(createSoftShadow());
        
        Label avatarInitials = new Label("AD");
        avatarInitials.setFont(Font.font("System", FontWeight.BOLD, 12));
        avatarInitials.setTextFill(Color.WHITE);
        
        avatarContainer.getChildren().addAll(avatar, avatarInitials);
        
        userSection.getChildren().addAll(dateTimeBox, userInfo, avatarContainer);
        
        return userSection;
    }
    
    private DropShadow createSoftShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#4E54C8", 0.3));
        shadow.setRadius(10);
        shadow.setSpread(0.2);
        return shadow;
    }
    
    public HBox getView() {
        return header;
    }
}

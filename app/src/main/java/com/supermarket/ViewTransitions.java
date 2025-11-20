package com.supermarket;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Utility class for view transitions and animations
 */
public class ViewTransitions {
    
    /**
     * Fade transition when switching views
     */
    public static void fadeTransition(StackPane container, Pane newView) {
        // If container is empty, just add the new view
        if (container.getChildren().isEmpty()) {
            container.getChildren().add(newView);
            fadeIn(newView);
            return;
        }
        
        // Get current view
        Node currentView = container.getChildren().get(0);
        
        // Fade out current view
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), currentView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            // Replace with new view
            container.getChildren().clear();
            container.getChildren().add(newView);
            
            // Fade in new view
            fadeIn(newView);
        });
        
        fadeOut.play();
    }
    
    /**
     * Fade in animation for a node
     */
    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    /**
     * Slide transition from right
     */
    public static void slideTransition(StackPane container, Pane newView) {
        // If container is empty, just add the new view
        if (container.getChildren().isEmpty()) {
            container.getChildren().add(newView);
            slideInFromRight(newView);
            return;
        }
        
        // Get current view
        Node currentView = container.getChildren().get(0);
        
        // Slide out current view to left
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), currentView);
        slideOut.setFromX(0);
        slideOut.setToX(-container.getWidth());
        
        slideOut.setOnFinished(e -> {
            // Replace with new view
            container.getChildren().clear();
            container.getChildren().add(newView);
            
            // Slide in new view from right
            slideInFromRight(newView);
        });
        
        slideOut.play();
    }
    
    /**
     * Slide in from right animation
     */
    private static void slideInFromRight(Node node) {
        node.setTranslateX(100);
        node.setOpacity(0.5);
        
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), node);
        slideIn.setFromX(100);
        slideIn.setToX(0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
        fadeIn.setFromValue(0.5);
        fadeIn.setToValue(1.0);
        
        slideIn.play();
        fadeIn.play();
    }
}

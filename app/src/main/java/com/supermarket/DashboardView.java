package com.supermarket;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Dashboard View with statistics cards and charts
 */
public class DashboardView {
    
    private VBox mainView;
    
    public DashboardView() {
        createDashboard();
    }
    
    private void createDashboard() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        // Title
        Label title = new Label("Tableau de Bord");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        // Statistics cards row
        HBox statsRow = createStatsCards();
        
        // Middle row with charts
        HBox middleRow = createMiddleRow();
        
        // Bottom row with monthly revenue chart
        VBox bottomRow = createBottomRow();
        
        mainView.getChildren().addAll(title, statsRow, middleRow, bottomRow);
    }
    
    private HBox createStatsCards() {
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        
        // Card 1: Total Stock
        VBox card1 = createStatCard("📦", "12,345", "Stock Total", "#4E54C8", "#8F94FB", 0);
        
        // Card 2: Sales Today
        VBox card2 = createStatCard("💰", "120", "Ventes Aujourd'hui", "#FF6E7F", "#BFE9FF", 100);
        
        // Card 3: Revenue Today
        VBox card3 = createStatCard("💵", "450,000 DA", "Revenu Aujourd'hui", "#66BB6A", "#A5D6A7", 200);
        
        // Card 4: Out of Stock
        VBox card4 = createStatCard("⚠️", "5", "Ruptures de Stock", "#FFB74D", "#FFE082", 300);
        
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);
        
        statsRow.getChildren().addAll(card1, card2, card3, card4);
        
        return statsRow;
    }
    
    private VBox createStatCard(String icon, String value, String label, String colorFrom, String colorTo, int delay) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("stat-card");
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);"
        );
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 40;");
        
        // Value
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        valueLabel.setTextFill(Color.WHITE);
        
        // Description
        Label descLabel = new Label(label);
        descLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        descLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        card.getChildren().addAll(iconLabel, valueLabel, descLabel);
        
        // Hover animation
        card.setOnMouseEntered(e -> animateCardHover(card, true));
        card.setOnMouseExited(e -> animateCardHover(card, false));
        
        // Entry animation
        animateCardEntry(card, delay);
        
        return card;
    }
    
    private void animateCardHover(VBox card, boolean hover) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        if (hover) {
            st.setToX(1.05);
            st.setToY(1.05);
        } else {
            st.setToX(1.0);
            st.setToY(1.0);
        }
        st.play();
    }
    
    private void animateCardEntry(VBox card, int delay) {
        card.setOpacity(0);
        card.setScaleX(0.8);
        card.setScaleY(0.8);
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(delay));
        
        ScaleTransition st = new ScaleTransition(Duration.millis(500), card);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setDelay(Duration.millis(delay));
        
        ft.play();
        st.play();
    }
    
    private HBox createMiddleRow() {
        HBox middleRow = new HBox(20);
        middleRow.setPrefHeight(350);
        
        // Left: Bar chart for weekly sales
        VBox chartContainer = createWeeklySalesChart();
        HBox.setHgrow(chartContainer, Priority.ALWAYS);
        
        // Right: Low stock table
        VBox tableContainer = createLowStockTable();
        tableContainer.setPrefWidth(450);
        
        middleRow.getChildren().addAll(chartContainer, tableContainer);
        
        return middleRow;
    }
    
    private VBox createWeeklySalesChart() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label title = new Label("Ventes par Jour de la Semaine");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#222222"));
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ventes");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Lun", 45));
        series.getData().add(new XYChart.Data<>("Mar", 52));
        series.getData().add(new XYChart.Data<>("Mer", 38));
        series.getData().add(new XYChart.Data<>("Jeu", 61));
        series.getData().add(new XYChart.Data<>("Ven", 78));
        series.getData().add(new XYChart.Data<>("Sam", 95));
        series.getData().add(new XYChart.Data<>("Dim", 68));
        
        barChart.getData().add(series);
        VBox.setVgrow(barChart, Priority.ALWAYS);
        
        container.getChildren().addAll(title, barChart);
        
        return container;
    }
    
    private VBox createLowStockTable() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label title = new Label("Produits en Faible Stock");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#222222"));
        
        TableView<LowStockProduct> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<LowStockProduct, String> productCol = new TableColumn<>("Produit");
        productCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        productCol.setPrefWidth(120);
        
        TableColumn<LowStockProduct, String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);
        
        TableColumn<LowStockProduct, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setPrefWidth(60);
        
        TableColumn<LowStockProduct, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        table.getColumns().addAll(productCol, categoryCol, stockCol, statusCol);
        
        // Mock data
        ObservableList<LowStockProduct> data = FXCollections.observableArrayList(
            new LowStockProduct("Lait Candia", "Produits laitiers", 8, "⚠️ Faible"),
            new LowStockProduct("Pain blanc", "Boulangerie", 12, "⚠️ Faible"),
            new LowStockProduct("Huile Elio", "Épicerie", 3, "🔴 Critique"),
            new LowStockProduct("Café Legal", "Boissons", 15, "⚠️ Faible"),
            new LowStockProduct("Sucre Blanc", "Épicerie", 5, "🔴 Critique")
        );
        
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        container.getChildren().addAll(title, table);
        
        return container;
    }
    
    private VBox createBottomRow() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label title = new Label("Revenu Mensuel");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#222222"));
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenu (DA)");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);
        lineChart.setPrefHeight(250);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Jan", 320000));
        series.getData().add(new XYChart.Data<>("Fév", 385000));
        series.getData().add(new XYChart.Data<>("Mar", 410000));
        series.getData().add(new XYChart.Data<>("Avr", 395000));
        series.getData().add(new XYChart.Data<>("Mai", 450000));
        series.getData().add(new XYChart.Data<>("Jun", 475000));
        series.getData().add(new XYChart.Data<>("Jul", 520000));
        series.getData().add(new XYChart.Data<>("Aoû", 485000));
        series.getData().add(new XYChart.Data<>("Sep", 510000));
        series.getData().add(new XYChart.Data<>("Oct", 545000));
        series.getData().add(new XYChart.Data<>("Nov", 580000));
        
        lineChart.getData().add(series);
        
        container.getChildren().addAll(title, lineChart);
        
        return container;
    }
    
    public VBox getView() {
        return mainView;
    }
    
    // Inner class for table data
    public static class LowStockProduct {
        private String product;
        private String category;
        private int stock;
        private String status;
        
        public LowStockProduct(String product, String category, int stock, String status) {
            this.product = product;
            this.category = category;
            this.stock = stock;
            this.status = status;
        }
        
        public String getProduct() { return product; }
        public String getCategory() { return category; }
        public int getStock() { return stock; }
        public String getStatus() { return status; }
    }
}

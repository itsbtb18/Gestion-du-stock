package com.supermarket;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

/**
 * Reports View with filters and mock report generation
 */
public class ReportsView {
    
    private VBox mainView;
    
    public ReportsView() {
        createReportsView();
    }
    
    private void createReportsView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        Label title = new Label("Rapports et Analyses");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        // Filter section
        VBox filterSection = createFilterSection();
        
        // Report results section
        VBox resultsSection = createResultsSection();
        VBox.setVgrow(resultsSection, Priority.ALWAYS);
        
        mainView.getChildren().addAll(title, filterSection, resultsSection);
    }
    
    private VBox createFilterSection() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(25));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label filterTitle = new Label("Filtres de Rapport");
        filterTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        filterTitle.setTextFill(Color.web("#222222"));
        
        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(20);
        filterGrid.setVgap(15);
        
        // Report type
        Label typeLabel = new Label("Type de rapport:");
        typeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(
            "Rapport de Ventes",
            "Rapport de Stock",
            "Rapport Clients",
            "Rapport Financier"
        );
        typeBox.setValue("Rapport de Ventes");
        typeBox.setPrefWidth(250);
        
        // Date range
        Label dateLabel = new Label("Période:");
        dateLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        
        HBox dateBox = new HBox(10);
        DatePicker startDate = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker endDate = new DatePicker(LocalDate.now());
        startDate.setPrefWidth(150);
        endDate.setPrefWidth(150);
        dateBox.getChildren().addAll(new Label("Du:"), startDate, new Label("Au:"), endDate);
        
        // Format
        Label formatLabel = new Label("Format:");
        formatLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        ComboBox<String> formatBox = new ComboBox<>();
        formatBox.getItems().addAll("PDF", "Excel", "CSV");
        formatBox.setValue("PDF");
        formatBox.setPrefWidth(120);
        
        filterGrid.add(typeLabel, 0, 0);
        filterGrid.add(typeBox, 1, 0);
        filterGrid.add(dateLabel, 0, 1);
        filterGrid.add(dateBox, 1, 1);
        filterGrid.add(formatLabel, 0, 2);
        filterGrid.add(formatBox, 1, 2);
        
        // Generate button
        Button generateBtn = createGradientButton("📊 Générer Rapport", "#4E54C8", "#8F94FB");
        generateBtn.setPrefWidth(200);
        generateBtn.setPrefHeight(45);
        generateBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        HBox buttonBox = new HBox(generateBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        section.getChildren().addAll(filterTitle, filterGrid, buttonBox);
        
        return section;
    }
    
    private VBox createResultsSection() {
        VBox section = new VBox(20);
        
        // Summary cards
        HBox summaryCards = createSummaryCards();
        
        // Charts row
        HBox chartsRow = new HBox(20);
        
        VBox salesChart = createSalesComparisonChart();
        HBox.setHgrow(salesChart, Priority.ALWAYS);
        
        VBox categoryChart = createCategoryChart();
        categoryChart.setPrefWidth(400);
        
        chartsRow.getChildren().addAll(salesChart, categoryChart);
        
        section.getChildren().addAll(summaryCards, chartsRow);
        
        return section;
    }
    
    private HBox createSummaryCards() {
        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        
        VBox card1 = createReportCard("💰", "Ventes Totales", "1,250,000 DA", "#66BB6A", "#A5D6A7");
        VBox card2 = createReportCard("📦", "Produits Vendus", "3,456 unités", "#4E54C8", "#8F94FB");
        VBox card3 = createReportCard("👥", "Nouveaux Clients", "87 clients", "#FFB74D", "#FFE082");
        VBox card4 = createReportCard("📈", "Croissance", "+15.3%", "#FF6E7F", "#BFE9FF");
        
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);
        
        cards.getChildren().addAll(card1, card2, card3, card4);
        
        return cards;
    }
    
    private VBox createReportCard(String icon, String label, String value, String colorFrom, String colorTo) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 3);"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32;");
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.WHITE);
        
        Label descLabel = new Label(label);
        descLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        descLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        card.getChildren().addAll(iconLabel, valueLabel, descLabel);
        
        return card;
    }
    
    private VBox createSalesComparisonChart() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label chartTitle = new Label("Comparaison des Ventes Mensuelles");
        chartTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        chartTitle.setTextFill(Color.web("#222222"));
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ventes (DA)");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setAnimated(true);
        lineChart.setPrefHeight(300);
        
        XYChart.Series<String, Number> series2024 = new XYChart.Series<>();
        series2024.setName("2024");
        series2024.getData().add(new XYChart.Data<>("Jan", 285000));
        series2024.getData().add(new XYChart.Data<>("Fév", 310000));
        series2024.getData().add(new XYChart.Data<>("Mar", 298000));
        series2024.getData().add(new XYChart.Data<>("Avr", 325000));
        series2024.getData().add(new XYChart.Data<>("Mai", 340000));
        series2024.getData().add(new XYChart.Data<>("Jun", 355000));
        
        XYChart.Series<String, Number> series2025 = new XYChart.Series<>();
        series2025.setName("2025");
        series2025.getData().add(new XYChart.Data<>("Jan", 320000));
        series2025.getData().add(new XYChart.Data<>("Fév", 385000));
        series2025.getData().add(new XYChart.Data<>("Mar", 410000));
        series2025.getData().add(new XYChart.Data<>("Avr", 395000));
        series2025.getData().add(new XYChart.Data<>("Mai", 450000));
        series2025.getData().add(new XYChart.Data<>("Jun", 475000));
        
        lineChart.getData().addAll(series2024, series2025);
        VBox.setVgrow(lineChart, Priority.ALWAYS);
        
        container.getChildren().addAll(chartTitle, lineChart);
        
        return container;
    }
    
    private VBox createCategoryChart() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label chartTitle = new Label("Ventes par Catégorie");
        chartTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        chartTitle.setTextFill(Color.web("#222222"));
        
        PieChart pieChart = new PieChart();
        pieChart.setAnimated(true);
        pieChart.setPrefHeight(300);
        
        PieChart.Data slice1 = new PieChart.Data("Épicerie", 35);
        PieChart.Data slice2 = new PieChart.Data("Produits laitiers", 25);
        PieChart.Data slice3 = new PieChart.Data("Boissons", 20);
        PieChart.Data slice4 = new PieChart.Data("Boulangerie", 15);
        PieChart.Data slice5 = new PieChart.Data("Autres", 5);
        
        pieChart.getData().addAll(slice1, slice2, slice3, slice4, slice5);
        
        container.getChildren().addAll(chartTitle, pieChart);
        
        return container;
    }
    
    private Button createGradientButton(String text, String colorFrom, String colorTo) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        button.setTextFill(Color.WHITE);
        button.setCursor(Cursor.HAND);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
        );
        
        return button;
    }
    
    public VBox getView() {
        return mainView;
    }
}

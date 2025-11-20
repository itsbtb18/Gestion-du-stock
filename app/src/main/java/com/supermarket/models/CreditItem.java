package com.supermarket.models;

/**
 * Model class representing an item in a credit sale
 */
public class CreditItem {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    
    public CreditItem(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }
    
    // Getters
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
    
    // For JavaFX table display
    public String getUnitPriceString() { return String.format("%.2f DA", unitPrice); }
    public String getTotalPriceString() { return String.format("%.2f DA", totalPrice); }
}

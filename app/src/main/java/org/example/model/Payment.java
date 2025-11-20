package org.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing a payment made towards a credit sale
 */
public class Payment {
    private double amount;
    private LocalDate date;
    private String paymentMethod; // "Espèces", "Carte", "Virement"
    
    public Payment(double amount, LocalDate date, String paymentMethod) {
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDateString() { 
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); 
    }
    public String getPaymentMethod() { return paymentMethod; }
    
    public String getAmountString() { return String.format("%.2f DA", amount); }
}

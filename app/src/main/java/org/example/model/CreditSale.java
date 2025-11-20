package org.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a credit sale
 */
public class CreditSale {
    private String clientId;
    private String nom;
    private String prenom;
    private double totalAmount;
    private double remainingAmount;
    private LocalDate date;
    private List<CreditItem> items;
    private String status; // "Non payé", "Partiellement payé", "Payé"
    private List<Payment> payments;
    
    public CreditSale(String clientId, String nom, String prenom, double totalAmount, LocalDate date) {
        this.clientId = clientId;
        this.nom = nom;
        this.prenom = prenom;
        this.totalAmount = totalAmount;
        this.remainingAmount = totalAmount;
        this.date = date;
        this.items = new ArrayList<>();
        this.status = "Non payé";
        this.payments = new ArrayList<>();
    }
    
    public void addItem(CreditItem item) {
        items.add(item);
    }
    
    public void addPayment(Payment payment) {
        payments.add(payment);
        remainingAmount -= payment.getAmount();
        updateStatus();
    }
    
    private void updateStatus() {
        if (remainingAmount <= 0) {
            status = "Payé";
            remainingAmount = 0;
        } else if (remainingAmount < totalAmount) {
            status = "Partiellement payé";
        } else {
            status = "Non payé";
        }
    }
    
    public void markAsPaid() {
        remainingAmount = 0;
        status = "Payé";
    }
    
    // Getters
    public String getClientId() { return clientId; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getFullName() { return prenom + " " + nom; }
    public double getTotalAmount() { return totalAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    public LocalDate getDate() { return date; }
    public String getDateString() { 
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); 
    }
    public List<CreditItem> getItems() { return items; }
    public String getStatus() { return status; }
    public List<Payment> getPayments() { return payments; }
    
    // Setters
    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
        updateStatus();
    }
}

package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * TransactionFidelite - Entity representing a loyalty points transaction
 */
public class TransactionFidelite {
    
    private Long id;
    private CarteFidelite carte;
    private String type; // GAIN, UTILISATION, EXPIRATION, AJUSTEMENT
    private int points;
    private LocalDateTime dateTransaction;
    private Vente vente; // Optional reference to related sale
    private String description;
    
    // Constructors
    public TransactionFidelite() {
        this.dateTransaction = LocalDateTime.now();
    }
    
    public TransactionFidelite(CarteFidelite carte, String type, int points, String description) {
        this();
        this.carte = carte;
        this.type = type;
        this.points = points;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public CarteFidelite getCarte() {
        return carte;
    }
    
    public void setCarte(CarteFidelite carte) {
        this.carte = carte;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }
    
    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }
    
    public Vente getVente() {
        return vente;
    }
    
    public void setVente(Vente vente) {
        this.vente = vente;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

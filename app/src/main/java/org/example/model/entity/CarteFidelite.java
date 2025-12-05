package org.example.model.entity;

import java.time.LocalDate;

/**
 * CarteFidelite - Entity representing a loyalty card
 */
public class CarteFidelite {
    
    private Long id;
    private String numero;
    private Client client;
    private LocalDate dateCreation;
    private LocalDate dateExpiration;
    private int pointsAccumules;
    private int pointsUtilises;
    private String statut; // ACTIVE, EXPIREE, BLOQUEE
    
    // Constructors
    public CarteFidelite() {
        this.numero = genererNumero();
        this.dateCreation = LocalDate.now();
        this.dateExpiration = dateCreation.plusYears(2);
        this.pointsAccumules = 0;
        this.pointsUtilises = 0;
        this.statut = "ACTIVE";
    }
    
    public CarteFidelite(Client client) {
        this();
        this.client = client;
    }
    
    // Business logic
    private String genererNumero() {
        return "CARD" + System.currentTimeMillis();
    }
    
    public int getPointsDisponibles() {
        return pointsAccumules - pointsUtilises;
    }
    
    public void ajouterPoints(int points) {
        this.pointsAccumules += points;
    }
    
    public boolean utiliserPoints(int points) {
        if (getPointsDisponibles() >= points && "ACTIVE".equals(statut)) {
            this.pointsUtilises += points;
            return true;
        }
        return false;
    }
    
    public boolean isExpiree() {
        return dateExpiration.isBefore(LocalDate.now());
    }
    
    public void renouveler() {
        this.dateExpiration = LocalDate.now().plusYears(2);
        this.statut = "ACTIVE";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }
    
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    
    public int getPointsAccumules() {
        return pointsAccumules;
    }
    
    public void setPointsAccumules(int pointsAccumules) {
        this.pointsAccumules = pointsAccumules;
    }
    
    public int getPointsUtilises() {
        return pointsUtilises;
    }
    
    public void setPointsUtilises(int pointsUtilises) {
        this.pointsUtilises = pointsUtilises;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
}

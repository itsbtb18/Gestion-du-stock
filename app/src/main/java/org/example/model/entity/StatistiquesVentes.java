package org.example.model.entity;

import java.time.LocalDate;

/**
 * StatistiquesVentes - Entity representing sales statistics
 */
public class StatistiquesVentes {
    
    private LocalDate date;
    private int nombreVentes;
    private double chiffreAffaires;
    private double montantRemises;
    private double montantTVA;
    private int nombreArticlesVendus;
    private double panierMoyen;
    private Produit produitPlusVendu;
    private int quantiteProduitPlusVendu;
    
    // Constructors
    public StatistiquesVentes() {
        this.date = LocalDate.now();
        this.nombreVentes = 0;
        this.chiffreAffaires = 0.0;
        this.montantRemises = 0.0;
        this.montantTVA = 0.0;
        this.nombreArticlesVendus = 0;
        this.panierMoyen = 0.0;
    }
    
    public StatistiquesVentes(LocalDate date) {
        this();
        this.date = date;
    }
    
    // Business logic
    public void calculerPanierMoyen() {
        if (nombreVentes > 0) {
            this.panierMoyen = chiffreAffaires / nombreVentes;
        } else {
            this.panierMoyen = 0.0;
        }
    }
    
    public double getTauxRemise() {
        if (chiffreAffaires > 0) {
            return (montantRemises / chiffreAffaires) * 100.0;
        }
        return 0.0;
    }
    
    public double getMargeCommerciale() {
        // Simplified: assuming 30% margin
        return chiffreAffaires * 0.30;
    }
    
    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getNombreVentes() {
        return nombreVentes;
    }
    
    public void setNombreVentes(int nombreVentes) {
        this.nombreVentes = nombreVentes;
        calculerPanierMoyen();
    }
    
    public double getChiffreAffaires() {
        return chiffreAffaires;
    }
    
    public void setChiffreAffaires(double chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
        calculerPanierMoyen();
    }
    
    public double getMontantRemises() {
        return montantRemises;
    }
    
    public void setMontantRemises(double montantRemises) {
        this.montantRemises = montantRemises;
    }
    
    public double getMontantTVA() {
        return montantTVA;
    }
    
    public void setMontantTVA(double montantTVA) {
        this.montantTVA = montantTVA;
    }
    
    public int getNombreArticlesVendus() {
        return nombreArticlesVendus;
    }
    
    public void setNombreArticlesVendus(int nombreArticlesVendus) {
        this.nombreArticlesVendus = nombreArticlesVendus;
    }
    
    public double getPanierMoyen() {
        return panierMoyen;
    }
    
    public void setPanierMoyen(double panierMoyen) {
        this.panierMoyen = panierMoyen;
    }
    
    public Produit getProduitPlusVendu() {
        return produitPlusVendu;
    }
    
    public void setProduitPlusVendu(Produit produitPlusVendu) {
        this.produitPlusVendu = produitPlusVendu;
    }
    
    public int getQuantiteProduitPlusVendu() {
        return quantiteProduitPlusVendu;
    }
    
    public void setQuantiteProduitPlusVendu(int quantiteProduitPlusVendu) {
        this.quantiteProduitPlusVendu = quantiteProduitPlusVendu;
    }
}

package org.example.model.entity;

import java.time.LocalDate;

/**
 * Produit - Entity class representing a product in the store
 * This is the Model in MVC architecture
 */
public class Produit {
    
    private Long id;
    private String code;
    private String nom;
    private String description;
    private double prix;
    private int quantiteStock;
    private int seuilAlerte;
    private Categorie categorie;
    private String unite; // kg, L, pièce, etc.
    private LocalDate dateExpiration;
    private String fournisseur;
    private String emplacement;
    private boolean actif;
    
    // Constructors
    public Produit() {
        this.actif = true;
    }
    
    public Produit(String code, String nom, String description, double prix, 
                   int quantiteStock, Categorie categorie, String unite) {
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.categorie = categorie;
        this.unite = unite;
        this.actif = true;
    }
    
    // Business logic methods
    public boolean isStockBas() {
        return quantiteStock <= seuilAlerte;
    }
    
    public boolean isExpireSoon(int joursAvance) {
        if (dateExpiration == null) return false;
        return dateExpiration.minusDays(joursAvance).isBefore(LocalDate.now());
    }
    
    public boolean isExpire() {
        if (dateExpiration == null) return false;
        return dateExpiration.isBefore(LocalDate.now());
    }
    
    public void ajouterStock(int quantite) {
        this.quantiteStock += quantite;
    }
    
    public void retirerStock(int quantite) throws IllegalArgumentException {
        if (quantite > this.quantiteStock) {
            throw new IllegalArgumentException("Stock insuffisant");
        }
        this.quantiteStock -= quantite;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public int getQuantiteStock() {
        return quantiteStock;
    }
    
    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }
    
    public int getSeuilAlerte() {
        return seuilAlerte;
    }
    
    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
    
    public Categorie getCategorie() {
        return categorie;
    }
    
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }
    
    public String getUnite() {
        return unite;
    }
    
    public void setUnite(String unite) {
        this.unite = unite;
    }
    
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }
    
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    
    public String getFournisseur() {
        return fournisseur;
    }
    
    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }
    
    public String getEmplacement() {
        return emplacement;
    }
    
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    // Alias method for compatibility
    public double getPrixVente() {
        return prix;
    }
    
    @Override
    public String toString() {
        return "Produit{" +
                "code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", stock=" + quantiteStock +
                '}';
    }
}

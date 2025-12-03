package org.example.model.pattern.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * ProduitComponent - Abstract base class for Composite pattern
 * Represents both simple products and composite products (bundles)
 * Part of the Model layer in MVC architecture
 */
public abstract class ProduitComponent {
    
    protected String code;
    protected String nom;
    protected String description;
    
    public ProduitComponent(String code, String nom, String description) {
        this.code = code;
        this.nom = nom;
        this.description = description;
    }
    
    // Abstract methods that must be implemented by concrete classes
    public abstract double getPrix();
    public abstract int getQuantiteStock();
    public abstract void afficher(String indent);
    
    // Default implementations for composite operations
    // These will be overridden in ProduitCompose
    public void ajouter(ProduitComponent produit) {
        throw new UnsupportedOperationException("Operation non supportée pour ce type de produit");
    }
    
    public void retirer(ProduitComponent produit) {
        throw new UnsupportedOperationException("Operation non supportée pour ce type de produit");
    }
    
    public ProduitComponent getEnfant(int index) {
        throw new UnsupportedOperationException("Operation non supportée pour ce type de produit");
    }
    
    public List<ProduitComponent> getEnfants() {
        return new ArrayList<>();
    }
    
    public boolean isComposite() {
        return false;
    }
    
    // Getters
    public String getCode() {
        return code;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Setters
    public void setCode(String code) {
        this.code = code;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

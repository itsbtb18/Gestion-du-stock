package org.example.model.entity;

public enum Role {
    ADMIN("Administrateur", "Accès complet au système"),
    GERANT("Gérant", "Gestion des stocks et rapports"),
    CAISSIER("Caissier", "Gestion des ventes en caisse"),
    VENDEUR("Vendeur", "Consultation et vente basique");
    
    private final String libelle;
    private final String description;
    
    Role(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}

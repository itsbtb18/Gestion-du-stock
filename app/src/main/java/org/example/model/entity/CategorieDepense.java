package org.example.model.entity;

/**
 * CategorieDepense - Expense category enum
 */
public enum CategorieDepense {
    LOYER("Loyer"),
    ELECTRICITE("Électricité"),
    EAU("Eau"),
    TELEPHONE("Téléphone/Internet"),
    SALAIRES("Salaires"),
    MARKETING("Marketing/Publicité"),
    FOURNITURES("Fournitures"),
    EQUIPEMENT("Équipement"),
    MAINTENANCE("Maintenance"),
    TRANSPORT("Transport"),
    ASSURANCE("Assurance"),
    TAXES("Taxes/Impôts"),
    SERVICES("Services Professionnels"),
    AUTRE("Autre");
    
    private final String libelle;
    
    CategorieDepense(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

package org.example.model.entity;

public class Categorie {
    
    private Long id;
    private String code;
    private String nom;
    private String description;
    private boolean actif;
    
    public Categorie() {
        this.actif = true;
    }
    
    public Categorie(String code, String nom, String description) {
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.actif = true;
    }
    
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
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    @Override
    public String toString() {
        return "Categorie{" +
                "code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }
}

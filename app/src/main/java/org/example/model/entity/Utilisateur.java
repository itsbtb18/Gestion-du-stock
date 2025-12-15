package org.example.model.entity;

import java.time.LocalDateTime;

public class Utilisateur {
    
    private Long id;
    private String username;
    private String password; 
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    private boolean actif;
    
    public Utilisateur() {
        this.dateCreation = LocalDateTime.now();
        this.actif = true;
    }
    
    public Utilisateur(String username, String password, String nom, String prenom, Role role) {
        this();
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }
    
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    public boolean verifierMotDePasse(String motDePasse) {
        
        return this.password.equals(motDePasse);
    }
    
    public void enregistrerConnexion() {
        this.derniereConnexion = LocalDateTime.now();
    }
    
    public boolean hasRole(Role... roles) {
        for (Role r : roles) {
            if (this.role == r) {
                return true;
            }
        }
        return false;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }
    
    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    @Override
    public String toString() {
        return username + " (" + role.getLibelle() + ")";
    }
}

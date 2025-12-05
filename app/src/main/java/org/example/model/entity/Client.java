package org.example.model.entity;

import java.time.LocalDate;

/**
 * Client - Entity class representing a customer
 */
public class Client {
    
    private Long id;
    private String code;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private TypeClient typeClient;
    private LocalDate dateInscription;
    private double totalAchats;
    private int pointsFidelite;
    private CarteFidelite carteFidelite;
    private boolean actif;
    
    // Constructors
    public Client() {
        this.typeClient = TypeClient.NORMAL;
        this.dateInscription = LocalDate.now();
        this.totalAchats = 0.0;
        this.pointsFidelite = 0;
        this.actif = true;
    }
    
    public Client(String code, String nom, String prenom, String telephone) {
        this();
        this.code = code;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }
    
    // Business logic
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    public void ajouterPoints(int points) {
        this.pointsFidelite += points;
    }
    
    public boolean retirerPoints(int points) {
        if (this.pointsFidelite >= points) {
            this.pointsFidelite -= points;
            return true;
        }
        return false;
    }
    
    public void enregistrerAchat(double montant) {
        this.totalAchats += montant;
        // Award loyalty points: 10 points per euro
        int pointsGagnes = (int) (montant * 10);
        ajouterPoints(pointsGagnes);
    }
    
    public double getRemiseDisponible() {
        return typeClient.getTauxRemise();
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
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public TypeClient getTypeClient() {
        return typeClient;
    }
    
    public void setTypeClient(TypeClient typeClient) {
        this.typeClient = typeClient;
    }
    
    public LocalDate getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    public double getTotalAchats() {
        return totalAchats;
    }
    
    public void setTotalAchats(double totalAchats) {
        this.totalAchats = totalAchats;
    }
    
    public int getPointsFidelite() {
        return pointsFidelite;
    }
    
    public void setPointsFidelite(int pointsFidelite) {
        this.pointsFidelite = pointsFidelite;
    }
    
    public CarteFidelite getCarteFidelite() {
        return carteFidelite;
    }
    
    public void setCarteFidelite(CarteFidelite carteFidelite) {
        this.carteFidelite = carteFidelite;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    @Override
    public String toString() {
        return getNomComplet() + " (" + code + ")";
    }
}

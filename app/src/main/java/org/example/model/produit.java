package org.example.model;

import java.util.Date;

public class produit {
    private String id;
    private String nom;
    private categorie categorie;
    private double prixAchat;
    private double prixVente;
    private Date dateExpiration;
    private String codeBar;
    private Fournisseur fournisseur;

    public produit() {
    }

    public produit(categorie categorie, double prixAchat, double prixVente, Date dateExpiration, String codeBar, String nom, Fournisseur fournisseur) {
        this.categorie = categorie;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.dateExpiration = dateExpiration;
        this.codeBar = codeBar;
        this.nom = nom;
        this.fournisseur = fournisseur;
    }

    public categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(categorie categorie) {
        this.categorie = categorie;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getCodeBar() {
        return codeBar;
    }

    public void setCodeBar(String codeBar) {
        this.codeBar = codeBar;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
}

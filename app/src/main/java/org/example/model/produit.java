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
    private Fournisseur id_fournisseur;

    public produit() {
    }

    public produit(String id, String nom, categorie categorie, double prixAchat, double prixVente,
                   Date dateExpiration, String codeBar, Fournisseur fournisseur) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.dateExpiration = dateExpiration;
        this.codeBar = codeBar;
        this.id_fournisseur = fournisseur;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public Fournisseur getFournisseur() {
        return id_fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.id_fournisseur = fournisseur;
    }

    @Override
    public String toString() {
        return "produit{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", categorie=" + categorie +
                ", prixAchat=" + prixAchat +
                ", prixVente=" + prixVente +
                ", dateExpiration=" + dateExpiration +
                ", codeBar='" + codeBar + '\'' +
                ", fournisseur=" + id_fournisseur +
                '}';
    }
}
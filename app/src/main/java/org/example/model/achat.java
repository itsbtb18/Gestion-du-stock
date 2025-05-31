package org.example.model;

public class achat {
    private int id;
    private String dateAchat;
    private double montantTotal;
    private String fournisseur;

    public achat(int id, String dateAchat, double montantTotal, String fournisseur) {
        this.id = id;
        this.dateAchat = dateAchat;
        this.montantTotal = montantTotal;
        this.fournisseur = fournisseur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(String dateAchat) {
        this.dateAchat = dateAchat;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }
    @Override
    public String toString() {
        return "Achat{" +
                "id=" + id +
                ", dateAchat='" + dateAchat + '\'' +
                ", montantTotal=" + montantTotal +
                ", fournisseur='" + fournisseur + '\'' +
                '}';
    }
}

package org.example.model;

public class vente {
    private int id;
    private String date;
    private double montantTotal;
    private String modePaiement;
    private int clientId;

    public vente(int id, String date, double montantTotal, String modePaiement, int clientId) {
        this.id = id;
        this.date = date;
        this.montantTotal = montantTotal;
        this.modePaiement = modePaiement;
        this.clientId = clientId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "Vente{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", montantTotal=" + montantTotal +
                ", modePaiement='" + modePaiement + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}

package org.example.model.entity;

import java.time.LocalDateTime;

public class Caisse {
    
    private Long id;
    private String numeroCaisse;
    private Utilisateur caissier;
    private LocalDateTime dateOuverture;
    private LocalDateTime dateFermeture;
    private Double soldeDepartEspeces;
    private Double soldeFinEspeces;
    private Double totalVentesEspeces;
    private Double totalVentesCarte;
    private Double totalVentesAutre;
    private Double totalDepenses;
    private Double ecart; 
    private StatutCaisse statut;
    private String commentaire;
    
    public Caisse() {
        this.dateOuverture = LocalDateTime.now();
        this.statut = StatutCaisse.OUVERTE;
        this.soldeDepartEspeces = 0.0;
        this.totalVentesEspeces = 0.0;
        this.totalVentesCarte = 0.0;
        this.totalVentesAutre = 0.0;
        this.totalDepenses = 0.0;
    }
    
    public void fermerCaisse(Double soldeFinEspeces) {
        this.dateFermeture = LocalDateTime.now();
        this.soldeFinEspeces = soldeFinEspeces;
        this.statut = StatutCaisse.FERMEE;
        calculerEcart();
    }
    
    public void calculerEcart() {
        Double soldeAttendu = soldeDepartEspeces + totalVentesEspeces - totalDepenses;
        this.ecart = soldeFinEspeces - soldeAttendu;
    }
    
    public Double getSoldeAttendu() {
        return soldeDepartEspeces + totalVentesEspeces - totalDepenses;
    }
    
    public Double getTotalVentes() {
        return totalVentesEspeces + totalVentesCarte + totalVentesAutre;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroCaisse() {
        return numeroCaisse;
    }
    
    public void setNumeroCaisse(String numeroCaisse) {
        this.numeroCaisse = numeroCaisse;
    }
    
    public Utilisateur getCaissier() {
        return caissier;
    }
    
    public void setCaissier(Utilisateur caissier) {
        this.caissier = caissier;
    }
    
    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }
    
    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }
    
    public LocalDateTime getDateFermeture() {
        return dateFermeture;
    }
    
    public void setDateFermeture(LocalDateTime dateFermeture) {
        this.dateFermeture = dateFermeture;
    }
    
    public Double getSoldeDepartEspeces() {
        return soldeDepartEspeces;
    }
    
    public void setSoldeDepartEspeces(Double soldeDepartEspeces) {
        this.soldeDepartEspeces = soldeDepartEspeces;
    }
    
    public Double getSoldeFinEspeces() {
        return soldeFinEspeces;
    }
    
    public void setSoldeFinEspeces(Double soldeFinEspeces) {
        this.soldeFinEspeces = soldeFinEspeces;
    }
    
    public Double getTotalVentesEspeces() {
        return totalVentesEspeces;
    }
    
    public void setTotalVentesEspeces(Double totalVentesEspeces) {
        this.totalVentesEspeces = totalVentesEspeces;
    }
    
    public Double getTotalVentesCarte() {
        return totalVentesCarte;
    }
    
    public void setTotalVentesCarte(Double totalVentesCarte) {
        this.totalVentesCarte = totalVentesCarte;
    }
    
    public Double getTotalVentesAutre() {
        return totalVentesAutre;
    }
    
    public void setTotalVentesAutre(Double totalVentesAutre) {
        this.totalVentesAutre = totalVentesAutre;
    }
    
    public Double getTotalDepenses() {
        return totalDepenses;
    }
    
    public void setTotalDepenses(Double totalDepenses) {
        this.totalDepenses = totalDepenses;
    }
    
    public Double getEcart() {
        return ecart;
    }
    
    public void setEcart(Double ecart) {
        this.ecart = ecart;
    }
    
    public StatutCaisse getStatut() {
        return statut;
    }
    
    public void setStatut(StatutCaisse statut) {
        this.statut = statut;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    @Override
    public String toString() {
        return numeroCaisse + " - " + caissier.getNomComplet();
    }
}

package org.example.model.pattern.strategy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CheckPayment implements PaymentStrategy {
    
    private String numeroCheck;
    private String nomBanque;
    private String nomEmetteur;
    private LocalDate dateEmission;
    private double montantPaye;
    private boolean valide;
    
    public CheckPayment(String numeroCheck, String nomBanque) {
        this.numeroCheck = numeroCheck;
        this.nomBanque = nomBanque;
        this.dateEmission = LocalDate.now();
        this.valide = false;
    }
    
    public CheckPayment(String numeroCheck, String nomBanque, String nomEmetteur, LocalDate dateEmission) {
        this.numeroCheck = numeroCheck;
        this.nomBanque = nomBanque;
        this.nomEmetteur = nomEmetteur;
        this.dateEmission = dateEmission;
        this.valide = false;
    }
    
    @Override
    public boolean effectuerPaiement(double montant) {
        if (!valider()) {
            System.out.println("Échec: Données du chèque invalides");
            return false;
        }
        
        LocalDate now = LocalDate.now();
        if (dateEmission.isAfter(now.plusDays(30))) {
            System.out.println("Échec: Chèque post-daté de plus de 30 jours");
            return false;
        }
        if (dateEmission.isBefore(now.minusDays(180))) {
            System.out.println("Échec: Chèque périmé (plus de 6 mois)");
            return false;
        }
        
        System.out.println("Traitement du paiement par chèque...");
        System.out.println("Numéro: " + numeroCheck);
        System.out.println("Banque: " + nomBanque);
        System.out.println("Montant: " + montant + " DZD");
        
        this.montantPaye = montant;
        this.valide = true;
        
        System.out.println("Chèque accepté - À encaisser");
        return true;
    }
    
    @Override
    public String getNomMethode() {
        return "Chèque";
    }
    
    @Override
    public boolean valider() {
        
        if (numeroCheck == null || numeroCheck.trim().isEmpty()) {
            return false;
        }
        if (numeroCheck.length() < 5) {
            return false;
        }
        
        if (nomBanque == null || nomBanque.trim().isEmpty()) {
            return false;
        }
        
        if (dateEmission == null) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getDetailsPaiement() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format(
            "Méthode: Chèque\n" +
            "Numéro: %s\n" +
            "Banque: %s\n" +
            "Émetteur: %s\n" +
            "Date: %s\n" +
            "Montant: %.2f DZD",
            numeroCheck,
            nomBanque,
            nomEmetteur != null ? nomEmetteur : "Non spécifié",
            dateEmission.format(formatter),
            montantPaye
        );
    }
    
    public String getNumeroCheck() {
        return numeroCheck;
    }
    
    public String getNomBanque() {
        return nomBanque;
    }
    
    public String getNomEmetteur() {
        return nomEmetteur;
    }
    
    public LocalDate getDateEmission() {
        return dateEmission;
    }
    
    public double getMontantPaye() {
        return montantPaye;
    }
    
    public boolean isValide() {
        return valide;
    }
    
    public void setNomEmetteur(String nomEmetteur) {
        this.nomEmetteur = nomEmetteur;
    }
    
    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }
}

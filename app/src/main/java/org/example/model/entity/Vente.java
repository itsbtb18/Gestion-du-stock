package org.example.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vente {
    
    private Long id;
    private String numero;
    private LocalDateTime dateVente;
    private Client client;
    private Utilisateur vendeur;
    private Long vendeurId; 
    private List<LigneVente> lignes;
    private double montantTotal;
    private double montantRemise;
    private double montantTVA;
    private double montantFinal;
    private String modePaiement; 
    private String statut; 
    private String commentaire;
    
    public Vente() {
        this.numero = genererNumero();
        this.dateVente = LocalDateTime.now();
        this.lignes = new ArrayList<>();
        this.montantTotal = 0.0;
        this.montantRemise = 0.0;
        this.montantTVA = 0.0;
        this.montantFinal = 0.0;
        this.statut = "EN_COURS";
    }
    
    public Vente(Client client, Utilisateur vendeur) {
        this();
        this.client = client;
        this.vendeur = vendeur;
    }
    
    private String genererNumero() {
        return "V" + System.currentTimeMillis();
    }
    
    public void ajouterLigne(LigneVente ligne) {
        this.lignes.add(ligne);
        recalculerMontants();
    }
    
    public void supprimerLigne(LigneVente ligne) {
        this.lignes.remove(ligne);
        recalculerMontants();
    }
    
    public void recalculerMontants() {
        this.montantTotal = 0.0;
        for (LigneVente ligne : lignes) {
            this.montantTotal += ligne.getSousTotal();
        }
        
        if (client != null) {
            double tauxRemise = client.getRemiseDisponible();
            this.montantRemise = montantTotal * tauxRemise;
        }
        
        double montantAvantTVA = montantTotal - montantRemise;
        this.montantTVA = montantAvantTVA * 0.20;
        
        this.montantFinal = montantAvantTVA + montantTVA;
    }
    
    public void valider() {
        this.statut = "VALIDEE";
        if (client != null) {
            client.enregistrerAchat(montantFinal);
        }
    }
    
    public void annuler() {
        this.statut = "ANNULEE";
    }
    
    public int getNombreArticles() {
        return lignes.stream().mapToInt(LigneVente::getQuantite).sum();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public LocalDateTime getDateVente() {
        return dateVente;
    }
    
    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public Utilisateur getVendeur() {
        return vendeur;
    }
    
    public void setVendeur(Utilisateur vendeur) {
        this.vendeur = vendeur;
    }
    
    public Long getVendeurId() {
        return vendeurId;
    }
    
    public void setVendeurId(Long vendeurId) {
        this.vendeurId = vendeurId;
    }
    
    public List<LigneVente> getLignes() {
        return lignes;
    }
    
    public void setLignes(List<LigneVente> lignes) {
        this.lignes = lignes;
    }
    
    public double getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public double getMontantRemise() {
        return montantRemise;
    }
    
    public void setMontantRemise(double montantRemise) {
        this.montantRemise = montantRemise;
    }
    
    public double getMontantTVA() {
        return montantTVA;
    }
    
    public void setMontantTVA(double montantTVA) {
        this.montantTVA = montantTVA;
    }
    
    public double getMontantFinal() {
        return montantFinal;
    }
    
    public void setMontantFinal(double montantFinal) {
        this.montantFinal = montantFinal;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public double getMontantTva() {
        return montantTVA;
    }
    
    public String getNumeroTicket() {
        return numero;
    }
    
    public static class Builder {
        private final Vente vente;
        
        public Builder() {
            this.vente = new Vente();
        }
        
        public Builder withNumero(String numero) {
            vente.numero = numero;
            return this;
        }
        
        public Builder withDateVente(LocalDateTime dateVente) {
            vente.dateVente = dateVente;
            return this;
        }
        
        public Builder withClient(Client client) {
            vente.client = client;
            return this;
        }
        
        public Builder withVendeur(Utilisateur vendeur) {
            vente.vendeur = vendeur;
            vente.vendeurId = vendeur != null ? vendeur.getId() : null;
            return this;
        }
        
        public Builder withVendeurId(Long vendeurId) {
            vente.vendeurId = vendeurId;
            return this;
        }
        
        public Builder withLignes(List<LigneVente> lignes) {
            vente.lignes = new ArrayList<>(lignes);
            vente.recalculerMontants();
            return this;
        }
        
        public Builder addLigne(LigneVente ligne) {
            vente.lignes.add(ligne);
            return this;
        }
        
        public Builder withModePaiement(String modePaiement) {
            vente.modePaiement = modePaiement;
            return this;
        }
        
        public Builder withStatut(String statut) {
            vente.statut = statut;
            return this;
        }
        
        public Builder withCommentaire(String commentaire) {
            vente.commentaire = commentaire;
            return this;
        }
        
        public Builder withMontantRemise(double montantRemise) {
            vente.montantRemise = montantRemise;
            return this;
        }
        
        public Builder withMontantTVA(double montantTVA) {
            vente.montantTVA = montantTVA;
            return this;
        }
        
        public Vente build() {
            if (!vente.lignes.isEmpty()) {
                vente.recalculerMontants();
            }
            return vente;
        }
        
        public Vente buildFromDB() {
            return vente;
        }
    }
}

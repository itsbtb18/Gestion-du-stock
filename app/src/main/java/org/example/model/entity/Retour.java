package org.example.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Retour {
    
    private Long id;
    private String numeroRetour;
    private LocalDateTime dateRetour;
    private Vente venteOriginale; 
    private Client client;
    private Utilisateur traiteParUser;
    private String motif; 
    private TypeRetour typeRetour; 
    private StatutRetour statut; 
    private Double montantTotal;
    private Double montantRembourse;
    private String modePaiement; 
    private String numeroCreditNote; 
    private List<LigneRetour> lignes;
    private String commentaire;
    
    public Retour() {
        this.dateRetour = LocalDateTime.now();
        this.lignes = new ArrayList<>();
        this.statut = StatutRetour.EN_COURS;
    }
    
    public Retour(String numeroRetour, Vente venteOriginale, Client client, String motif) {
        this();
        this.numeroRetour = numeroRetour;
        this.venteOriginale = venteOriginale;
        this.client = client;
        this.motif = motif;
    }
    
    public void calculerMontantTotal() {
        this.montantTotal = lignes.stream()
            .mapToDouble(LigneRetour::getMontantLigne)
            .sum();
    }
    
    public void ajouterLigne(LigneRetour ligne) {
        this.lignes.add(ligne);
        calculerMontantTotal();
    }
    
    public boolean isRetourComplet() {
        return typeRetour == TypeRetour.TOTAL;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroRetour() {
        return numeroRetour;
    }
    
    public void setNumeroRetour(String numeroRetour) {
        this.numeroRetour = numeroRetour;
    }
    
    public LocalDateTime getDateRetour() {
        return dateRetour;
    }
    
    public void setDateRetour(LocalDateTime dateRetour) {
        this.dateRetour = dateRetour;
    }
    
    public Vente getVenteOriginale() {
        return venteOriginale;
    }
    
    public void setVenteOriginale(Vente venteOriginale) {
        this.venteOriginale = venteOriginale;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public Utilisateur getTraiteParUser() {
        return traiteParUser;
    }
    
    public void setTraiteParUser(Utilisateur traiteParUser) {
        this.traiteParUser = traiteParUser;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public TypeRetour getTypeRetour() {
        return typeRetour;
    }
    
    public void setTypeRetour(TypeRetour typeRetour) {
        this.typeRetour = typeRetour;
    }
    
    public StatutRetour getStatut() {
        return statut;
    }
    
    public void setStatut(StatutRetour statut) {
        this.statut = statut;
    }
    
    public Double getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public Double getMontantRembourse() {
        return montantRembourse;
    }
    
    public void setMontantRembourse(Double montantRembourse) {
        this.montantRembourse = montantRembourse;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public String getNumeroCreditNote() {
        return numeroCreditNote;
    }
    
    public void setNumeroCreditNote(String numeroCreditNote) {
        this.numeroCreditNote = numeroCreditNote;
    }
    
    public List<LigneRetour> getLignes() {
        return lignes;
    }
    
    public void setLignes(List<LigneRetour> lignes) {
        this.lignes = lignes;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public Vente getVente() {
        return venteOriginale;
    }
    
    public String getNotes() {
        return commentaire;
    }
    
    public boolean isRemboursementEffectue() {
        return montantRembourse != null && montantRembourse > 0;
    }
    
    @Override
    public String toString() {
        return numeroRetour + " - " + dateRetour;
    }
    
    public static class Builder {
        private final Retour retour;
        
        public Builder() {
            this.retour = new Retour();
        }
        
        public Builder withNumeroRetour(String numeroRetour) {
            retour.numeroRetour = numeroRetour;
            return this;
        }
        
        public Builder withDateRetour(LocalDateTime dateRetour) {
            retour.dateRetour = dateRetour;
            return this;
        }
        
        public Builder withVenteOriginale(Vente venteOriginale) {
            retour.venteOriginale = venteOriginale;
            return this;
        }
        
        public Builder withClient(Client client) {
            retour.client = client;
            return this;
        }
        
        public Builder withTraiteParUser(Utilisateur user) {
            retour.traiteParUser = user;
            return this;
        }
        
        public Builder withMotif(String motif) {
            retour.motif = motif;
            return this;
        }
        
        public Builder withTypeRetour(TypeRetour typeRetour) {
            retour.typeRetour = typeRetour;
            return this;
        }
        
        public Builder withStatut(StatutRetour statut) {
            retour.statut = statut;
            return this;
        }
        
        public Builder withLignes(List<LigneRetour> lignes) {
            retour.lignes = new ArrayList<>(lignes);
            retour.calculerMontantTotal();
            return this;
        }
        
        public Builder addLigne(LigneRetour ligne) {
            retour.lignes.add(ligne);
            return this;
        }
        
        public Builder withMontantRembourse(Double montantRembourse) {
            retour.montantRembourse = montantRembourse;
            return this;
        }
        
        public Builder withModePaiement(String modePaiement) {
            retour.modePaiement = modePaiement;
            return this;
        }
        
        public Builder withNumeroCreditNote(String numeroCreditNote) {
            retour.numeroCreditNote = numeroCreditNote;
            return this;
        }
        
        public Builder withCommentaire(String commentaire) {
            retour.commentaire = commentaire;
            return this;
        }
        
        public Retour build() {
            if (!retour.lignes.isEmpty()) {
                retour.calculerMontantTotal();
            }
            return retour;
        }
        
        public Retour buildFromDB() {
            return retour;
        }
    }
}

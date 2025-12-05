package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * AvisClient - Customer feedback/rating entity
 */
public class AvisClient {
    
    private Long id;
    private Vente vente;
    private Client client;
    private int note; // Rating 1-5
    private String commentaire;
    private CategorieAvis categorie; // SERVICE, PRODUIT, PRIX, PROPRETE
    private LocalDateTime dateAvis;
    private boolean traite;
    private String reponse;
    private Utilisateur reponsePar;
    
    public AvisClient() {
        this.dateAvis = LocalDateTime.now();
        this.traite = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Vente getVente() {
        return vente;
    }
    
    public void setVente(Vente vente) {
        this.vente = vente;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public int getNote() {
        return note;
    }
    
    public void setNote(int note) {
        this.note = note;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public CategorieAvis getCategorie() {
        return categorie;
    }
    
    public void setCategorie(CategorieAvis categorie) {
        this.categorie = categorie;
    }
    
    public LocalDateTime getDateAvis() {
        return dateAvis;
    }

    public void setDateAvis(LocalDateTime dateAvis) {
        this.dateAvis = dateAvis;
    }
    
    public boolean isTraite() {
        return traite;
    }
    
    public void setTraite(boolean traite) {
        this.traite = traite;
    }
    
    public String getReponse() {
        return reponse;
    }
    
    public void setReponse(String reponse) {
        this.reponse = reponse;
    }
    
    public Utilisateur getReponsePar() {
        return reponsePar;
    }
    
    public void setReponsePar(Utilisateur reponsePar) {
        this.reponsePar = reponsePar;
    }
}

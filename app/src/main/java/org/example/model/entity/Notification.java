package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * Notification - System notification entity
 */
public class Notification {
    
    private Long id;
    private String titre;
    private String message;
    private TypeNotification type; // INFO, WARNING, ERROR, SUCCESS
    private PrioriteNotification priorite; // BASSE, NORMALE, HAUTE, URGENTE
    private LocalDateTime dateCreation;
    private LocalDateTime dateEnvoi;
    private Utilisateur destinataire; // Null for broadcast
    private boolean lu;
    private LocalDateTime dateLecture;
    private String lienAction; // URL or action identifier
    private boolean envoiEmail;
    private boolean envoiSMS;
    private String entiteRelated; // Related entity type
    private Long entiteId; // Related entity ID
    
    public Notification() {
        this.dateCreation = LocalDateTime.now();
        this.lu = false;
        this.envoiEmail = false;
        this.envoiSMS = false;
        this.priorite = PrioriteNotification.NORMALE;
    }
    
    public Notification(String titre, String message, TypeNotification type) {
        this();
        this.titre = titre;
        this.message = message;
        this.type = type;
    }
    
    public void marquerCommeLu() {
        this.lu = true;
        this.dateLecture = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public TypeNotification getType() {
        return type;
    }
    
    public void setType(TypeNotification type) {
        this.type = type;
    }
    
    public PrioriteNotification getPriorite() {
        return priorite;
    }
    
    public void setPriorite(PrioriteNotification priorite) {
        this.priorite = priorite;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }
    
    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
    
    public Utilisateur getDestinataire() {
        return destinataire;
    }
    
    public void setDestinataire(Utilisateur destinataire) {
        this.destinataire = destinataire;
    }
    
    public boolean isLu() {
        return lu;
    }
    
    public void setLu(boolean lu) {
        this.lu = lu;
    }
    
    public LocalDateTime getDateLecture() {
        return dateLecture;
    }
    
    public void setDateLecture(LocalDateTime dateLecture) {
        this.dateLecture = dateLecture;
    }
    
    public String getLienAction() {
        return lienAction;
    }
    
    public void setLienAction(String lienAction) {
        this.lienAction = lienAction;
    }
    
    public boolean isEnvoiEmail() {
        return envoiEmail;
    }
    
    public void setEnvoiEmail(boolean envoiEmail) {
        this.envoiEmail = envoiEmail;
    }
    
    public boolean isEnvoiSMS() {
        return envoiSMS;
    }
    
    public void setEnvoiSMS(boolean envoiSMS) {
        this.envoiSMS = envoiSMS;
    }
    
    public String getEntiteRelated() {
        return entiteRelated;
    }
    
    public void setEntiteRelated(String entiteRelated) {
        this.entiteRelated = entiteRelated;
    }
    
    public Long getEntiteId() {
        return entiteId;
    }
    
    public void setEntiteId(Long entiteId) {
        this.entiteId = entiteId;
    }
}

package org.example.model.service;

import org.example.dao.BonCommandeDAO;
import org.example.model.entity.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BonCommandeService {
    
    private static BonCommandeService instance;
    private final BonCommandeDAO bonCommandeDAO;
    
    private BonCommandeService() {
        this.bonCommandeDAO = new BonCommandeDAO();
    }
    
    public static synchronized BonCommandeService getInstance() {
        if (instance == null) {
            instance = new BonCommandeService();
        }
        return instance;
    }
    
    public BonCommande createCommande(BonCommande commande) throws SQLException {
        
        if (commande.getFournisseur() == null) {
            throw new IllegalArgumentException("Le fournisseur est requis");
        }
        
        if (commande.getLignes() == null || commande.getLignes().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins une ligne");
        }
        
        if (commande.getNumero() == null || commande.getNumero().isEmpty()) {
            commande.setNumero(generateNumeroCommande());
        }
        
        commande.calculerMontantTotal();
        
        if (commande.getStatut() == null) {
            commande.setStatut(StatutCommande.BROUILLON);
        }
        
        return bonCommandeDAO.save(commande);
    }
    
    public BonCommande updateCommande(BonCommande commande) throws SQLException {
        if (commande.getId() == null) {
            throw new IllegalArgumentException("ID de la commande requis pour la mise à jour");
        }
        
        commande.calculerMontantTotal();
        
        bonCommandeDAO.update(commande);
        return commande;
    }
    
    public boolean deleteCommande(Long id) throws SQLException {
        Optional<BonCommande> commande = bonCommandeDAO.findById(id);
        if (commande.isEmpty()) {
            throw new IllegalArgumentException("Commande introuvable");
        }
        
        if (commande.get().getStatut() != StatutCommande.BROUILLON) {
            throw new IllegalArgumentException("Seules les commandes en brouillon peuvent être supprimées");
        }
        
        bonCommandeDAO.delete(id);
        return true;
    }
    
    public boolean validateCommande(Long id) throws SQLException {
        Optional<BonCommande> commandeOpt = bonCommandeDAO.findById(id);
        if (commandeOpt.isEmpty()) {
            throw new IllegalArgumentException("Commande introuvable");
        }
        
        BonCommande commande = commandeOpt.get();
        
        if (commande.getStatut() != StatutCommande.BROUILLON) {
            throw new IllegalArgumentException("Seules les commandes en brouillon peuvent être validées");
        }
        
        commande.setStatut(StatutCommande.VALIDEE);
        bonCommandeDAO.update(commande);
        return true;
    }
    
    public boolean approveCommande(Long id) throws SQLException {
        Optional<BonCommande> commandeOpt = bonCommandeDAO.findById(id);
        if (commandeOpt.isEmpty()) {
            throw new IllegalArgumentException("Commande introuvable");
        }
        
        BonCommande commande = commandeOpt.get();
        
        if (commande.getStatut() != StatutCommande.VALIDEE) {
            throw new IllegalArgumentException("Seules les commandes validées peuvent être approuvées");
        }
        
        commande.setStatut(StatutCommande.ENVOYEE);
        bonCommandeDAO.update(commande);
        return true;
    }
    
    public boolean receiveCommande(Long id) throws SQLException {
        Optional<BonCommande> commandeOpt = bonCommandeDAO.findById(id);
        if (commandeOpt.isEmpty()) {
            throw new IllegalArgumentException("Commande introuvable");
        }
        
        BonCommande commande = commandeOpt.get();
        
        if (commande.getStatut() != StatutCommande.ENVOYEE && 
            commande.getStatut() != StatutCommande.PARTIELLE) {
            throw new IllegalArgumentException("Seules les commandes envoyées peuvent être reçues");
        }
        
        boolean allReceived = commande.getLignes().stream()
            .allMatch(LigneBonCommande::estCompletementRecu);
        
        if (allReceived) {
            commande.setStatut(StatutCommande.RECUE);
        } else {
            commande.setStatut(StatutCommande.PARTIELLE);
        }
        
        bonCommandeDAO.update(commande);
        return true;
    }
    
    public boolean cancelCommande(Long id) throws SQLException {
        Optional<BonCommande> commandeOpt = bonCommandeDAO.findById(id);
        if (commandeOpt.isEmpty()) {
            throw new IllegalArgumentException("Commande introuvable");
        }
        
        BonCommande commande = commandeOpt.get();
        
        if (commande.getStatut() == StatutCommande.RECUE) {
            throw new IllegalArgumentException("Une commande reçue ne peut pas être annulée");
        }
        
        commande.setStatut(StatutCommande.ANNULEE);
        bonCommandeDAO.update(commande);
        return true;
    }
    
    public List<BonCommande> getAllCommandes() throws SQLException {
        return bonCommandeDAO.findAll();
    }
    
    public Optional<BonCommande> getCommandeById(Long id) throws SQLException {
        return bonCommandeDAO.findById(id);
    }
    
    public Optional<BonCommande> getCommandeByNumero(String numero) throws SQLException {
        return bonCommandeDAO.findByNumero(numero);
    }
    
    public List<BonCommande> getCommandesByFournisseur(Long fournisseurId) throws SQLException {
        return bonCommandeDAO.findByFournisseur(fournisseurId);
    }
    
    public List<BonCommande> getCommandesByStatut(StatutCommande statut) throws SQLException {
        return bonCommandeDAO.findByStatut(statut);
    }
    
    public List<BonCommande> getPendingCommandes() throws SQLException {
        return bonCommandeDAO.findByStatut(StatutCommande.VALIDEE);
    }
    
    private String generateNumeroCommande() {
        return "BC-" + System.currentTimeMillis();
    }
}

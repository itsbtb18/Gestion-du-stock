package org.example.model.service;

import org.example.dao.BonCommandeDAO;
import org.example.model.entity.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * BonCommandeService - Business logic for purchase order management
 */
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
    
    /**
     * Create a new purchase order
     */
    public BonCommande createCommande(BonCommande commande) throws SQLException {
        // Validate
        if (commande.getFournisseur() == null) {
            throw new IllegalArgumentException("Le fournisseur est requis");
        }
        
        if (commande.getLignes() == null || commande.getLignes().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins une ligne");
        }
        
        // Generate numero if not set
        if (commande.getNumero() == null || commande.getNumero().isEmpty()) {
            commande.setNumero(generateNumeroCommande());
        }
        
        // Calculate total
        commande.calculerMontantTotal();
        
        // Set initial status if not set
        if (commande.getStatut() == null) {
            commande.setStatut(StatutCommande.BROUILLON);
        }
        
        return bonCommandeDAO.save(commande);
    }
    
    /**
     * Update existing purchase order
     */
    public BonCommande updateCommande(BonCommande commande) throws SQLException {
        if (commande.getId() == null) {
            throw new IllegalArgumentException("ID de la commande requis pour la mise à jour");
        }
        
        // Recalculate total
        commande.calculerMontantTotal();
        
        bonCommandeDAO.update(commande);
        return commande;
    }
    
    /**
     * Delete purchase order
     */
    public boolean deleteCommande(Long id) throws SQLException {
        Optional<BonCommande> commande = bonCommandeDAO.findById(id);
        if (commande.isEmpty()) {
            throw new IllegalArgumentException("Commande introuvable");
        }
        
        // Can only delete draft orders
        if (commande.get().getStatut() != StatutCommande.BROUILLON) {
            throw new IllegalArgumentException("Seules les commandes en brouillon peuvent être supprimées");
        }
        
        bonCommandeDAO.delete(id);
        return true;
    }
    
    /**
     * Validate purchase order
     */
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
    
    /**
     * Approve purchase order
     */
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
    
    /**
     * Receive purchase order (mark as received)
     */
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
        
        // Check if all lines are fully received
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
    
    /**
     * Cancel purchase order
     */
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
    
    /**
     * Get all purchase orders
     */
    public List<BonCommande> getAllCommandes() throws SQLException {
        return bonCommandeDAO.findAll();
    }
    
    /**
     * Get purchase order by ID
     */
    public Optional<BonCommande> getCommandeById(Long id) throws SQLException {
        return bonCommandeDAO.findById(id);
    }
    
    /**
     * Get purchase order by numero
     */
    public Optional<BonCommande> getCommandeByNumero(String numero) throws SQLException {
        return bonCommandeDAO.findByNumero(numero);
    }
    
    /**
     * Get purchase orders by supplier
     */
    public List<BonCommande> getCommandesByFournisseur(Long fournisseurId) throws SQLException {
        return bonCommandeDAO.findByFournisseur(fournisseurId);
    }
    
    /**
     * Get purchase orders by status
     */
    public List<BonCommande> getCommandesByStatut(StatutCommande statut) throws SQLException {
        return bonCommandeDAO.findByStatut(statut);
    }
    
    /**
     * Get pending purchase orders
     */
    public List<BonCommande> getPendingCommandes() throws SQLException {
        return bonCommandeDAO.findByStatut(StatutCommande.VALIDEE);
    }
    
    /**
     * Generate unique purchase order number
     */
    private String generateNumeroCommande() {
        return "BC-" + System.currentTimeMillis();
    }
}

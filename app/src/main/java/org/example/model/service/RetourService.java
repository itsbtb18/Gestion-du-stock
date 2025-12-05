package org.example.model.service;

import org.example.dao.RetourDAO;
import org.example.dao.VenteDAO;
import org.example.dao.ProduitDAO;
import org.example.model.entity.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RetourService - Business logic for product return management
 * Handles return processing, refunds, and credit notes
 */
public class RetourService {
    
    private static RetourService instance;
    private final RetourDAO retourDAO;
    private final VenteDAO venteDAO;
    private final ProduitDAO produitDAO;
    
    private RetourService() {
        this.retourDAO = new RetourDAO();
        this.venteDAO = new VenteDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    public static synchronized RetourService getInstance() {
        if (instance == null) {
            instance = new RetourService();
        }
        return instance;
    }
    
    /**
     * Create a new return from a sale
     */
    public Retour createRetour(Retour retour) throws SQLException {
        // Validate return
        if (retour.getVenteOriginale() == null) {
            throw new IllegalArgumentException("La vente originale est requise");
        }
        
        if (retour.getLignes() == null || retour.getLignes().isEmpty()) {
            throw new IllegalArgumentException("Le retour doit contenir au moins un article");
        }
        
        // Verify original sale exists
        Optional<Vente> venteOpt = venteDAO.findById(retour.getVenteOriginale().getId());
        if (venteOpt.isEmpty()) {
            throw new IllegalArgumentException("Vente originale introuvable");
        }
        
        // Generate return number
        if (retour.getNumeroRetour() == null || retour.getNumeroRetour().isEmpty()) {
            retour.setNumeroRetour(generateRetourNumero());
        }
        
        // Set initial status
        if (retour.getStatut() == null) {
            retour.setStatut(StatutRetour.EN_COURS);
        }
        
        // Set date if not provided
        if (retour.getDateRetour() == null) {
            retour.setDateRetour(LocalDateTime.now());
        }
        
        // Calculate total
        double total = retour.getLignes().stream()
            .mapToDouble(ligne -> ligne.getPrixUnitaire() * ligne.getQuantiteRetournee())
            .sum();
        retour.setMontantTotal(total);
        
        // Initialize refund amount
        if (retour.getMontantRembourse() == null) {
            retour.setMontantRembourse(0.0);
        }
        
        return retourDAO.save(retour);
    }
    
    /**
     * Approve a return and process refund
     */
    public boolean approveRetour(Long retourId, Long userId) throws SQLException {
        Optional<Retour> retourOpt = retourDAO.findById(retourId);
        if (retourOpt.isEmpty()) {
            throw new IllegalArgumentException("Retour introuvable");
        }
        
        Retour retour = retourOpt.get();
        
        if (retour.getStatut() != StatutRetour.EN_COURS) {
            throw new IllegalArgumentException("Seuls les retours en cours peuvent être approuvés");
        }
        
        // Update status to approved
        retourDAO.updateStatut(retourId, StatutRetour.APPROUVE);
        
        // Process refund
        processRefund(retour);
        
        // Mark as complete
        retourDAO.updateStatut(retourId, StatutRetour.COMPLETE);
        
        return true;
    }
    
    /**
     * Refuse a return
     */
    public boolean refuseRetour(Long retourId, String reason) throws SQLException {
        Optional<Retour> retourOpt = retourDAO.findById(retourId);
        if (retourOpt.isEmpty()) {
            throw new IllegalArgumentException("Retour introuvable");
        }
        
        Retour retour = retourOpt.get();
        retour.setCommentaire(reason);
        retourDAO.update(retour);
        
        retourDAO.updateStatut(retourId, StatutRetour.REFUSE);
        return true;
    }
    
    /**
     * Process refund for approved return
     */
    private void processRefund(Retour retour) throws SQLException {
        double refundAmount = retour.getMontantTotal();
        
        // Generate credit note if needed
        if ("CREDIT_NOTE".equals(retour.getModePaiement())) {
            retour.setNumeroCreditNote(generateCreditNoteNumber());
        }
        
        retour.setMontantRembourse(refundAmount);
        retourDAO.update(retour);
        
        // Restore stock for non-damaged items
        for (LigneRetour ligne : retour.getLignes()) {
            if (!ligne.isProduitEndommage()) {
                restockProduct(ligne.getProduit().getId(), ligne.getQuantiteRetournee());
            }
        }
    }
    
    /**
     * Restore product stock
     */
    private void restockProduct(Long produitId, int quantite) throws SQLException {
        Optional<Produit> produitOpt = produitDAO.findById(produitId);
        if (produitOpt.isPresent()) {
            Produit produit = produitOpt.get();
            produit.setQuantiteStock(produit.getQuantiteStock() + quantite);
            produitDAO.update(produit);
        }
    }
    
    /**
     * Generate return number
     */
    private String generateRetourNumero() {
        return "RET-" + System.currentTimeMillis();
    }
    
    /**
     * Generate credit note number
     */
    private String generateCreditNoteNumber() {
        return "CN-" + System.currentTimeMillis();
    }
    
    /**
     * Search returns
     */
    public List<Retour> searchByClient(Long clientId) throws SQLException {
        return retourDAO.findByClient(clientId);
    }
    
    /**
     * Get returns by status
     */
    public List<Retour> getRetoursByStatut(StatutRetour statut) throws SQLException {
        return retourDAO.findByStatut(statut);
    }
    
    /**
     * Get all returns
     */
    public List<Retour> getAllRetours() throws SQLException {
        return retourDAO.findAll();
    }
    
    /**
     * Get return by ID
     */
    public Optional<Retour> getRetourById(Long id) throws SQLException {
        return retourDAO.findById(id);
    }
    
    /**
     * Get return by number
     */
    public Optional<Retour> getRetourByNumero(String numero) throws SQLException {
        return retourDAO.findByNumero(numero);
    }
}

package org.example.model.service;

import org.example.dao.EmplacementDAO;
import org.example.model.entity.Emplacement;
import org.example.model.entity.TypeEmplacement;
import org.example.model.entity.TransfertStock;
import org.example.model.entity.StatutTransfert;
import org.example.model.entity.Lot;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * EmplacementService - Business logic for warehouse location management
 * Handles multi-location inventory distribution
 */
public class EmplacementService {
    
    private static EmplacementService instance;
    private final EmplacementDAO emplacementDAO;
    
    private EmplacementService() {
        this.emplacementDAO = new EmplacementDAO();
    }
    
    public static synchronized EmplacementService getInstance() {
        if (instance == null) {
            instance = new EmplacementService();
        }
        return instance;
    }
    
    /**
     * Create a new location
     */
    public Emplacement createEmplacement(Emplacement emplacement) throws SQLException {
        // Validate
        if (emplacement.getCode() == null || emplacement.getCode().isEmpty()) {
            throw new IllegalArgumentException("Le code d'emplacement est requis");
        }
        
        if (emplacement.getNom() == null || emplacement.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'emplacement est requis");
        }
        
        if (emplacement.getType() == null) {
            throw new IllegalArgumentException("Le type d'emplacement est requis");
        }
        
        // Check for duplicates
        Optional<Emplacement> existing = emplacementDAO.findByCode(emplacement.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Un emplacement avec ce code existe déjà");
        }
        
        return emplacementDAO.save(emplacement);
    }
    
    /**
     * Update location
     */
    public boolean updateEmplacement(Emplacement emplacement) throws SQLException {
        if (emplacement.getId() == null) {
            throw new IllegalArgumentException("L'ID de l'emplacement est requis");
        }
        
        Optional<Emplacement> existing = emplacementDAO.findById(emplacement.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Emplacement introuvable");
        }
        
        emplacementDAO.update(emplacement);
        return true;
    }
    
    /**
     * Deactivate location
     */
    public boolean deactivateEmplacement(Long emplacementId) throws SQLException {
        Optional<Emplacement> emplacementOpt = emplacementDAO.findById(emplacementId);
        if (emplacementOpt.isEmpty()) {
            throw new IllegalArgumentException("Emplacement introuvable");
        }
        
        Emplacement emplacement = emplacementOpt.get();
        emplacement.setActif(false);
        
        emplacementDAO.update(emplacement);
        return true;
    }
    
    /**
     * Activate location
     */
    public boolean activateEmplacement(Long emplacementId) throws SQLException {
        Optional<Emplacement> emplacementOpt = emplacementDAO.findById(emplacementId);
        if (emplacementOpt.isEmpty()) {
            throw new IllegalArgumentException("Emplacement introuvable");
        }
        
        Emplacement emplacement = emplacementOpt.get();
        emplacement.setActif(true);
        
        emplacementDAO.update(emplacement);
        return true;
    }
    
    /**
     * Delete location
     */
    public boolean deleteEmplacement(Long emplacementId) throws SQLException {
        Optional<Emplacement> emplacementOpt = emplacementDAO.findById(emplacementId);
        if (emplacementOpt.isEmpty()) {
            throw new IllegalArgumentException("Emplacement introuvable");
        }
        
        Emplacement emplacement = emplacementOpt.get();
        
        // Check if location has active transfers
        TransfertService transfertService = TransfertService.getInstance();
        List<TransfertStock> transferts = transfertService.getTransfertsByEmplacement(emplacementId);
        
        long activeTransfers = transferts.stream()
            .filter(t -> t.getStatut() == StatutTransfert.EN_ATTENTE || 
                        t.getStatut() == StatutTransfert.APPROUVE || 
                        t.getStatut() == StatutTransfert.EN_TRANSIT)
            .count();
        
        if (activeTransfers > 0) {
            throw new IllegalArgumentException(
                String.format("Impossible de supprimer l'emplacement: %d transfert(s) actif(s) en cours", activeTransfers)
            );
        }
        
        // Check if location has lots
        LotService lotService = LotService.getInstance();
        List<Lot> lots = lotService.getLotsByEmplacement(emplacementId);
        
        long activeLots = lots.stream()
            .filter(lot -> lot.getQuantite() > 0)
            .count();
        
        if (activeLots > 0) {
            throw new IllegalArgumentException(
                String.format("Impossible de supprimer l'emplacement: %d lot(s) avec stock restant", activeLots)
            );
        }
        
        // TODO: For full multi-location support, also check location-specific stock tables
        // For now, we recommend deactivating instead of deleting
        
        emplacementDAO.delete(emplacementId);
        return true;
    }
    
    /**
     * Get locations by type
     */
    public List<Emplacement> getEmplacementsByType(TypeEmplacement type) throws SQLException {
        return emplacementDAO.findByType(type);
    }
    
    /**
     * Get active locations only
     */
    public List<Emplacement> getActiveEmplacements() throws SQLException {
        return emplacementDAO.findActive();
    }
    
    /**
     * Get all locations
     */
    public List<Emplacement> getAllEmplacements() throws SQLException {
        return emplacementDAO.findAll();
    }
    
    /**
     * Get location by ID
     */
    public Optional<Emplacement> getEmplacementById(Long id) throws SQLException {
        return emplacementDAO.findById(id);
    }
    
    /**
     * Get location by code
     */
    public Optional<Emplacement> getEmplacementByCode(String code) throws SQLException {
        return emplacementDAO.findByCode(code);
    }
    
    /**
     * Get total location count
     */
    public int getTotalCount() throws SQLException {
        return emplacementDAO.count();
    }
    
    /**
     * Get stores (MAGASIN type)
     */
    public List<Emplacement> getStores() throws SQLException {
        return emplacementDAO.findByType(TypeEmplacement.MAGASIN);
    }
    
    /**
     * Get warehouses (ENTREPOT type)
     */
    public List<Emplacement> getWarehouses() throws SQLException {
        return emplacementDAO.findByType(TypeEmplacement.ENTREPOT);
    }
    
    /**
     * Get reserves (RESERVE type)
     */
    public List<Emplacement> getReserves() throws SQLException {
        return emplacementDAO.findByType(TypeEmplacement.RESERVE);
    }
    
    /**
     * Get showrooms (SHOWROOM type)
     */
    public List<Emplacement> getShowrooms() throws SQLException {
        return emplacementDAO.findByType(TypeEmplacement.SHOWROOM);
    }
}

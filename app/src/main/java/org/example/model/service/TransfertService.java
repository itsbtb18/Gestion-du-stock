package org.example.model.service;

import org.example.dao.TransfertStockDAO;
import org.example.dao.EmplacementDAO;
import org.example.dao.ProduitDAO;
import org.example.model.entity.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * TransfertService - Business logic for inter-location stock transfers
 * Handles transfer workflow and inventory synchronization
 */
public class TransfertService {
    
    private static TransfertService instance;
    private final TransfertStockDAO transfertDAO;
    private final EmplacementDAO emplacementDAO;
    private final ProduitDAO produitDAO;
    
    private TransfertService() {
        this.transfertDAO = new TransfertStockDAO();
        this.emplacementDAO = new EmplacementDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    public static synchronized TransfertService getInstance() {
        if (instance == null) {
            instance = new TransfertService();
        }
        return instance;
    }
    
    /**
     * Create a new transfer request
     */
    public TransfertStock createTransfert(TransfertStock transfert) throws SQLException {
        // Validate
        if (transfert.getEmplacementSource() == null) {
            throw new IllegalArgumentException("L'emplacement source est requis");
        }
        
        if (transfert.getEmplacementDestination() == null) {
            throw new IllegalArgumentException("L'emplacement destination est requis");
        }
        
        if (transfert.getEmplacementSource().getId().equals(transfert.getEmplacementDestination().getId())) {
            throw new IllegalArgumentException("Les emplacements source et destination doivent être différents");
        }
        
        if (transfert.getProduit() == null) {
            throw new IllegalArgumentException("Le produit est requis");
        }
        
        if (transfert.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        
        // Verify locations exist and are active
        Optional<Emplacement> sourceOpt = emplacementDAO.findById(transfert.getEmplacementSource().getId());
        if (sourceOpt.isEmpty() || !sourceOpt.get().isActif()) {
            throw new IllegalArgumentException("L'emplacement source n'est pas valide");
        }
        
        Optional<Emplacement> destOpt = emplacementDAO.findById(transfert.getEmplacementDestination().getId());
        if (destOpt.isEmpty() || !destOpt.get().isActif()) {
            throw new IllegalArgumentException("L'emplacement destination n'est pas valide");
        }
        
        // Verify product exists
        Optional<Produit> produitOpt = produitDAO.findById(transfert.getProduit().getId());
        if (produitOpt.isEmpty()) {
            throw new IllegalArgumentException("Produit introuvable");
        }
        
        Produit produit = produitOpt.get();
        
        // Check if source has sufficient stock
        // Note: For full multi-location support, we would need location-specific stock tracking
        // For now, we check global product stock as a safeguard
        if (produit.getQuantiteStock() < transfert.getQuantite()) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant: disponible=%d, demandé=%d", 
                    produit.getQuantiteStock(), transfert.getQuantite())
            );
        }
        // TODO: Implement location-specific stock tracking when multi-emplacement stock is fully implemented
        
        // Generate transfer number
        if (transfert.getNumeroTransfert() == null || transfert.getNumeroTransfert().isEmpty()) {
            transfert.setNumeroTransfert(generateTransfertNumero());
        }
        
        // Set initial status
        if (transfert.getStatut() == null) {
            transfert.setStatut(StatutTransfert.EN_ATTENTE);
        }
        
        return transfertDAO.save(transfert);
    }
    
    /**
     * Approve transfer request
     */
    public boolean approveTransfert(Long transfertId, Long validatorId) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() != StatutTransfert.EN_ATTENTE) {
            throw new IllegalArgumentException("Seuls les transferts en attente peuvent être approuvés");
        }
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.APPROUVE, validatorId);
        return true;
    }
    
    /**
     * Refuse transfer request
     */
    public boolean refuseTransfert(Long transfertId, String motif, Long refusedById) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() != StatutTransfert.EN_ATTENTE) {
            throw new IllegalArgumentException("Seuls les transferts en attente peuvent être refusés");
        }
        
        transfert.setMotif(motif);
        transfertDAO.update(transfert);
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.REFUSE, refusedById);
        return true;
    }
    
    /**
     * Mark transfer as in transit
     */
    public boolean startTransfert(Long transfertId) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() != StatutTransfert.APPROUVE) {
            throw new IllegalArgumentException("Seuls les transferts approuvés peuvent être démarrés");
        }
        
        // Deduct stock from source location
        Produit produit = produitDAO.findById(transfert.getProduit().getId())
            .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
        
        int nouveauStock = produit.getQuantiteStock() - transfert.getQuantite();
        if (nouveauStock < 0) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant pour démarrer le transfert: disponible=%d, requis=%d",
                    produit.getQuantiteStock(), transfert.getQuantite())
            );
        }
        
        produit.setQuantiteStock(nouveauStock);
        produitDAO.update(produit);
        
        // Record stock movement
        StockService stockService = StockService.getInstance();
        stockService.removeStock(
            produit.getId(), 
            transfert.getQuantite(), 
            "Transfert " + transfert.getNumeroTransfert() + " vers " + transfert.getEmplacementDestination().getNom(),
            1L // TODO: Use actual current user ID
        );
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.EN_TRANSIT, null);
        return true;
    }
    
    /**
     * Complete transfer (mark as received)
     */
    public boolean completeTransfert(Long transfertId) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() != StatutTransfert.EN_TRANSIT) {
            throw new IllegalArgumentException("Seuls les transferts en transit peuvent être complétés");
        }
        
        // Add stock to destination location
        Produit produit = produitDAO.findById(transfert.getProduit().getId())
            .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
        
        int nouveauStock = produit.getQuantiteStock() + transfert.getQuantite();
        produit.setQuantiteStock(nouveauStock);
        produitDAO.update(produit);
        
        // Record stock movement
        StockService stockService = StockService.getInstance();
        stockService.addStock(
            produit.getId(),
            transfert.getQuantite(),
            "Réception transfert " + transfert.getNumeroTransfert() + " depuis " + transfert.getEmplacementSource().getNom(),
            1L // TODO: Use actual receiving user ID
        );
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.RECU, null);
        return true;
    }
    
    /**
     * Cancel transfer
     */
    public boolean cancelTransfert(Long transfertId) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() == StatutTransfert.RECU) {
            throw new IllegalArgumentException("Un transfert reçu ne peut pas être annulé");
        }
        
        // Restore stock to source if already deducted (i.e., if status is EN_TRANSIT)
        if (transfert.getStatut() == StatutTransfert.EN_TRANSIT) {
            Produit produit = produitDAO.findById(transfert.getProduit().getId())
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
            
            int nouveauStock = produit.getQuantiteStock() + transfert.getQuantite();
            produit.setQuantiteStock(nouveauStock);
            produitDAO.update(produit);
            
            // Record stock movement for restoration
            StockService stockService = StockService.getInstance();
            stockService.addStock(
                produit.getId(),
                transfert.getQuantite(),
                "Annulation transfert " + transfert.getNumeroTransfert() + " - stock restauré",
                null // TODO: Use actual cancelling user ID
            );
        }
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.ANNULE, null);
        return true;
    }
    
    /**
     * Get pending transfers
     */
    public List<TransfertStock> getPendingTransferts() throws SQLException {
        return transfertDAO.findEnAttente();
    }
    
    /**
     * Get transfers by status
     */
    public List<TransfertStock> getTransfertsByStatut(StatutTransfert statut) throws SQLException {
        return transfertDAO.findByStatut(statut);
    }
    
    /**
     * Get transfers from location
     */
    public List<TransfertStock> getTransfertsFromLocation(Long emplacementId) throws SQLException {
        return transfertDAO.findByEmplacementSource(emplacementId);
    }
    
    /**
     * Get transfers to location
     */
    public List<TransfertStock> getTransfertsToLocation(Long emplacementId) throws SQLException {
        return transfertDAO.findByEmplacementDestination(emplacementId);
    }
    
    /**
     * Get transfers for product
     */
    public List<TransfertStock> getTransfertsByProduit(Long produitId) throws SQLException {
        return transfertDAO.findByProduit(produitId);
    }
    
    /**
     * Get all transfers
     */
    public List<TransfertStock> getAllTransferts() throws SQLException {
        return transfertDAO.findAll();
    }
    
    /**
     * Get transfers by location (source or destination)
     */
    public List<TransfertStock> getTransfertsByEmplacement(Long emplacementId) throws SQLException {
        List<TransfertStock> allTransferts = transfertDAO.findAll();
        return allTransferts.stream()
            .filter(t -> 
                (t.getEmplacementSource() != null && t.getEmplacementSource().getId().equals(emplacementId)) ||
                (t.getEmplacementDestination() != null && t.getEmplacementDestination().getId().equals(emplacementId))
            )
            .toList();
    }
    
    /**
     * Get transfer by ID
     */
    public Optional<TransfertStock> getTransfertById(Long id) throws SQLException {
        return transfertDAO.findById(id);
    }
    
    /**
     * Get transfer by number
     */
    public Optional<TransfertStock> getTransfertByNumero(String numero) throws SQLException {
        return transfertDAO.findByNumero(numero);
    }
    
    /**
     * Generate transfer number
     */
    private String generateTransfertNumero() {
        return "TRF-" + System.currentTimeMillis();
    }
}

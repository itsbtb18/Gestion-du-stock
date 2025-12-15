package org.example.model.service;

import org.example.dao.TransfertStockDAO;
import org.example.dao.EmplacementDAO;
import org.example.dao.ProduitDAO;
import org.example.model.entity.*;
import org.example.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    
    public TransfertStock createTransfert(TransfertStock transfert) throws SQLException {
        
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
        
        Optional<Emplacement> sourceOpt = emplacementDAO.findById(transfert.getEmplacementSource().getId());
        if (sourceOpt.isEmpty() || !sourceOpt.get().isActif()) {
            throw new IllegalArgumentException("L'emplacement source n'est pas valide");
        }
        
        Optional<Emplacement> destOpt = emplacementDAO.findById(transfert.getEmplacementDestination().getId());
        if (destOpt.isEmpty() || !destOpt.get().isActif()) {
            throw new IllegalArgumentException("L'emplacement destination n'est pas valide");
        }
        
        Optional<Produit> produitOpt = produitDAO.findById(transfert.getProduit().getId());
        if (produitOpt.isEmpty()) {
            throw new IllegalArgumentException("Produit introuvable");
        }
        
        Produit produit = produitOpt.get();
        
        if (produit.getQuantiteStock() < transfert.getQuantite()) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant: disponible=%d, demandé=%d", 
                    produit.getQuantiteStock(), transfert.getQuantite())
            );
        }
        
        if (transfert.getNumeroTransfert() == null || transfert.getNumeroTransfert().isEmpty()) {
            transfert.setNumeroTransfert(generateTransfertNumero());
        }
        
        if (transfert.getStatut() == null) {
            transfert.setStatut(StatutTransfert.EN_ATTENTE);
        }
        
        return transfertDAO.save(transfert);
    }
    
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
    
    public boolean startTransfert(Long transfertId) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() != StatutTransfert.APPROUVE) {
            throw new IllegalArgumentException("Seuls les transferts approuvés peuvent être démarrés");
        }
        
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
        
        StockService stockService = StockService.getInstance();
        stockService.removeStock(
            produit.getId(), 
            transfert.getQuantite(), 
            "Transfert " + transfert.getNumeroTransfert() + " vers " + transfert.getEmplacementDestination().getNom(),
            getCurrentUserId()
        );
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.EN_TRANSIT, null);
        return true;
    }
    
    public boolean completeTransfert(Long transfertId) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() != StatutTransfert.EN_TRANSIT) {
            throw new IllegalArgumentException("Seuls les transferts en transit peuvent être complétés");
        }
        
        Produit produit = produitDAO.findById(transfert.getProduit().getId())
            .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
        
        int nouveauStock = produit.getQuantiteStock() + transfert.getQuantite();
        produit.setQuantiteStock(nouveauStock);
        produitDAO.update(produit);
        
        StockService stockService = StockService.getInstance();
        stockService.addStock(
            produit.getId(),
            transfert.getQuantite(),
            "Réception transfert " + transfert.getNumeroTransfert() + " depuis " + transfert.getEmplacementSource().getNom(),
            getCurrentUserId()
        );
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.RECU, null);
        return true;
    }
    
    public boolean cancelTransfert(Long transfertId) throws SQLException {
        Optional<TransfertStock> transfertOpt = transfertDAO.findById(transfertId);
        if (transfertOpt.isEmpty()) {
            throw new IllegalArgumentException("Transfert introuvable");
        }
        
        TransfertStock transfert = transfertOpt.get();
        
        if (transfert.getStatut() == StatutTransfert.RECU) {
            throw new IllegalArgumentException("Un transfert reçu ne peut pas être annulé");
        }
        
        if (transfert.getStatut() == StatutTransfert.EN_TRANSIT) {
            Produit produit = produitDAO.findById(transfert.getProduit().getId())
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
            
            int nouveauStock = produit.getQuantiteStock() + transfert.getQuantite();
            produit.setQuantiteStock(nouveauStock);
            produitDAO.update(produit);
            
            StockService stockService = StockService.getInstance();
            stockService.addStock(
                produit.getId(),
                transfert.getQuantite(),
                "Annulation transfert " + transfert.getNumeroTransfert() + " - stock restauré",
                getCurrentUserId()
            );
        }
        
        transfertDAO.updateStatut(transfertId, StatutTransfert.ANNULE, null);
        return true;
    }
    
    public List<TransfertStock> getPendingTransferts() throws SQLException {
        return transfertDAO.findEnAttente();
    }
    
    public List<TransfertStock> getTransfertsByStatut(StatutTransfert statut) throws SQLException {
        return transfertDAO.findByStatut(statut);
    }
    
    public List<TransfertStock> getTransfertsFromLocation(Long emplacementId) throws SQLException {
        return transfertDAO.findByEmplacementSource(emplacementId);
    }
    
    public List<TransfertStock> getTransfertsToLocation(Long emplacementId) throws SQLException {
        return transfertDAO.findByEmplacementDestination(emplacementId);
    }
    
    public List<TransfertStock> getTransfertsByProduit(Long produitId) throws SQLException {
        return transfertDAO.findByProduit(produitId);
    }
    
    public List<TransfertStock> getAllTransferts() throws SQLException {
        return transfertDAO.findAll();
    }
    
    public List<TransfertStock> getTransfertsByEmplacement(Long emplacementId) throws SQLException {
        List<TransfertStock> allTransferts = transfertDAO.findAll();
        return allTransferts.stream()
            .filter(t -> 
                (t.getEmplacementSource() != null && t.getEmplacementSource().getId().equals(emplacementId)) ||
                (t.getEmplacementDestination() != null && t.getEmplacementDestination().getId().equals(emplacementId))
            )
            .toList();
    }
    
    public Optional<TransfertStock> getTransfertById(Long id) throws SQLException {
        return transfertDAO.findById(id);
    }
    
    public Optional<TransfertStock> getTransfertByNumero(String numero) throws SQLException {
        return transfertDAO.findByNumero(numero);
    }
    
    private String generateTransfertNumero() {
        return "TRF-" + System.currentTimeMillis();
    }
    
    private Long getCurrentUserId() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn() && session.getCurrentUser() != null) {
            return session.getCurrentUser().getId();
        }
        return null;
    }
}

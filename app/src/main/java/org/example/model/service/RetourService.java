package org.example.model.service;

import org.example.dao.RetourDAO;
import org.example.dao.VenteDAO;
import org.example.dao.ProduitDAO;
import org.example.model.entity.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    
    public Retour createRetour(Retour retour) throws SQLException {
        
        if (retour.getVenteOriginale() == null) {
            throw new IllegalArgumentException("La vente originale est requise");
        }
        
        if (retour.getLignes() == null || retour.getLignes().isEmpty()) {
            throw new IllegalArgumentException("Le retour doit contenir au moins un article");
        }
        
        Optional<Vente> venteOpt = venteDAO.findById(retour.getVenteOriginale().getId());
        if (venteOpt.isEmpty()) {
            throw new IllegalArgumentException("Vente originale introuvable");
        }
        
        if (retour.getNumeroRetour() == null || retour.getNumeroRetour().isEmpty()) {
            retour.setNumeroRetour(generateRetourNumero());
        }
        
        if (retour.getStatut() == null) {
            retour.setStatut(StatutRetour.EN_COURS);
        }
        
        if (retour.getDateRetour() == null) {
            retour.setDateRetour(LocalDateTime.now());
        }
        
        double total = retour.getLignes().stream()
            .mapToDouble(ligne -> ligne.getPrixUnitaire() * ligne.getQuantiteRetournee())
            .sum();
        retour.setMontantTotal(total);
        
        if (retour.getMontantRembourse() == null) {
            retour.setMontantRembourse(0.0);
        }
        
        return retourDAO.save(retour);
    }
    
    public boolean approveRetour(Long retourId, Long userId) throws SQLException {
        Optional<Retour> retourOpt = retourDAO.findById(retourId);
        if (retourOpt.isEmpty()) {
            throw new IllegalArgumentException("Retour introuvable");
        }
        
        Retour retour = retourOpt.get();
        
        if (retour.getStatut() != StatutRetour.EN_COURS) {
            throw new IllegalArgumentException("Seuls les retours en cours peuvent être approuvés");
        }
        
        retourDAO.updateStatut(retourId, StatutRetour.APPROUVE);
        
        processRefund(retour);
        
        retourDAO.updateStatut(retourId, StatutRetour.COMPLETE);
        
        return true;
    }
    
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
    
    private void processRefund(Retour retour) throws SQLException {
        double refundAmount = retour.getMontantTotal();
        
        if ("CREDIT_NOTE".equals(retour.getModePaiement())) {
            retour.setNumeroCreditNote(generateCreditNoteNumber());
        }
        
        retour.setMontantRembourse(refundAmount);
        retourDAO.update(retour);
        
        for (LigneRetour ligne : retour.getLignes()) {
            if (!ligne.isProduitEndommage()) {
                restockProduct(ligne.getProduit().getId(), ligne.getQuantiteRetournee());
            }
        }
    }
    
    private void restockProduct(Long produitId, int quantite) throws SQLException {
        Optional<Produit> produitOpt = produitDAO.findById(produitId);
        if (produitOpt.isPresent()) {
            Produit produit = produitOpt.get();
            produit.setQuantiteStock(produit.getQuantiteStock() + quantite);
            produitDAO.update(produit);
        }
    }
    
    private String generateRetourNumero() {
        return "RET-" + System.currentTimeMillis();
    }
    
    private String generateCreditNoteNumber() {
        return "CN-" + System.currentTimeMillis();
    }
    
    public List<Retour> searchByClient(Long clientId) throws SQLException {
        return retourDAO.findByClient(clientId);
    }
    
    public List<Retour> getRetoursByStatut(StatutRetour statut) throws SQLException {
        return retourDAO.findByStatut(statut);
    }
    
    public List<Retour> getAllRetours() throws SQLException {
        return retourDAO.findAll();
    }
    
    public Optional<Retour> getRetourById(Long id) throws SQLException {
        return retourDAO.findById(id);
    }
    
    public Optional<Retour> getRetourByNumero(String numero) throws SQLException {
        return retourDAO.findByNumero(numero);
    }
}

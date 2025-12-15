package org.example.model.service;

import org.example.app.AppConfig;
import org.example.dao.VenteDAO;
import org.example.dao.ProduitDAO;
import org.example.model.entity.Vente;
import org.example.model.entity.LigneVente;
import org.example.model.entity.Produit;
import org.example.model.pattern.strategy.PaymentStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VenteService {
    
    private static VenteService instance;
    private final VenteDAO venteDAO;
    private final ProduitDAO produitDAO;
    
    private VenteService() {
        this.venteDAO = new VenteDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    public static synchronized VenteService getInstance() {
        if (instance == null) {
            instance = new VenteService();
        }
        return instance;
    }
    
    public Vente processSale(Vente vente, PaymentStrategy paymentStrategy) {
        
        if (vente.getLignes() == null || vente.getLignes().isEmpty()) {
            throw new IllegalArgumentException("La vente doit contenir au moins un article");
        }
        
        for (LigneVente ligne : vente.getLignes()) {
            Produit produitLigne = ligne.getProduit();
            
            if (produitLigne.getId() == null || produitLigne.getId() <= 0) {
                continue;
            }
            
            Optional<Produit> produitOpt = produitDAO.findById(produitLigne.getId());
            if (produitOpt.isEmpty()) {
                continue;
            }
            
            Produit produit = produitOpt.get();
            if (produit.getQuantiteStock() < ligne.getQuantite()) {
                throw new IllegalArgumentException("Stock insuffisant pour: " + produit.getNom());
            }
        }
        
        if (paymentStrategy != null) {
            boolean paymentSuccess = paymentStrategy.effectuerPaiement(vente.getMontantFinal());
            if (!paymentSuccess) {
                throw new IllegalArgumentException("Le paiement a echoue");
            }
            vente.setModePaiement(paymentStrategy.getNomMethode());
        }
        
        if (vente.getNumero() == null || vente.getNumero().isEmpty()) {
            vente.setNumero(generateVenteNumero());
        }
        
        if (vente.getDateVente() == null) {
            vente.setDateVente(LocalDateTime.now());
        }
        vente.setStatut("VALIDEE");
        
        Vente savedVente = venteDAO.save(vente);
        
        for (LigneVente ligne : vente.getLignes()) {
            Produit produitLigne = ligne.getProduit();
            if (produitLigne.getId() == null || produitLigne.getId() <= 0) {
                continue;
            }
            
            Optional<Produit> produitOpt = produitDAO.findById(produitLigne.getId());
            if (produitOpt.isPresent()) {
                Produit produit = produitOpt.get();
                int newStock = produit.getQuantiteStock() - ligne.getQuantite();
                produitDAO.updateStock(produit.getId(), newStock);
            }
        }
        
        if (vente.getClient() != null && vente.getClient().getId() != null) {
            int pointsEarned = (int) (vente.getMontantFinal() * AppConfig.POINTS_PAR_EURO);
            ClientService.getInstance().recordPurchase(
                vente.getClient().getId(),
                vente.getMontantFinal(),
                pointsEarned
            );
        }
        
        return savedVente;
    }
    
    public Vente createSale(Vente vente) {
        if (vente.getNumero() == null || vente.getNumero().isEmpty()) {
            vente.setNumero(generateVenteNumero());
        }
        
        if (vente.getDateVente() == null) {
            vente.setDateVente(LocalDateTime.now());
        }
        
        if (vente.getStatut() == null) {
            vente.setStatut("EN_COURS");
        }
        
        return venteDAO.save(vente);
    }
    
    public List<Vente> getAllVentes() {
        return venteDAO.findAll();
    }
    
    public Optional<Vente> getVenteById(Long id) {
        return venteDAO.findById(id);
    }
    
    public Optional<Vente> getVenteByNumero(String numero) {
        return venteDAO.findByNumero(numero);
    }
    
    public List<Vente> getVentesByDateRange(LocalDate startDate, LocalDate endDate) {
        return venteDAO.findByDateRange(startDate, endDate);
    }
    
    public List<Vente> getVentesByClient(Long clientId) {
        return venteDAO.findByClient(clientId);
    }
    
    public List<Vente> getTodayVentes() {
        return venteDAO.findToday();
    }
    
    public boolean updateVenteStatut(Long venteId, String statut) {
        return venteDAO.updateStatut(venteId, statut);
    }
    
    public boolean cancelVente(Long venteId) {
        return venteDAO.updateStatut(venteId, "ANNULEE");
    }
    
    public double[] getStatistics(LocalDate startDate, LocalDate endDate) {
        return venteDAO.getStatistics(startDate, endDate);
    }
    
    public double getTodayRevenue() {
        double[] stats = venteDAO.getStatistics(LocalDate.now(), LocalDate.now());
        return stats[1]; 
    }
    
    public int getTodayTransactionCount() {
        double[] stats = venteDAO.getStatistics(LocalDate.now(), LocalDate.now());
        return (int) stats[0]; 
    }
    
    private String generateVenteNumero() {
        return "VNT" + System.currentTimeMillis();
    }
    
    public int getVenteCount() {
        return venteDAO.count();
    }
    
    public List<Vente> getRecentVentes(int limit) {
        List<Vente> allVentes = venteDAO.findAll();
        return allVentes.stream()
            .sorted((v1, v2) -> v2.getDateVente().compareTo(v1.getDateVente()))
            .limit(limit)
            .toList();
    }
}

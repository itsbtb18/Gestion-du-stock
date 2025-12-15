package org.example.model.service;

import org.example.dao.AvisClientDAO;
import org.example.dao.ClientDAO;
import org.example.model.entity.AvisClient;
import org.example.model.entity.CategorieAvis;
import org.example.model.entity.Client;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AvisService {
    
    private static AvisService instance;
    private final AvisClientDAO avisDAO;
    private final ClientDAO clientDAO;
    
    private AvisService() {
        this.avisDAO = new AvisClientDAO();
        this.clientDAO = new ClientDAO();
    }
    
    public static synchronized AvisService getInstance() {
        if (instance == null) {
            instance = new AvisService();
        }
        return instance;
    }
    
    public AvisClient submitAvis(AvisClient avis) throws SQLException {
        
        if (avis.getClient() == null) {
            throw new IllegalArgumentException("Le client est requis");
        }
        
        if (avis.getNote() < 1 || avis.getNote() > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        
        if (avis.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie est requise");
        }
        
        Optional<Client> clientOpt = clientDAO.findById(avis.getClient().getId());
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Client introuvable");
        }
        
        avis.setTraite(false);
        
        return avisDAO.save(avis);
    }
    
    public boolean respondToAvis(Long avisId, String reponse, Long responderId) throws SQLException {
        if (reponse == null || reponse.trim().isEmpty()) {
            throw new IllegalArgumentException("La réponse ne peut pas être vide");
        }
        
        Optional<AvisClient> avisOpt = avisDAO.findById(avisId);
        if (avisOpt.isEmpty()) {
            throw new IllegalArgumentException("Avis introuvable");
        }
        
        avisDAO.marquerTraite(avisId, reponse, responderId);
        return true;
    }
    
    public boolean updateAvis(AvisClient avis) throws SQLException {
        if (avis.getId() == null) {
            throw new IllegalArgumentException("L'ID de l'avis est requis");
        }
        
        Optional<AvisClient> existing = avisDAO.findById(avis.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Avis introuvable");
        }
        
        avisDAO.update(avis);
        return true;
    }
    
    public boolean deleteAvis(Long avisId) throws SQLException {
        avisDAO.delete(avisId);
        return true;
    }
    
    public Double getAverageRating() throws SQLException {
        return avisDAO.getMoyenneNotes();
    }
    
    public Double getAverageRatingByCategorie(CategorieAvis categorie) throws SQLException {
        return avisDAO.getMoyenneParCategorie(categorie);
    }
    
    public Double getSatisfactionScore() throws SQLException {
        List<AvisClient> allAvis = avisDAO.findAll();
        if (allAvis.isEmpty()) {
            return 0.0;
        }
        
        long satisfiedCount = allAvis.stream()
            .filter(avis -> avis.getNote() >= 4)
            .count();
        
        return (satisfiedCount * 100.0) / allAvis.size();
    }
    
    public Map<Integer, Long> getRatingDistribution() throws SQLException {
        List<AvisClient> allAvis = avisDAO.findAll();
        Map<Integer, Long> distribution = new HashMap<>();
        
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }
        
        for (AvisClient avis : allAvis) {
            distribution.merge(avis.getNote(), 1L, Long::sum);
        }
        
        return distribution;
    }
    
    public Map<CategorieAvis, Double> getCategoryPerformance() throws SQLException {
        Map<CategorieAvis, Double> performance = new HashMap<>();
        
        for (CategorieAvis categorie : CategorieAvis.values()) {
            Double moyenne = avisDAO.getMoyenneParCategorie(categorie);
            if (moyenne != null) {
                performance.put(categorie, moyenne);
            }
        }
        
        return performance;
    }
    
    public List<AvisClient> getUnprocessedAvis() throws SQLException {
        return avisDAO.findNonTraites();
    }
    
    public List<AvisClient> getNegativeAvis() throws SQLException {
        return avisDAO.findNegatifs();
    }
    
    public List<AvisClient> getAvisByNote(int note) throws SQLException {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        return avisDAO.findByNote(note);
    }
    
    public List<AvisClient> getAvisByCategorie(CategorieAvis categorie) throws SQLException {
        return avisDAO.findByCategorie(categorie);
    }
    
    public List<AvisClient> getAvisByClient(Long clientId) throws SQLException {
        return avisDAO.findByClient(clientId);
    }
    
    public List<AvisClient> getAllAvis() throws SQLException {
        return avisDAO.findAll();
    }
    
    public Optional<AvisClient> getAvisById(Long id) throws SQLException {
        return avisDAO.findById(id);
    }
    
    public int getTotalCount() throws SQLException {
        return avisDAO.count();
    }
    
    public boolean clientNeedsAttention(Long clientId) throws SQLException {
        List<AvisClient> clientAvis = avisDAO.findByClient(clientId);
        
        long negativeCount = clientAvis.stream()
            .filter(avis -> avis.getNote() <= 2)
            .count();
        
        return negativeCount >= 2; 
    }
}

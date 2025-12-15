package org.example.model.service;

import org.example.dao.FournisseurDAO;
import org.example.model.entity.Fournisseur;
import org.example.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FournisseurService {
    
    private static FournisseurService instance;
    private final FournisseurDAO fournisseurDAO;
    
    private FournisseurService() {
        this.fournisseurDAO = new FournisseurDAO();
    }
    
    public static synchronized FournisseurService getInstance() {
        if (instance == null) {
            instance = new FournisseurService();
        }
        return instance;
    }
    
    public Fournisseur registerFournisseur(Fournisseur fournisseur) throws SQLException {
        
        if (!ValidationUtil.estNonVide(fournisseur.getCode())) {
            throw new IllegalArgumentException("Le code fournisseur est requis");
        }
        
        if (!ValidationUtil.estNonVide(fournisseur.getNom())) {
            throw new IllegalArgumentException("Le nom du fournisseur est requis");
        }
        
        if (!ValidationUtil.estNonVide(fournisseur.getTelephone())) {
            throw new IllegalArgumentException("Le téléphone est requis");
        }
        
        Optional<Fournisseur> existing = fournisseurDAO.findByCode(fournisseur.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Un fournisseur avec ce code existe déjà");
        }
        
        if (ValidationUtil.estNonVide(fournisseur.getEmail()) && 
            !ValidationUtil.estEmailValide(fournisseur.getEmail())) {
            throw new IllegalArgumentException("L'adresse email n'est pas valide");
        }
        
        if (fournisseur.getNotePerforme() == null) {
            fournisseur.setNotePerforme(3.0); 
        }
        
        return fournisseurDAO.save(fournisseur);
    }
    
    public boolean updateFournisseur(Fournisseur fournisseur) throws SQLException {
        if (fournisseur.getId() == null) {
            throw new IllegalArgumentException("L'ID du fournisseur est requis");
        }
        
        Optional<Fournisseur> existing = fournisseurDAO.findById(fournisseur.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Fournisseur introuvable");
        }
        
        fournisseurDAO.update(fournisseur);
        return true;
    }
    
    public boolean updatePerformanceRating(Long fournisseurId, Double rating) throws SQLException {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Le rating doit être entre 0 et 5");
        }
        fournisseurDAO.updatePerformanceRating(fournisseurId, rating);
        return true;
    }
    
    public void calculatePerformance(Long fournisseurId, boolean deliveredOnTime, 
                                    boolean qualityAcceptable, boolean priceCompetitive) throws SQLException {
        Optional<Fournisseur> fournisseurOpt = fournisseurDAO.findById(fournisseurId);
        if (fournisseurOpt.isEmpty()) {
            return;
        }
        
        Fournisseur fournisseur = fournisseurOpt.get();
        double currentRating = fournisseur.getNotePerforme() != null ? 
                              fournisseur.getNotePerforme() : 3.0;
        
        double score = 0.0;
        if (deliveredOnTime) score += 2.0;
        if (qualityAcceptable) score += 2.0;
        if (priceCompetitive) score += 1.0;
        
        double newRating = (currentRating * 0.7) + (score * 0.3);
        
        updatePerformanceRating(fournisseurId, newRating);
    }
    
    public boolean deactivateFournisseur(Long fournisseurId) throws SQLException {
        fournisseurDAO.delete(fournisseurId);
        return true;
    }
    
    public List<Fournisseur> searchFournisseurs(String query) throws SQLException {
        if (!ValidationUtil.estNonVide(query)) {
            return fournisseurDAO.findAll();
        }
        return fournisseurDAO.search(query);
    }
    
    public List<Fournisseur> getAllFournisseurs() throws SQLException {
        return fournisseurDAO.findAll();
    }
    
    public List<Fournisseur> getActiveFournisseurs() throws SQLException {
        return fournisseurDAO.findActive();
    }
    
    public Optional<Fournisseur> getFournisseurById(Long id) throws SQLException {
        return fournisseurDAO.findById(id);
    }
    
    public Optional<Fournisseur> getFournisseurByCode(String code) throws SQLException {
        return fournisseurDAO.findByCode(code);
    }
    
    public int getTotalCount() throws SQLException {
        return fournisseurDAO.count();
    }
}

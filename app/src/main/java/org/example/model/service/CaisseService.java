package org.example.model.service;

import org.example.dao.CaisseDAO;
import org.example.model.entity.Caisse;
import org.example.model.entity.StatutCaisse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CaisseService {
    
    private static CaisseService instance;
    private final CaisseDAO caisseDAO;
    
    private CaisseService() {
        this.caisseDAO = new CaisseDAO();
    }
    
    public static synchronized CaisseService getInstance() {
        if (instance == null) {
            instance = new CaisseService();
        }
        return instance;
    }
    
    public Caisse ouvrirCaisse(Caisse caisse) throws SQLException {
        
        if (caisse.getCaissier() == null) {
            throw new IllegalArgumentException("Le caissier est requis");
        }
        
        Optional<Caisse> openSession = caisseDAO.findCaisseOuverte(caisse.getCaissier().getId());
        if (openSession.isPresent()) {
            throw new IllegalArgumentException("Le caissier a déjà une caisse ouverte");
        }
        
        if (caisse.getNumeroCaisse() == null || caisse.getNumeroCaisse().isEmpty()) {
            caisse.setNumeroCaisse(generateCaisseNumero());
        }
        
        caisse.setDateOuverture(LocalDateTime.now());
        caisse.setStatut(StatutCaisse.OUVERTE);
        
        if (caisse.getSoldeDepartEspeces() == null) {
            caisse.setSoldeDepartEspeces(0.0);
        }
        caisse.setTotalVentesEspeces(0.0);
        caisse.setTotalVentesCarte(0.0);
        caisse.setTotalVentesAutre(0.0);
        caisse.setTotalDepenses(0.0);
        
        return caisseDAO.save(caisse);
    }
    
    public boolean fermerCaisse(Long caisseId, Double soldeFinEspeces, String commentaire) throws SQLException {
        Optional<Caisse> caisseOpt = caisseDAO.findById(caisseId);
        if (caisseOpt.isEmpty()) {
            throw new IllegalArgumentException("Caisse introuvable");
        }
        
        Caisse caisse = caisseOpt.get();
        
        if (caisse.getStatut() != StatutCaisse.OUVERTE) {
            throw new IllegalArgumentException("Seule une caisse ouverte peut être fermée");
        }
        
        caisseDAO.fermerCaisse(caisseId, soldeFinEspeces, commentaire);
        return true;
    }
    
    public Double calculateExpectedCash(Caisse caisse) {
        double expected = caisse.getSoldeDepartEspeces() != null ? caisse.getSoldeDepartEspeces() : 0.0;
        expected += caisse.getTotalVentesEspeces() != null ? caisse.getTotalVentesEspeces() : 0.0;
        expected -= caisse.getTotalDepenses() != null ? caisse.getTotalDepenses() : 0.0;
        return expected;
    }
    
    public Double calculateVariance(Caisse caisse, Double soldeFinEspeces) {
        double expected = calculateExpectedCash(caisse);
        return soldeFinEspeces - expected;
    }
    
    public boolean suspendCaisse(Long caisseId) throws SQLException {
        caisseDAO.updateStatut(caisseId, StatutCaisse.SUSPENDUE);
        return true;
    }
    
    public boolean resumeCaisse(Long caisseId) throws SQLException {
        Optional<Caisse> caisseOpt = caisseDAO.findById(caisseId);
        if (caisseOpt.isEmpty()) {
            throw new IllegalArgumentException("Caisse introuvable");
        }
        
        Caisse caisse = caisseOpt.get();
        if (caisse.getStatut() != StatutCaisse.SUSPENDUE) {
            throw new IllegalArgumentException("Seule une caisse suspendue peut être reprise");
        }
        
        caisseDAO.updateStatut(caisseId, StatutCaisse.OUVERTE);
        return true;
    }
    
    public Optional<Caisse> getCurrentSession(Long caissierId) throws SQLException {
        return caisseDAO.findCaisseOuverte(caissierId);
    }
    
    public List<Caisse> getSessionsByCaissier(Long caissierId) throws SQLException {
        return caisseDAO.findByCaissier(caissierId);
    }
    
    public List<Caisse> getSessionsByStatut(StatutCaisse statut) throws SQLException {
        return caisseDAO.findByStatut(statut);
    }
    
    public List<Caisse> getSessionsByPeriode(LocalDate debut, LocalDate fin) throws SQLException {
        return caisseDAO.findByPeriode(debut, fin);
    }
    
    public List<Caisse> getAllSessions() throws SQLException {
        return caisseDAO.findAll();
    }
    
    public Optional<Caisse> getSessionById(Long id) throws SQLException {
        return caisseDAO.findById(id);
    }
    
    public Optional<Caisse> getSessionByNumero(String numero) throws SQLException {
        return caisseDAO.findByNumero(numero);
    }
    
    private String generateCaisseNumero() {
        return "CAISSE-" + System.currentTimeMillis();
    }
}

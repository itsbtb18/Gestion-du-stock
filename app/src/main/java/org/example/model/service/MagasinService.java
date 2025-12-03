package org.example.model.service;

import org.example.dao.*;
import org.example.model.entity.*;
import java.util.List;

/**
 * MagasinService - Singleton central service for managing the store
 * Thread-safe implementation using double-checked locking
 */
public class MagasinService {
    
    // Singleton instance - volatile ensures visibility across threads
    private static volatile MagasinService instance;
    
    // DAOs for data access
    private final ProduitDAO produitDAO;
    private final VenteDAO venteDAO;
    private final MouvementStockDAO mouvementStockDAO;
    private final ClientDAO clientDAO;
    
    // Services
    private final ClientService clientService;
    private final StatistiquesService statistiquesService;
    private final AlerteService alerteService;
    
    /**
     * Private constructor to prevent direct instantiation
     * Initializes all DAOs and services
     */
    private MagasinService() {
        // Initialize DAOs
        this.produitDAO = new ProduitDAO();
        this.venteDAO = new VenteDAO();
        this.mouvementStockDAO = new MouvementStockDAO();
        this.clientDAO = new ClientDAO();
        
        // Initialize services
        this.clientService = new ClientService();
        this.statistiquesService = new StatistiquesService();
        this.alerteService = new AlerteService();
    }
    
    /**
     * Get the singleton instance of MagasinService
     * Thread-safe using double-checked locking pattern
     * @return the unique instance of MagasinService
     */
    public static MagasinService getInstance() {
        if (instance == null) {
            synchronized (MagasinService.class) {
                if (instance == null) {
                    instance = new MagasinService();
                }
            }
        }
        return instance;
    }
    
    // Getters for DAOs
    public ProduitDAO getProduitDAO() {
        return produitDAO;
    }
    
    public VenteDAO getVenteDAO() {
        return venteDAO;
    }
    
    public MouvementStockDAO getMouvementStockDAO() {
        return mouvementStockDAO;
    }
    
    public ClientDAO getClientDAO() {
        return clientDAO;
    }
    
    // Getters for Services
    public ClientService getClientService() {
        return clientService;
    }
    
    public StatistiquesService getStatistiquesService() {
        return statistiquesService;
    }
    
    public AlerteService getAlerteService() {
        return alerteService;
    }
    
    /**
     * Prevent cloning of singleton instance
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton is not allowed");
    }
}

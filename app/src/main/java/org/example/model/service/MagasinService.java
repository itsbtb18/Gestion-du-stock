package org.example.model.service;

import org.example.dao.*;

public class MagasinService {
    
    private static volatile MagasinService instance;
    
    private final ProduitDAO produitDAO;
    private final VenteDAO venteDAO;
    private final MouvementStockDAO mouvementStockDAO;
    private final ClientDAO clientDAO;
    
    private final ClientService clientService;
    private final StatistiquesService statistiquesService;
    private final AlerteService alerteService;
    
    private MagasinService() {
        
        this.produitDAO = new ProduitDAO();
        this.venteDAO = new VenteDAO();
        this.mouvementStockDAO = new MouvementStockDAO();
        this.clientDAO = new ClientDAO();
        
        this.clientService = ClientService.getInstance();
        this.statistiquesService = StatistiquesService.getInstance();
        this.alerteService = AlerteService.getInstance();
    }
    
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
    
    public ClientService getClientService() {
        return clientService;
    }
    
    public StatistiquesService getStatistiquesService() {
        return statistiquesService;
    }
    
    public AlerteService getAlerteService() {
        return alerteService;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton is not allowed");
    }
}

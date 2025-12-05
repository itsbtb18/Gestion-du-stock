package org.example.model.service;

import org.example.dao.ClientDAO;
import org.example.model.entity.Client;
import org.example.model.entity.TypeClient;
import org.example.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * ClientService - Business logic for client management
 * Singleton service for handling client operations
 */
public class ClientService {
    
    private static ClientService instance;
    private final ClientDAO clientDAO;
    
    private ClientService() {
        this.clientDAO = new ClientDAO();
    }
    
    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }
    
    /**
     * Register a new client
     */
    public Client registerClient(Client client) {
        if (!ValidationUtil.estNonVide(client.getNom())) {
            throw new IllegalArgumentException("Le nom du client est requis");
        }
        
        if (!ValidationUtil.estNonVide(client.getTelephone())) {
            throw new IllegalArgumentException("Le téléphone du client est requis");
        }
        
        if (!ValidationUtil.estTelephoneValide(client.getTelephone())) {
            throw new IllegalArgumentException("Le numéro de téléphone n'est pas valide");
        }
        
        Optional<Client> existing = clientDAO.findByTelephone(client.getTelephone());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Un client avec ce numéro existe déjà");
        }
        
        if (ValidationUtil.estNonVide(client.getEmail()) && 
            !ValidationUtil.estEmailValide(client.getEmail())) {
            throw new IllegalArgumentException("L'adresse email n'est pas valide");
        }
        
        if (!ValidationUtil.estNonVide(client.getCode())) {
            client.setCode(generateClientCode());
        }
        
        if (client.getTypeClient() == null) {
            client.setTypeClient(TypeClient.NORMAL);
        }
        
        return clientDAO.save(client);
    }
    
    public boolean updateClient(Client client) {
        if (client.getId() == null) {
            throw new IllegalArgumentException("L'ID du client est requis");
        }
        return clientDAO.update(client);
    }
    
    public List<Client> searchClients(String query) {
        if (!ValidationUtil.estNonVide(query)) {
            return clientDAO.findAll();
        }
        return clientDAO.search(query);
    }
    
    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }
    
    public Optional<Client> getClientById(Long id) {
        return clientDAO.findById(id);
    }
    
    public Optional<Client> getClientByTelephone(String telephone) {
        return clientDAO.findByTelephone(telephone);
    }
    
    public boolean addLoyaltyPoints(Long clientId, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Les points doivent être positifs");
        }
        return clientDAO.updatePoints(clientId, points);
    }
    
    public boolean useLoyaltyPoints(Long clientId, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Les points doivent être positifs");
        }
        
        Optional<Client> clientOpt = clientDAO.findById(clientId);
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Client non trouvé");
        }
        
        Client client = clientOpt.get();
        if (client.getPointsFidelite() < points) {
            throw new IllegalArgumentException("Points insuffisants");
        }
        
        return clientDAO.updatePoints(clientId, -points);
    }
    
    public boolean recordPurchase(Long clientId, double amount, int pointsEarned) {
        boolean totalUpdated = clientDAO.updateTotalAchats(clientId, amount);
        boolean pointsUpdated = clientDAO.updatePoints(clientId, pointsEarned);
        
        if (totalUpdated) {
            Optional<Client> clientOpt = clientDAO.findById(clientId);
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                if (client.getTotalAchats() >= 1000 && client.getTypeClient() != TypeClient.VIP) {
                    client.setTypeClient(TypeClient.VIP);
                    clientDAO.update(client);
                }
            }
        }
        
        return totalUpdated && pointsUpdated;
    }
    
    public List<Client> getVIPClients() {
        return clientDAO.findVIPClients(1000.0);
    }
    
    public boolean deleteClient(Long id) {
        return clientDAO.delete(id);
    }
    
    private String generateClientCode() {
        int count = clientDAO.count();
        return String.format("CLI%04d", count + 1);
    }
    
    public int getClientCount() {
        return clientDAO.count();
    }
}

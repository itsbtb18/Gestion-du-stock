package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Client;
import org.example.model.entity.TypeClient;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ClientDAO - Data Access Object for Client entity
 * Handles all database operations for clients
 */
public class ClientDAO {
    
    private final DatabaseConnection dbConnection;
    
    public ClientDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Find all clients
     * @return list of all active clients
     */
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE actif = TRUE ORDER BY nom, prenom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all clients: " + e.getMessage());
        }
        
        return clients;
    }
    
    /**
     * Find client by ID
     * @param id the client ID
     * @return Optional containing the client if found
     */
    public Optional<Client> findById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding client by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find client by phone number
     * @param telephone the phone number
     * @return Optional containing the client if found
     */
    public Optional<Client> findByTelephone(String telephone) {
        String sql = "SELECT * FROM clients WHERE telephone = ? AND actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, telephone);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding client by phone: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find client by code
     * @param code the client code
     * @return Optional containing the client if found
     */
    public Optional<Client> findByCode(String code) {
        String sql = "SELECT * FROM clients WHERE code = ? AND actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding client by code: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Search clients by name, phone, or email
     * @param query the search query
     * @return list of matching clients
     */
    public List<Client> search(String query) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE actif = TRUE AND " +
                    "(LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR telephone LIKE ? OR LOWER(email) LIKE ?) " +
                    "ORDER BY nom, prenom";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching clients: " + e.getMessage());
        }
        
        return clients;
    }
    
    /**
     * Find VIP clients (total purchases > threshold)
     * @param threshold the purchase threshold
     * @return list of VIP clients
     */
    public List<Client> findVIPClients(double threshold) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE actif = TRUE AND total_achats >= ? " +
                    "ORDER BY total_achats DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, threshold);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding VIP clients: " + e.getMessage());
        }
        
        return clients;
    }
    
    /**
     * Save a new client
     * @param client the client to save
     * @return the saved client with generated ID
     */
    public Client save(Client client) {
        String sql = "INSERT INTO clients (code, nom, prenom, telephone, email, adresse, " +
                    "type_client, date_inscription, total_achats, points_fidelite, actif) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, client.getCode());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getPrenom());
            pstmt.setString(4, client.getTelephone());
            pstmt.setString(5, client.getEmail());
            pstmt.setString(6, client.getAdresse());
            pstmt.setString(7, client.getTypeClient().name());
            
            if (client.getDateInscription() != null) {
                pstmt.setDate(8, Date.valueOf(client.getDateInscription()));
            } else {
                pstmt.setDate(8, Date.valueOf(LocalDate.now()));
            }
            
            pstmt.setDouble(9, client.getTotalAchats());
            pstmt.setInt(10, client.getPointsFidelite());
            pstmt.setBoolean(11, true);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                }
                System.out.println("Client saved successfully: " + client.getCode());
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving client: " + e.getMessage());
        }
        
        return client;
    }
    
    /**
     * Update an existing client
     * @param client the client to update
     * @return true if update successful
     */
    public boolean update(Client client) {
        String sql = "UPDATE clients SET code = ?, nom = ?, prenom = ?, telephone = ?, email = ?, " +
                    "adresse = ?, type_client = ?, total_achats = ?, points_fidelite = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, client.getCode());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getPrenom());
            pstmt.setString(4, client.getTelephone());
            pstmt.setString(5, client.getEmail());
            pstmt.setString(6, client.getAdresse());
            pstmt.setString(7, client.getTypeClient().name());
            pstmt.setDouble(8, client.getTotalAchats());
            pstmt.setInt(9, client.getPointsFidelite());
            pstmt.setLong(10, client.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Client updated successfully: " + client.getCode());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating client: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete a client (soft delete - mark as inactive)
     * @param id the client ID
     * @return true if delete successful
     */
    public boolean delete(Long id) {
        String sql = "UPDATE clients SET actif = FALSE WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Client deleted successfully (soft delete)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting client: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update client loyalty points
     * @param clientId the client ID
     * @param points the points to add (can be negative)
     * @return true if update successful
     */
    public boolean updatePoints(Long clientId, int points) {
        String sql = "UPDATE clients SET points_fidelite = points_fidelite + ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, points);
            pstmt.setLong(2, clientId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating client points: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update client total purchases
     * @param clientId the client ID
     * @param amount the amount to add
     * @return true if update successful
     */
    public boolean updateTotalAchats(Long clientId, double amount) {
        String sql = "UPDATE clients SET total_achats = total_achats + ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, amount);
            pstmt.setLong(2, clientId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating client purchases: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to Client object
     */
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        
        client.setId(rs.getLong("id"));
        client.setCode(rs.getString("code"));
        client.setNom(rs.getString("nom"));
        client.setPrenom(rs.getString("prenom"));
        client.setTelephone(rs.getString("telephone"));
        client.setEmail(rs.getString("email"));
        client.setAdresse(rs.getString("adresse"));
        
        String typeClient = rs.getString("type_client");
        if (typeClient != null) {
            client.setTypeClient(TypeClient.valueOf(typeClient));
        }
        
        Date dateInscription = rs.getDate("date_inscription");
        if (dateInscription != null) {
            client.setDateInscription(dateInscription.toLocalDate());
        }
        
        client.setTotalAchats(rs.getDouble("total_achats"));
        client.setPointsFidelite(rs.getInt("points_fidelite"));
        
        return client;
    }
    
    /**
     * Get total client count
     * @return total number of active clients
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM clients WHERE actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting clients: " + e.getMessage());
        }
        
        return 0;
    }
}


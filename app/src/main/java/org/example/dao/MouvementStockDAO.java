package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.MouvementStock;
import org.example.model.entity.TypeMouvement;
import org.example.model.entity.Produit;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MouvementStockDAO - Data Access Object for MouvementStock entity
 * Handles all database operations for stock movements
 */
public class MouvementStockDAO {
    
    private final DatabaseConnection dbConnection;
    
    public MouvementStockDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Find all stock movements
     * @return list of all movements
     */
    public List<MouvementStock> findAll() {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.code as prod_code, p.nom as prod_nom " +
                    "FROM mouvements_stock m " +
                    "JOIN produits p ON m.produit_id = p.id " +
                    "ORDER BY m.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all movements: " + e.getMessage());
        }
        
        return mouvements;
    }
    
    /**
     * Find movement by ID
     * @param id the movement ID
     * @return Optional containing the movement if found
     */
    public Optional<MouvementStock> findById(Long id) {
        String sql = "SELECT m.*, p.code as prod_code, p.nom as prod_nom " +
                    "FROM mouvements_stock m " +
                    "JOIN produits p ON m.produit_id = p.id " +
                    "WHERE m.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToMouvement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding movement by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find movements by product
     * @param produitId the product ID
     * @return list of movements for the product
     */
    public List<MouvementStock> findByProduit(Long produitId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.code as prod_code, p.nom as prod_nom " +
                    "FROM mouvements_stock m " +
                    "JOIN produits p ON m.produit_id = p.id " +
                    "WHERE m.produit_id = ? " +
                    "ORDER BY m.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, produitId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding movements by product: " + e.getMessage());
        }
        
        return mouvements;
    }
    
    /**
     * Find movements by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of movements in the date range
     */
    public List<MouvementStock> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.code as prod_code, p.nom as prod_nom " +
                    "FROM mouvements_stock m " +
                    "JOIN produits p ON m.produit_id = p.id " +
                    "WHERE DATE(m.date_mouvement) BETWEEN ? AND ? " +
                    "ORDER BY m.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding movements by date range: " + e.getMessage());
        }
        
        return mouvements;
    }
    
    /**
     * Find movements by type
     * @param typeMouvement the movement type
     * @return list of movements of the specified type
     */
    public List<MouvementStock> findByType(TypeMouvement typeMouvement) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.code as prod_code, p.nom as prod_nom " +
                    "FROM mouvements_stock m " +
                    "JOIN produits p ON m.produit_id = p.id " +
                    "WHERE m.type_mouvement = ? " +
                    "ORDER BY m.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, typeMouvement.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding movements by type: " + e.getMessage());
        }
        
        return mouvements;
    }
    
    /**
     * Find today's movements
     * @return list of today's movements
     */
    public List<MouvementStock> findToday() {
        LocalDate today = LocalDate.now();
        return findByDateRange(today, today);
    }
    
    /**
     * Save a new stock movement and update product stock
     * @param mouvement the movement to save
     * @return the saved movement with generated ID
     */
    public MouvementStock save(MouvementStock mouvement) {
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert movement
            String mouvementSql = "INSERT INTO mouvements_stock (produit_id, type_mouvement, " +
                                "quantite, date_mouvement, utilisateur_id, motif) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(mouvementSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, mouvement.getProduit().getId());
                pstmt.setString(2, mouvement.getTypeMouvement().name());
                pstmt.setInt(3, mouvement.getQuantite());
                pstmt.setTimestamp(4, Timestamp.valueOf(mouvement.getDateMouvement()));
                
                if (mouvement.getUtilisateurId() != null) {
                    pstmt.setLong(5, mouvement.getUtilisateurId());
                } else {
                    pstmt.setNull(5, Types.BIGINT);
                }
                
                pstmt.setString(6, mouvement.getMotif());
                
                pstmt.executeUpdate();
                
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    mouvement.setId(generatedKeys.getLong(1));
                }
            }
            
            // Update product stock based on movement type coefficient
            String updateStockSql = "UPDATE produits SET quantite_stock = quantite_stock + ? WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateStockSql)) {
                int stockChange = mouvement.getQuantite() * mouvement.getTypeMouvement().getCoefficient();
                pstmt.setInt(1, stockChange);
                pstmt.setLong(2, mouvement.getProduit().getId());
                
                pstmt.executeUpdate();
            }
            
            conn.commit(); // Commit transaction
            System.out.println("Stock movement saved successfully");
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            System.err.println("Error saving movement: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
        
        return mouvement;
    }
    
    /**
     * Get movement statistics by type for a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of arrays with [type, totalQuantity]
     */
    public List<Object[]> getStatisticsByType(LocalDate startDate, LocalDate endDate) {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT type_mouvement, SUM(quantite) as total " +
                    "FROM mouvements_stock " +
                    "WHERE DATE(date_mouvement) BETWEEN ? AND ? " +
                    "GROUP BY type_mouvement " +
                    "ORDER BY total DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] stat = new Object[2];
                stat[0] = rs.getString("type_mouvement");
                stat[1] = rs.getInt("total");
                stats.add(stat);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting movement statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Map ResultSet to MouvementStock object
     */
    private MouvementStock mapResultSetToMouvement(ResultSet rs) throws SQLException {
        MouvementStock mouvement = new MouvementStock();
        
        mouvement.setId(rs.getLong("id"));
        mouvement.setQuantite(rs.getInt("quantite"));
        
        String typeMouvement = rs.getString("type_mouvement");
        if (typeMouvement != null) {
            mouvement.setTypeMouvement(TypeMouvement.valueOf(typeMouvement));
        }
        
        Timestamp dateMouvement = rs.getTimestamp("date_mouvement");
        if (dateMouvement != null) {
            mouvement.setDateMouvement(dateMouvement.toLocalDateTime());
        }
        
        long utilisateurId = rs.getLong("utilisateur_id");
        if (!rs.wasNull()) {
            mouvement.setUtilisateurId(utilisateurId);
        }
        
        mouvement.setMotif(rs.getString("motif"));
        
        // Create minimal product object
        Produit produit = new Produit();
        produit.setId(rs.getLong("produit_id"));
        produit.setCode(rs.getString("prod_code"));
        produit.setNom(rs.getString("prod_nom"));
        
        mouvement.setProduit(produit);
        
        return mouvement;
    }
    
    /**
     * Get total movements count
     * @return total number of movements
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM mouvements_stock";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting movements: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Find recent movements (limited)
     */
    public List<MouvementStock> findRecent(int limit) {
        String sql = "SELECT * FROM mouvements_stock ORDER BY date_mouvement DESC LIMIT ?";
        List<MouvementStock> mouvements = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding recent movements: " + e.getMessage());
        }
        
        return mouvements;
    }
}


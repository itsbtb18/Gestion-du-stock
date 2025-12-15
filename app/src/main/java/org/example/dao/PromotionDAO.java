package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Promotion;
import org.example.model.entity.Promotion.PromotionType;
import org.example.model.entity.Promotion.PromotionScope;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PromotionDAO {
    
    private final DatabaseConnection dbConnection;
    
    public PromotionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Promotion> findByStoreId(Long storeId) {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotions WHERE store_id = ? ORDER BY priority DESC, created_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding promotions: " + e.getMessage());
        }
        
        return promotions;
    }
    
    public List<Promotion> findActiveByStoreId(Long storeId) {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotions WHERE store_id = ? AND active = TRUE " +
                    "ORDER BY priority DESC, created_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding active promotions: " + e.getMessage());
        }
        
        return promotions;
    }
    
    public Optional<Promotion> findById(Long id) {
        String sql = "SELECT * FROM promotions WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPromotion(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding promotion by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Promotion save(Promotion promotion) {
        String sql = "INSERT INTO promotions (store_id, name, description, type, value, scope, " +
                    "target_ids, start_date, end_date, active, priority, created_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, promotion.getStoreId());
            pstmt.setString(2, promotion.getName());
            pstmt.setString(3, promotion.getDescription());
            pstmt.setString(4, promotion.getType().name());
            pstmt.setDouble(5, promotion.getValue());
            pstmt.setString(6, promotion.getScope().name());
            pstmt.setString(7, promotion.getTargetIds());
            pstmt.setDate(8, promotion.getStartDate() != null ? Date.valueOf(promotion.getStartDate()) : null);
            pstmt.setDate(9, promotion.getEndDate() != null ? Date.valueOf(promotion.getEndDate()) : null);
            pstmt.setBoolean(10, promotion.isActive());
            pstmt.setInt(11, promotion.getPriority());
            pstmt.setTimestamp(12, Timestamp.valueOf(promotion.getCreatedDate()));
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                promotion.setId(rs.getLong(1));
            }
            
            return promotion;
            
        } catch (SQLException e) {
            System.err.println("Error saving promotion: " + e.getMessage());
            throw new RuntimeException("Failed to save promotion", e);
        }
    }
    
    public boolean update(Promotion promotion) {
        String sql = "UPDATE promotions SET name = ?, description = ?, type = ?, value = ?, " +
                    "scope = ?, target_ids = ?, start_date = ?, end_date = ?, active = ?, " +
                    "priority = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, promotion.getName());
            pstmt.setString(2, promotion.getDescription());
            pstmt.setString(3, promotion.getType().name());
            pstmt.setDouble(4, promotion.getValue());
            pstmt.setString(5, promotion.getScope().name());
            pstmt.setString(6, promotion.getTargetIds());
            pstmt.setDate(7, promotion.getStartDate() != null ? Date.valueOf(promotion.getStartDate()) : null);
            pstmt.setDate(8, promotion.getEndDate() != null ? Date.valueOf(promotion.getEndDate()) : null);
            pstmt.setBoolean(9, promotion.isActive());
            pstmt.setInt(10, promotion.getPriority());
            pstmt.setLong(11, promotion.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating promotion: " + e.getMessage());
            return false;
        }
    }
    
    public boolean delete(Long id) {
        String sql = "DELETE FROM promotions WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting promotion: " + e.getMessage());
            return false;
        }
    }
    
    private Promotion mapResultSetToPromotion(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setId(rs.getLong("id"));
        promotion.setStoreId(rs.getLong("store_id"));
        promotion.setName(rs.getString("name"));
        promotion.setDescription(rs.getString("description"));
        promotion.setType(PromotionType.valueOf(rs.getString("type")));
        promotion.setValue(rs.getDouble("value"));
        promotion.setScope(PromotionScope.valueOf(rs.getString("scope")));
        promotion.setTargetIds(rs.getString("target_ids"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            promotion.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            promotion.setEndDate(endDate.toLocalDate());
        }
        
        promotion.setActive(rs.getBoolean("active"));
        promotion.setPriority(rs.getInt("priority"));
        
        Timestamp createdTs = rs.getTimestamp("created_date");
        if (createdTs != null) {
            promotion.setCreatedDate(createdTs.toLocalDateTime());
        }
        
        return promotion;
    }
}

package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Store;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StoreDAO {
    
    private final DatabaseConnection dbConnection;
    
    public StoreDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Store> findAll() {
        List<Store> stores = new ArrayList<>();
        String sql = "SELECT * FROM stores ORDER BY name";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stores.add(mapResultSetToStore(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all stores: " + e.getMessage());
        }
        
        return stores;
    }
    
    public Optional<Store> findById(Long id) {
        String sql = "SELECT * FROM stores WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToStore(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding store by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Optional<Store> findByCode(String code) {
        String sql = "SELECT * FROM stores WHERE code = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToStore(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding store by code: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Store save(Store store) {
        String sql = "INSERT INTO stores (code, name, address, phone, email, logo_path, " +
                    "currency, language, created_date, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, store.getCode());
            pstmt.setString(2, store.getName());
            pstmt.setString(3, store.getAddress());
            pstmt.setString(4, store.getPhone());
            pstmt.setString(5, store.getEmail());
            pstmt.setString(6, store.getLogoPath());
            pstmt.setString(7, store.getCurrency());
            pstmt.setString(8, store.getLanguage());
            pstmt.setTimestamp(9, Timestamp.valueOf(store.getCreatedDate()));
            pstmt.setBoolean(10, store.isActive());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                store.setId(rs.getLong(1));
            }
            
            return store;
            
        } catch (SQLException e) {
            System.err.println("Error saving store: " + e.getMessage());
            throw new RuntimeException("Failed to save store", e);
        }
    }
    
    public boolean update(Store store) {
        String sql = "UPDATE stores SET code = ?, name = ?, address = ?, phone = ?, email = ?, " +
                    "logo_path = ?, currency = ?, language = ?, active = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, store.getCode());
            pstmt.setString(2, store.getName());
            pstmt.setString(3, store.getAddress());
            pstmt.setString(4, store.getPhone());
            pstmt.setString(5, store.getEmail());
            pstmt.setString(6, store.getLogoPath());
            pstmt.setString(7, store.getCurrency());
            pstmt.setString(8, store.getLanguage());
            pstmt.setBoolean(9, store.isActive());
            pstmt.setLong(10, store.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating store: " + e.getMessage());
            return false;
        }
    }
    
    public boolean delete(Long id) {
        String sql = "DELETE FROM stores WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting store: " + e.getMessage());
            return false;
        }
    }
    
    private Store mapResultSetToStore(ResultSet rs) throws SQLException {
        Store store = new Store();
        store.setId(rs.getLong("id"));
        store.setCode(rs.getString("code"));
        store.setName(rs.getString("name"));
        store.setAddress(rs.getString("address"));
        store.setPhone(rs.getString("phone"));
        store.setEmail(rs.getString("email"));
        store.setLogoPath(rs.getString("logo_path"));
        store.setCurrency(rs.getString("currency"));
        store.setLanguage(rs.getString("language"));
        
        Timestamp createdTs = rs.getTimestamp("created_date");
        if (createdTs != null) {
            store.setCreatedDate(createdTs.toLocalDateTime());
        }
        
        store.setActive(rs.getBoolean("active"));
        
        return store;
    }
}

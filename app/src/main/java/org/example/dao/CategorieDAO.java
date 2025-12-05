package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CategorieDAO - Data Access Object for Category entity
 */
public class CategorieDAO {
    
    private final DatabaseConnection dbConnection;
    
    public CategorieDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public Categorie save(Categorie categorie) {
        String sql = "INSERT INTO categories (code, nom, description, actif) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, categorie.getCode());
            pstmt.setString(2, categorie.getNom());
            pstmt.setString(3, categorie.getDescription());
            pstmt.setBoolean(4, categorie.isActif());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        categorie.setId(rs.getLong(1));
                    }
                }
            }
            
            return categorie;
            
        } catch (SQLException e) {
            System.err.println("Error saving category: " + e.getMessage());
            throw new RuntimeException("Failed to save category", e);
        }
    }
    
    public boolean update(Categorie categorie) {
        String sql = "UPDATE categories SET code = ?, nom = ?, description = ?, actif = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categorie.getCode());
            pstmt.setString(2, categorie.getNom());
            pstmt.setString(3, categorie.getDescription());
            pstmt.setBoolean(4, categorie.isActif());
            pstmt.setLong(5, categorie.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }
    
    public boolean delete(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
    
    public Optional<Categorie> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCategorie(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding category by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Optional<Categorie> findByCode(String code) {
        String sql = "SELECT * FROM categories WHERE code = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCategorie(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding category by code: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public List<Categorie> findAll() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY nom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    public List<Categorie> findActive() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE actif = true ORDER BY nom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding active categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    public int count() {
        String sql = "SELECT COUNT(*) FROM categories";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting categories: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Categorie mapResultSetToCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        categorie.setId(rs.getLong("id"));
        categorie.setCode(rs.getString("code"));
        categorie.setNom(rs.getString("nom"));
        categorie.setDescription(rs.getString("description"));
        categorie.setActif(rs.getBoolean("actif"));
        return categorie;
    }
}

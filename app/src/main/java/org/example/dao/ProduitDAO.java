package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Produit;
import org.example.model.entity.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProduitDAO {
    
    private final DatabaseConnection dbConnection;
    
    public ProduitDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Produit> findAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.code as cat_code, c.nom as cat_nom, c.description as cat_desc " +
                    "FROM produits p LEFT JOIN categories c ON p.categorie_id = c.id " +
                    "WHERE p.actif = TRUE ORDER BY p.nom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all products: " + e.getMessage());
        }
        
        return produits;
    }
    
    public Optional<Produit> findById(Long id) {
        String sql = "SELECT p.*, c.code as cat_code, c.nom as cat_nom, c.description as cat_desc " +
                    "FROM produits p LEFT JOIN categories c ON p.categorie_id = c.id " +
                    "WHERE p.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProduit(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding product by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Optional<Produit> findByCode(String code) {
        String sql = "SELECT p.*, c.code as cat_code, c.nom as cat_nom, c.description as cat_desc " +
                    "FROM produits p LEFT JOIN categories c ON p.categorie_id = c.id " +
                    "WHERE p.code = ? AND p.actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProduit(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding product by code: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public List<Produit> search(String query) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.code as cat_code, c.nom as cat_nom, c.description as cat_desc " +
                    "FROM produits p LEFT JOIN categories c ON p.categorie_id = c.id " +
                    "WHERE p.actif = TRUE AND (LOWER(p.nom) LIKE ? OR LOWER(p.code) LIKE ?) " +
                    "ORDER BY p.nom";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        
        return produits;
    }
    
    public List<Produit> findLowStock() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.code as cat_code, c.nom as cat_nom, c.description as cat_desc " +
                    "FROM produits p LEFT JOIN categories c ON p.categorie_id = c.id " +
                    "WHERE p.actif = TRUE AND p.quantite_stock <= p.seuil_alerte " +
                    "ORDER BY p.quantite_stock ASC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding low stock products: " + e.getMessage());
        }
        
        return produits;
    }
    
    public List<Produit> findExpiringSoon(int days) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.code as cat_code, c.nom as cat_nom, c.description as cat_desc " +
                    "FROM produits p LEFT JOIN categories c ON p.categorie_id = c.id " +
                    "WHERE p.actif = TRUE AND p.date_expiration IS NOT NULL " +
                    "AND p.date_expiration <= DATEADD('DAY', ?, CURRENT_DATE) " +
                    "ORDER BY p.date_expiration ASC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding expiring products: " + e.getMessage());
        }
        
        return produits;
    }
    
    public Produit save(Produit produit) {
        String sql = "INSERT INTO produits (code, nom, description, prix, quantite_stock, " +
                    "seuil_alerte, categorie_id, unite, date_expiration, fournisseur, emplacement, actif) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, produit.getCode());
            pstmt.setString(2, produit.getNom());
            pstmt.setString(3, produit.getDescription());
            pstmt.setDouble(4, produit.getPrix());
            pstmt.setInt(5, produit.getQuantiteStock());
            pstmt.setInt(6, produit.getSeuilAlerte());
            
            if (produit.getCategorie() != null) {
                pstmt.setLong(7, produit.getCategorie().getId());
            } else {
                pstmt.setNull(7, Types.BIGINT);
            }
            
            pstmt.setString(8, produit.getUnite());
            
            if (produit.getDateExpiration() != null) {
                pstmt.setDate(9, Date.valueOf(produit.getDateExpiration()));
            } else {
                pstmt.setNull(9, Types.DATE);
            }
            
            pstmt.setString(10, produit.getFournisseur());
            pstmt.setString(11, produit.getEmplacement());
            pstmt.setBoolean(12, produit.isActif());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getLong(1));
                }
                System.out.println("Product saved successfully: " + produit.getCode());
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving product: " + e.getMessage());
        }
        
        return produit;
    }
    
    public boolean update(Produit produit) {
        String sql = "UPDATE produits SET code = ?, nom = ?, description = ?, prix = ?, " +
                    "quantite_stock = ?, seuil_alerte = ?, categorie_id = ?, unite = ?, " +
                    "date_expiration = ?, fournisseur = ?, emplacement = ?, actif = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, produit.getCode());
            pstmt.setString(2, produit.getNom());
            pstmt.setString(3, produit.getDescription());
            pstmt.setDouble(4, produit.getPrix());
            pstmt.setInt(5, produit.getQuantiteStock());
            pstmt.setInt(6, produit.getSeuilAlerte());
            
            if (produit.getCategorie() != null) {
                pstmt.setLong(7, produit.getCategorie().getId());
            } else {
                pstmt.setNull(7, Types.BIGINT);
            }
            
            pstmt.setString(8, produit.getUnite());
            
            if (produit.getDateExpiration() != null) {
                pstmt.setDate(9, Date.valueOf(produit.getDateExpiration()));
            } else {
                pstmt.setNull(9, Types.DATE);
            }
            
            pstmt.setString(10, produit.getFournisseur());
            pstmt.setString(11, produit.getEmplacement());
            pstmt.setBoolean(12, produit.isActif());
            pstmt.setLong(13, produit.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Product updated successfully: " + produit.getCode());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean delete(Long id) {
        String sql = "UPDATE produits SET actif = FALSE WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Product deleted successfully (soft delete)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean updateStock(Long produitId, int newQuantity) {
        String sql = "UPDATE produits SET quantite_stock = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setLong(2, produitId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating product stock: " + e.getMessage());
        }
        
        return false;
    }
    
    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        
        produit.setId(rs.getLong("id"));
        produit.setCode(rs.getString("code"));
        produit.setNom(rs.getString("nom"));
        produit.setDescription(rs.getString("description"));
        produit.setPrix(rs.getDouble("prix"));
        produit.setQuantiteStock(rs.getInt("quantite_stock"));
        produit.setSeuilAlerte(rs.getInt("seuil_alerte"));
        produit.setUnite(rs.getString("unite"));
        
        Date dateExp = rs.getDate("date_expiration");
        if (dateExp != null) {
            produit.setDateExpiration(dateExp.toLocalDate());
        }
        
        produit.setFournisseur(rs.getString("fournisseur"));
        produit.setEmplacement(rs.getString("emplacement"));
        produit.setActif(rs.getBoolean("actif"));
        
        Long categorieId = rs.getLong("categorie_id");
        if (!rs.wasNull() && categorieId > 0) {
            Categorie categorie = new Categorie();
            categorie.setId(categorieId);
            String catCode = rs.getString("cat_code");
            if (catCode != null) {
                categorie.setCode(catCode);
                categorie.setNom(rs.getString("cat_nom"));
                categorie.setDescription(rs.getString("cat_desc"));
            }
            produit.setCategorie(categorie);
        }
        
        return produit;
    }
    
    public int count() {
        String sql = "SELECT COUNT(*) FROM produits WHERE actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting products: " + e.getMessage());
        }
        
        return 0;
    }
}


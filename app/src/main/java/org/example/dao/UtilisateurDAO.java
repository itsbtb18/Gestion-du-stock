package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Utilisateur;
import org.example.model.entity.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UtilisateurDAO - Data Access Object for User entity
 * Handles all database operations for users with BCrypt password hashing
 */
public class UtilisateurDAO {
    
    private final DatabaseConnection dbConnection;
    
    public UtilisateurDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Save a new user with hashed password
     */
    public Utilisateur save(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateurs (username, password, nom, prenom, email, role, " +
                    "date_creation, derniere_connexion, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Hash password before storing
            String hashedPassword = BCrypt.hashpw(utilisateur.getPassword(), BCrypt.gensalt(12));
            
            pstmt.setString(1, utilisateur.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, utilisateur.getNom());
            pstmt.setString(4, utilisateur.getPrenom());
            pstmt.setString(5, utilisateur.getEmail());
            pstmt.setString(6, utilisateur.getRole().name());
            pstmt.setTimestamp(7, Timestamp.valueOf(utilisateur.getDateCreation()));
            pstmt.setTimestamp(8, utilisateur.getDerniereConnexion() != null ? 
                Timestamp.valueOf(utilisateur.getDerniereConnexion()) : null);
            pstmt.setBoolean(9, utilisateur.isActif());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        utilisateur.setId(rs.getLong(1));
                    }
                }
            }
            
            return utilisateur;
            
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            throw new RuntimeException("Failed to save user", e);
        }
    }
    
    /**
     * Update user (password not updated here, use changePassword method)
     */
    public boolean update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, role = ?, actif = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, utilisateur.getRole().name());
            pstmt.setBoolean(5, utilisateur.isActif());
            pstmt.setLong(6, utilisateur.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Change user password with BCrypt hashing
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<Utilisateur> userOpt = findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        Utilisateur user = userOpt.get();
        
        // Verify old password
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return false;
        }
        
        // Hash new password
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        
        String sql = "UPDATE utilisateurs SET password = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hashedPassword);
            pstmt.setLong(2, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reset password (for admin/forgot password)
     */
    public boolean resetPassword(Long userId, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        
        String sql = "UPDATE utilisateurs SET password = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hashedPassword);
            pstmt.setLong(2, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Authenticate user with BCrypt verification
     */
    public Optional<Utilisateur> authenticate(String username, String password) {
        Optional<Utilisateur> userOpt = findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Utilisateur user = userOpt.get();
        
        // Check if user is active
        if (!user.isActif()) {
            return Optional.empty();
        }
        
        // Verify password with BCrypt
        if (BCrypt.checkpw(password, user.getPassword())) {
            // Update last login
            updateLastLogin(user.getId());
            user.setDerniereConnexion(LocalDateTime.now());
            return Optional.of(user);
        }
        
        return Optional.empty();
    }
    
    /**
     * Update last login timestamp
     */
    public boolean updateLastLogin(Long userId) {
        String sql = "UPDATE utilisateurs SET derniere_connexion = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(2, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find user by ID
     */
    public Optional<Utilisateur> findById(Long id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUtilisateur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find user by username
     */
    public Optional<Utilisateur> findByUsername(String username) {
        String sql = "SELECT * FROM utilisateurs WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUtilisateur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find user by email
     */
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUtilisateur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find all users
     */
    public List<Utilisateur> findAll() {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY nom, prenom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUtilisateur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Find users by role
     */
    public List<Utilisateur> findByRole(Role role) {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE role = ? ORDER BY nom, prenom";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUtilisateur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding users by role: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Find active users
     */
    public List<Utilisateur> findActive() {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE actif = true ORDER BY nom, prenom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUtilisateur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding active users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Delete user
     */
    public boolean delete(Long id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Map ResultSet to Utilisateur object
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password")); // Hashed password
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setRole(Role.valueOf(rs.getString("role")));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            user.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp derniereConnexion = rs.getTimestamp("derniere_connexion");
        if (derniereConnexion != null) {
            user.setDerniereConnexion(derniereConnexion.toLocalDateTime());
        }
        
        user.setActif(rs.getBoolean("actif"));
        
        return user;
    }
    
    /**
     * Count total users
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM utilisateurs";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
        }
        
        return 0;
    }
}

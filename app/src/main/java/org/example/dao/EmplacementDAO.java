package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Emplacement;
import org.example.model.entity.TypeEmplacement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des emplacements de stock
 */
public class EmplacementDAO {

    private final DatabaseConnection dbConnection;

    public EmplacementDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Sauvegarde un nouvel emplacement
     */
    public Emplacement save(Emplacement emplacement) throws SQLException {
        String sql = "INSERT INTO emplacements (code, nom, type, adresse, ville, responsable, " +
                "telephone, date_creation, actif, commentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, emplacement.getCode());
            stmt.setString(2, emplacement.getNom());
            stmt.setString(3, emplacement.getType().name());
            stmt.setString(4, emplacement.getAdresse());
            stmt.setString(5, emplacement.getVille());
            stmt.setString(6, emplacement.getResponsable());
            stmt.setString(7, emplacement.getTelephone());
            stmt.setTimestamp(8, Timestamp.valueOf(emplacement.getDateCreation()));
            stmt.setBoolean(9, emplacement.isActif());
            stmt.setString(10, emplacement.getCommentaire());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    emplacement.setId(rs.getLong(1));
                }
            }

            return emplacement;
        }
    }

    /**
     * Met Ã  jour un emplacement
     */
    public void update(Emplacement emplacement) throws SQLException {
        String sql = "UPDATE emplacements SET code = ?, nom = ?, type = ?, adresse = ?, ville = ?, " +
                "responsable = ?, telephone = ?, actif = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emplacement.getCode());
            stmt.setString(2, emplacement.getNom());
            stmt.setString(3, emplacement.getType().name());
            stmt.setString(4, emplacement.getAdresse());
            stmt.setString(5, emplacement.getVille());
            stmt.setString(6, emplacement.getResponsable());
            stmt.setString(7, emplacement.getTelephone());
            stmt.setBoolean(8, emplacement.isActif());
            stmt.setString(9, emplacement.getCommentaire());
            stmt.setLong(10, emplacement.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Supprime un emplacement
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM emplacements WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche un emplacement par ID
     */
    public Optional<Emplacement> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM emplacements WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmplacement(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recherche un emplacement par code
     */
    public Optional<Emplacement> findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM emplacements WHERE code = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmplacement(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne tous les emplacements
     */
    public List<Emplacement> findAll() throws SQLException {
        String sql = "SELECT * FROM emplacements ORDER BY nom";
        List<Emplacement> emplacements = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emplacements.add(mapResultSetToEmplacement(rs));
            }
        }
        return emplacements;
    }

    /**
     * Retourne les emplacements actifs
     */
    public List<Emplacement> findActive() throws SQLException {
        String sql = "SELECT * FROM emplacements WHERE actif = true ORDER BY nom";
        List<Emplacement> emplacements = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emplacements.add(mapResultSetToEmplacement(rs));
            }
        }
        return emplacements;
    }

    /**
     * Retourne les emplacements par type
     */
    public List<Emplacement> findByType(TypeEmplacement type) throws SQLException {
        String sql = "SELECT * FROM emplacements WHERE type = ? ORDER BY nom";
        List<Emplacement> emplacements = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emplacements.add(mapResultSetToEmplacement(rs));
                }
            }
        }
        return emplacements;
    }

    /**
     * Compte le nombre d'emplacements
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM emplacements";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Convertit un ResultSet en Emplacement
     */
    private Emplacement mapResultSetToEmplacement(ResultSet rs) throws SQLException {
        Emplacement emplacement = new Emplacement();
        emplacement.setId(rs.getLong("id"));
        emplacement.setCode(rs.getString("code"));
        emplacement.setNom(rs.getString("nom"));
        emplacement.setType(TypeEmplacement.valueOf(rs.getString("type")));
        emplacement.setAdresse(rs.getString("adresse"));
        emplacement.setVille(rs.getString("ville"));
        emplacement.setResponsable(rs.getString("responsable"));
        emplacement.setTelephone(rs.getString("telephone"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            emplacement.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        emplacement.setActif(rs.getBoolean("actif"));
        emplacement.setCommentaire(rs.getString("commentaire"));
        
        return emplacement;
    }
}


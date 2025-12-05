package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Fournisseur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des fournisseurs
 */
public class FournisseurDAO {

    private final DatabaseConnection dbConnection;

    public FournisseurDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Sauvegarde un nouveau fournisseur
     */
    public Fournisseur save(Fournisseur fournisseur) throws SQLException {
        String sql = "INSERT INTO fournisseurs (code, nom, contact, telephone, email, adresse, ville, pays, " +
                "code_postal, site_web, numero_tva, conditions_paiement, note_performe, date_creation, actif, commentaire) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fournisseur.getCode());
            stmt.setString(2, fournisseur.getNom());
            stmt.setString(3, fournisseur.getContact());
            stmt.setString(4, fournisseur.getTelephone());
            stmt.setString(5, fournisseur.getEmail());
            stmt.setString(6, fournisseur.getAdresse());
            stmt.setString(7, fournisseur.getVille());
            stmt.setString(8, fournisseur.getPays());
            stmt.setString(9, fournisseur.getCodePostal());
            stmt.setString(10, fournisseur.getSiteWeb());
            stmt.setString(11, fournisseur.getNumeroTVA());
            stmt.setString(12, fournisseur.getConditionsPaiement());
            stmt.setDouble(13, fournisseur.getNotePerforme());
            stmt.setTimestamp(14, Timestamp.valueOf(fournisseur.getDateCreation()));
            stmt.setBoolean(15, fournisseur.isActif());
            stmt.setString(16, fournisseur.getCommentaire());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fournisseur.setId(rs.getLong(1));
                }
            }

            return fournisseur;
        }
    }

    /**
     * Met Ã  jour un fournisseur existant
     */
    public void update(Fournisseur fournisseur) throws SQLException {
        String sql = "UPDATE fournisseurs SET code = ?, nom = ?, contact = ?, telephone = ?, email = ?, " +
                "adresse = ?, ville = ?, pays = ?, code_postal = ?, site_web = ?, numero_tva = ?, " +
                "conditions_paiement = ?, note_performe = ?, actif = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fournisseur.getCode());
            stmt.setString(2, fournisseur.getNom());
            stmt.setString(3, fournisseur.getContact());
            stmt.setString(4, fournisseur.getTelephone());
            stmt.setString(5, fournisseur.getEmail());
            stmt.setString(6, fournisseur.getAdresse());
            stmt.setString(7, fournisseur.getVille());
            stmt.setString(8, fournisseur.getPays());
            stmt.setString(9, fournisseur.getCodePostal());
            stmt.setString(10, fournisseur.getSiteWeb());
            stmt.setString(11, fournisseur.getNumeroTVA());
            stmt.setString(12, fournisseur.getConditionsPaiement());
            stmt.setDouble(13, fournisseur.getNotePerforme());
            stmt.setBoolean(14, fournisseur.isActif());
            stmt.setString(15, fournisseur.getCommentaire());
            stmt.setLong(16, fournisseur.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Supprime un fournisseur
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM fournisseurs WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche un fournisseur par ID
     */
    public Optional<Fournisseur> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM fournisseurs WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFournisseur(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recherche un fournisseur par code
     */
    public Optional<Fournisseur> findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM fournisseurs WHERE code = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFournisseur(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne tous les fournisseurs
     */
    public List<Fournisseur> findAll() throws SQLException {
        String sql = "SELECT * FROM fournisseurs ORDER BY nom";
        List<Fournisseur> fournisseurs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        }
        return fournisseurs;
    }

    /**
     * Retourne les fournisseurs actifs
     */
    public List<Fournisseur> findActive() throws SQLException {
        String sql = "SELECT * FROM fournisseurs WHERE actif = true ORDER BY nom";
        List<Fournisseur> fournisseurs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        }
        return fournisseurs;
    }

    /**
     * Recherche des fournisseurs par critÃ¨res
     */
    public List<Fournisseur> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM fournisseurs WHERE " +
                "(LOWER(nom) LIKE ? OR LOWER(code) LIKE ? OR LOWER(contact) LIKE ?) " +
                "ORDER BY nom";
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String searchPattern = "%" + keyword.toLowerCase() + "%";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fournisseurs.add(mapResultSetToFournisseur(rs));
                }
            }
        }
        return fournisseurs;
    }

    /**
     * Met Ã  jour la note de performance d'un fournisseur
     */
    public void updatePerformanceRating(Long id, double rating) throws SQLException {
        String sql = "UPDATE fournisseurs SET note_performe = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, rating);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Compte le nombre de fournisseurs
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM fournisseurs";
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
     * Convertit un ResultSet en objet Fournisseur
     */
    private Fournisseur mapResultSetToFournisseur(ResultSet rs) throws SQLException {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(rs.getLong("id"));
        fournisseur.setCode(rs.getString("code"));
        fournisseur.setNom(rs.getString("nom"));
        fournisseur.setContact(rs.getString("contact"));
        fournisseur.setTelephone(rs.getString("telephone"));
        fournisseur.setEmail(rs.getString("email"));
        fournisseur.setAdresse(rs.getString("adresse"));
        fournisseur.setVille(rs.getString("ville"));
        fournisseur.setPays(rs.getString("pays"));
        fournisseur.setCodePostal(rs.getString("code_postal"));
        fournisseur.setSiteWeb(rs.getString("site_web"));
        fournisseur.setNumeroTVA(rs.getString("numero_tva"));
        fournisseur.setConditionsPaiement(rs.getString("conditions_paiement"));
        fournisseur.setNotePerforme(rs.getDouble("note_performe"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            fournisseur.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        fournisseur.setActif(rs.getBoolean("actif"));
        fournisseur.setCommentaire(rs.getString("commentaire"));
        
        return fournisseur;
    }
}


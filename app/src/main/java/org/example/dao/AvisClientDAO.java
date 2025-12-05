package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.AvisClient;
import org.example.model.entity.CategorieAvis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des avis clients
 */
public class AvisClientDAO {

    private final DatabaseConnection dbConnection;

    public AvisClientDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final VenteDAO venteDAO = new VenteDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Sauvegarde un nouvel avis
     */
    public AvisClient save(AvisClient avis) throws SQLException {
        String sql = "INSERT INTO avis_clients (vente_id, client_id, note, commentaire, categorie, " +
                "date_avis, traite, reponse, reponse_par_user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (avis.getVente() != null) {
                stmt.setLong(1, avis.getVente().getId());
            } else {
                stmt.setNull(1, Types.BIGINT);
            }
            
            stmt.setLong(2, avis.getClient().getId());
            stmt.setInt(3, avis.getNote());
            stmt.setString(4, avis.getCommentaire());
            stmt.setString(5, avis.getCategorie().name());
            stmt.setTimestamp(6, Timestamp.valueOf(avis.getDateAvis()));
            stmt.setBoolean(7, avis.isTraite());
            stmt.setString(8, avis.getReponse());
            
            if (avis.getReponsePar() != null) {
                stmt.setLong(9, avis.getReponsePar().getId());
            } else {
                stmt.setNull(9, Types.BIGINT);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    avis.setId(rs.getLong(1));
                }
            }

            return avis;
        }
    }

    /**
     * Met Ã  jour un avis
     */
    public void update(AvisClient avis) throws SQLException {
        String sql = "UPDATE avis_clients SET traite = ?, reponse = ?, reponse_par_user_id = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, avis.isTraite());
            stmt.setString(2, avis.getReponse());
            
            if (avis.getReponsePar() != null) {
                stmt.setLong(3, avis.getReponsePar().getId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            
            stmt.setLong(4, avis.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Marque un avis comme traitÃ©
     */
    public void marquerTraite(Long id, String reponse, Long reponsePar) throws SQLException {
        String sql = "UPDATE avis_clients SET traite = true, reponse = ?, reponse_par_user_id = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reponse);
            stmt.setLong(2, reponsePar);
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Supprime un avis
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM avis_clients WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche un avis par ID
     */
    public Optional<AvisClient> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM avis_clients WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAvis(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne tous les avis
     */
    public List<AvisClient> findAll() throws SQLException {
        String sql = "SELECT * FROM avis_clients ORDER BY date_avis DESC";
        List<AvisClient> avisList = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                avisList.add(mapResultSetToAvis(rs));
            }
        }
        return avisList;
    }

    /**
     * Retourne les avis par client
     */
    public List<AvisClient> findByClient(Long clientId) throws SQLException {
        String sql = "SELECT * FROM avis_clients WHERE client_id = ? ORDER BY date_avis DESC";
        List<AvisClient> avisList = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avisList.add(mapResultSetToAvis(rs));
                }
            }
        }
        return avisList;
    }

    /**
     * Retourne les avis par catÃ©gorie
     */
    public List<AvisClient> findByCategorie(CategorieAvis categorie) throws SQLException {
        String sql = "SELECT * FROM avis_clients WHERE categorie = ? ORDER BY date_avis DESC";
        List<AvisClient> avisList = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categorie.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avisList.add(mapResultSetToAvis(rs));
                }
            }
        }
        return avisList;
    }

    /**
     * Retourne les avis par note
     */
    public List<AvisClient> findByNote(int note) throws SQLException {
        String sql = "SELECT * FROM avis_clients WHERE note = ? ORDER BY date_avis DESC";
        List<AvisClient> avisList = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, note);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avisList.add(mapResultSetToAvis(rs));
                }
            }
        }
        return avisList;
    }

    /**
     * Retourne les avis non traitÃ©s
     */
    public List<AvisClient> findNonTraites() throws SQLException {
        String sql = "SELECT * FROM avis_clients WHERE traite = false ORDER BY date_avis DESC";
        List<AvisClient> avisList = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                avisList.add(mapResultSetToAvis(rs));
            }
        }
        return avisList;
    }

    /**
     * Retourne les avis nÃ©gatifs (note <= 2)
     */
    public List<AvisClient> findNegatifs() throws SQLException {
        String sql = "SELECT * FROM avis_clients WHERE note <= 2 ORDER BY date_avis DESC";
        List<AvisClient> avisList = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                avisList.add(mapResultSetToAvis(rs));
            }
        }
        return avisList;
    }

    /**
     * Calcule la note moyenne
     */
    public double getMoyenneNotes() throws SQLException {
        String sql = "SELECT AVG(note) FROM avis_clients";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    /**
     * Calcule la note moyenne par catÃ©gorie
     */
    public double getMoyenneParCategorie(CategorieAvis categorie) throws SQLException {
        String sql = "SELECT AVG(note) FROM avis_clients WHERE categorie = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categorie.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    /**
     * Compte le nombre d'avis
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM avis_clients";
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
     * Convertit un ResultSet en AvisClient
     */
    private AvisClient mapResultSetToAvis(ResultSet rs) throws SQLException {
        AvisClient avis = new AvisClient();
        avis.setId(rs.getLong("id"));
        
        Long venteId = rs.getLong("vente_id");
        if (!rs.wasNull()) {
            avis.setVente(venteDAO.findById(venteId).orElse(null));
        }
        
        avis.setClient(clientDAO.findById(rs.getLong("client_id")).orElse(null));
        avis.setNote(rs.getInt("note"));
        avis.setCommentaire(rs.getString("commentaire"));
        avis.setCategorie(CategorieAvis.valueOf(rs.getString("categorie")));
        
        Timestamp dateAvis = rs.getTimestamp("date_avis");
        if (dateAvis != null) {
            avis.setDateAvis(dateAvis.toLocalDateTime());
        }
        
        avis.setTraite(rs.getBoolean("traite"));
        avis.setReponse(rs.getString("reponse"));
        
        Long reponseParId = rs.getLong("reponse_par_user_id");
        if (!rs.wasNull()) {
            avis.setReponsePar(utilisateurDAO.findById(reponseParId).orElse(null));
        }
        
        return avis;
    }
}


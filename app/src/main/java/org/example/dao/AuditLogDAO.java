package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.AuditLog;
import org.example.model.entity.TypeAction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditLogDAO {

    private final DatabaseConnection dbConnection;

    public AuditLogDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public AuditLog save(AuditLog log) throws SQLException {
        String sql = "INSERT INTO audit_log (date_heure, utilisateur_id, action, entite, entite_id, " +
                "description, valeur_avant, valeur_apres, adresse_ip, succes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, Timestamp.valueOf(log.getDateHeure()));
            
            if (log.getUtilisateur() != null) {
                stmt.setLong(2, log.getUtilisateur().getId());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            
            stmt.setString(3, log.getAction().name());
            stmt.setString(4, log.getEntite());
            stmt.setLong(5, log.getEntiteId());
            stmt.setString(6, log.getDescription());
            stmt.setString(7, log.getValeurAvant());
            stmt.setString(8, log.getValeurApres());
            stmt.setString(9, log.getAdresseIP());
            stmt.setBoolean(10, log.isSucces());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    log.setId(rs.getLong(1));
                }
            }

            return log;
        }
    }

    public void logAction(TypeAction action, String entite, Long entiteId, String description, 
                         Long utilisateurId, String adresseIp) throws SQLException {
        AuditLog log = new AuditLog();
        log.setDateHeure(java.time.LocalDateTime.now());
        
        if (utilisateurId != null) {
            utilisateurDAO.findById(utilisateurId).ifPresent(log::setUtilisateur);
        }
        
        log.setAction(action);
        log.setEntite(entite);
        log.setEntiteId(entiteId);
        log.setDescription(description);
        log.setAdresseIP(adresseIp);
        log.setSucces(true);
        
        save(log);
    }

    public void logModification(String entite, Long entiteId, String description, 
                               String valeurAvant, String valeurApres, 
                               Long utilisateurId, String adresseIp) throws SQLException {
        AuditLog log = new AuditLog();
        log.setDateHeure(java.time.LocalDateTime.now());
        
        if (utilisateurId != null) {
            utilisateurDAO.findById(utilisateurId).ifPresent(log::setUtilisateur);
        }
        
        log.setAction(TypeAction.MODIFICATION);
        log.setEntite(entite);
        log.setEntiteId(entiteId);
        log.setDescription(description);
        log.setValeurAvant(valeurAvant);
        log.setValeurApres(valeurApres);
        log.setAdresseIP(adresseIp);
        log.setSucces(true);
        
        save(log);
    }

    public Optional<AuditLog> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAuditLog(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<AuditLog> findAll() throws SQLException {
        String sql = "SELECT * FROM audit_log ORDER BY date_heure DESC LIMIT 1000";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }
        }
        return logs;
    }

    public List<AuditLog> findByUtilisateur(Long utilisateurId) throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE utilisateur_id = ? ORDER BY date_heure DESC LIMIT 500";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
        }
        return logs;
    }

    public List<AuditLog> findByAction(TypeAction action) throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE action = ? ORDER BY date_heure DESC LIMIT 500";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
        }
        return logs;
    }

    public List<AuditLog> findByEntite(String entite, Long entiteId) throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE entite = ? AND entite_id = ? ORDER BY date_heure DESC";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entite);
            stmt.setLong(2, entiteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
        }
        return logs;
    }

    public List<AuditLog> findByPeriode(java.time.LocalDateTime debut, java.time.LocalDateTime fin) throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE date_heure BETWEEN ? AND ? ORDER BY date_heure DESC";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
        }
        return logs;
    }

    public List<AuditLog> findEchecs() throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE succes = false ORDER BY date_heure DESC LIMIT 500";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }
        }
        return logs;
    }

    public List<AuditLog> findConnexions() throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE action = 'CONNEXION' ORDER BY date_heure DESC LIMIT 500";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }
        }
        return logs;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM audit_log";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int deleteOlderThan(int jours) throws SQLException {
        String sql = "DELETE FROM audit_log WHERE date_heure < DATEADD('DAY', ?, CURRENT_TIMESTAMP)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, -jours);
            return stmt.executeUpdate();
        }
    }

    private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getLong("id"));
        
        Timestamp dateHeure = rs.getTimestamp("date_heure");
        if (dateHeure != null) {
            log.setDateHeure(dateHeure.toLocalDateTime());
        }
        
        Long utilisateurId = rs.getLong("utilisateur_id");
        if (!rs.wasNull()) {
            log.setUtilisateur(utilisateurDAO.findById(utilisateurId).orElse(null));
        }
        
        log.setAction(TypeAction.valueOf(rs.getString("action")));
        log.setEntite(rs.getString("entite"));
        log.setEntiteId(rs.getLong("entite_id"));
        log.setDescription(rs.getString("description"));
        log.setValeurAvant(rs.getString("valeur_avant"));
        log.setValeurApres(rs.getString("valeur_apres"));
        log.setAdresseIP(rs.getString("adresse_ip"));
        log.setSucces(rs.getBoolean("succes"));
        
        return log;
    }
}


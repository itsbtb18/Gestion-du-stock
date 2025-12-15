package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Notification;
import org.example.model.entity.TypeNotification;
import org.example.model.entity.PrioriteNotification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationDAO {

    private final DatabaseConnection dbConnection;

    public NotificationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Notification save(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (titre, message, type, priorite, date_creation, " +
                "date_envoi, destinataire_id, lu, date_lecture, lien_action, envoi_email, envoi_sms, " +
                "entite_related, entite_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, notification.getTitre());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getType().name());
            stmt.setString(4, notification.getPriorite().name());
            stmt.setTimestamp(5, Timestamp.valueOf(notification.getDateCreation()));
            
            if (notification.getDateEnvoi() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(notification.getDateEnvoi()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            
            stmt.setLong(7, notification.getDestinataire().getId());
            stmt.setBoolean(8, notification.isLu());
            
            if (notification.getDateLecture() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(notification.getDateLecture()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }
            
            stmt.setString(10, notification.getLienAction());
            stmt.setBoolean(11, notification.isEnvoiEmail());
            stmt.setBoolean(12, notification.isEnvoiSMS());
            stmt.setString(13, notification.getEntiteRelated());
            
            if (notification.getEntiteId() != null) {
                stmt.setLong(14, notification.getEntiteId());
            } else {
                stmt.setNull(14, Types.BIGINT);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    notification.setId(rs.getLong(1));
                }
            }

            return notification;
        }
    }

    public void update(Notification notification) throws SQLException {
        String sql = "UPDATE notifications SET date_envoi = ?, lu = ?, date_lecture = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (notification.getDateEnvoi() != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(notification.getDateEnvoi()));
            } else {
                stmt.setNull(1, Types.TIMESTAMP);
            }
            
            stmt.setBoolean(2, notification.isLu());
            
            if (notification.getDateLecture() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(notification.getDateLecture()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }
            
            stmt.setLong(4, notification.getId());

            stmt.executeUpdate();
        }
    }

    public void marquerLue(Long id) throws SQLException {
        String sql = "UPDATE notifications SET lu = true, date_lecture = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void marquerToutesLues(Long destinataireId) throws SQLException {
        String sql = "UPDATE notifications SET lu = true, date_lecture = ? WHERE destinataire_id = ? AND lu = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setLong(2, destinataireId);
            stmt.executeUpdate();
        }
    }

    public void marquerEnvoyee(Long id) throws SQLException {
        String sql = "UPDATE notifications SET date_envoi = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Optional<Notification> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToNotification(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Notification> findByDestinataire(Long destinataireId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE destinataire_id = ? ORDER BY date_creation DESC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, destinataireId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    public List<Notification> findNonLues(Long destinataireId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE destinataire_id = ? AND lu = false " +
                "ORDER BY priorite DESC, date_creation DESC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, destinataireId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    public List<Notification> findByType(TypeNotification type) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE type = ? ORDER BY date_creation DESC LIMIT 500";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    public List<Notification> findByPriorite(PrioriteNotification priorite) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE priorite = ? ORDER BY date_creation DESC LIMIT 500";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, priorite.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    public List<Notification> findAEnvoyerParEmail() throws SQLException {
        String sql = "SELECT * FROM notifications WHERE envoi_email = true AND date_envoi IS NULL " +
                "ORDER BY priorite DESC, date_creation ASC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        }
        return notifications;
    }

    public List<Notification> findUrgentesNonLues(Long destinataireId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE destinataire_id = ? AND priorite = 'URGENTE' " +
                "AND lu = false ORDER BY date_creation DESC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, destinataireId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    public int countNonLues(Long destinataireId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE destinataire_id = ? AND lu = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, destinataireId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int deleteAnciennesLues(int jours) throws SQLException {
        String sql = "DELETE FROM notifications WHERE lu = true AND " +
                "date_lecture < DATEADD('DAY', ?, CURRENT_TIMESTAMP)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, -jours);
            return stmt.executeUpdate();
        }
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getLong("id"));
        notification.setTitre(rs.getString("titre"));
        notification.setMessage(rs.getString("message"));
        notification.setType(TypeNotification.valueOf(rs.getString("type")));
        notification.setPriorite(PrioriteNotification.valueOf(rs.getString("priorite")));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            notification.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
        if (dateEnvoi != null) {
            notification.setDateEnvoi(dateEnvoi.toLocalDateTime());
        }
        
        notification.setDestinataire(utilisateurDAO.findById(rs.getLong("destinataire_id")).orElse(null));
        notification.setLu(rs.getBoolean("lu"));
        
        Timestamp dateLecture = rs.getTimestamp("date_lecture");
        if (dateLecture != null) {
            notification.setDateLecture(dateLecture.toLocalDateTime());
        }
        
        notification.setLienAction(rs.getString("lien_action"));
        notification.setEnvoiEmail(rs.getBoolean("envoi_email"));
        notification.setEnvoiSMS(rs.getBoolean("envoi_sms"));
        notification.setEntiteRelated(rs.getString("entite_related"));
        
        Long entiteId = rs.getLong("entite_id");
        if (!rs.wasNull()) {
            notification.setEntiteId(entiteId);
        }
        
        return notification;
    }
}


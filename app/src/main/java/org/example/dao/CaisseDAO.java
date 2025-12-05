package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Caisse;
import org.example.model.entity.StatutCaisse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des caisses (sessions de caisse)
 */
public class CaisseDAO {

    private final DatabaseConnection dbConnection;

    public CaisseDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Ouvre une nouvelle session de caisse
     */
    public Caisse save(Caisse caisse) throws SQLException {
        String sql = "INSERT INTO caisses (numero_caisse, caissier_id, date_ouverture, date_fermeture, " +
                "solde_depart_especes, solde_fin_especes, total_ventes_especes, total_ventes_carte, " +
                "total_ventes_autre, total_depenses, ecart, statut, commentaire) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, caisse.getNumeroCaisse());
            stmt.setLong(2, caisse.getCaissier().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(caisse.getDateOuverture()));
            
            if (caisse.getDateFermeture() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(caisse.getDateFermeture()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            stmt.setDouble(5, caisse.getSoldeDepartEspeces());
            stmt.setDouble(6, caisse.getSoldeFinEspeces());
            stmt.setDouble(7, caisse.getTotalVentesEspeces());
            stmt.setDouble(8, caisse.getTotalVentesCarte());
            stmt.setDouble(9, caisse.getTotalVentesAutre());
            stmt.setDouble(10, caisse.getTotalDepenses());
            stmt.setDouble(11, caisse.getEcart());
            stmt.setString(12, caisse.getStatut().name());
            stmt.setString(13, caisse.getCommentaire());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    caisse.setId(rs.getLong(1));
                }
            }

            return caisse;
        }
    }

    /**
     * Met Ã  jour une session de caisse
     */
    public void update(Caisse caisse) throws SQLException {
        String sql = "UPDATE caisses SET date_fermeture = ?, solde_fin_especes = ?, " +
                "total_ventes_especes = ?, total_ventes_carte = ?, total_ventes_autre = ?, " +
                "total_depenses = ?, ecart = ?, statut = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (caisse.getDateFermeture() != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(caisse.getDateFermeture()));
            } else {
                stmt.setNull(1, Types.TIMESTAMP);
            }
            
            stmt.setDouble(2, caisse.getSoldeFinEspeces());
            stmt.setDouble(3, caisse.getTotalVentesEspeces());
            stmt.setDouble(4, caisse.getTotalVentesCarte());
            stmt.setDouble(5, caisse.getTotalVentesAutre());
            stmt.setDouble(6, caisse.getTotalDepenses());
            stmt.setDouble(7, caisse.getEcart());
            stmt.setString(8, caisse.getStatut().name());
            stmt.setString(9, caisse.getCommentaire());
            stmt.setLong(10, caisse.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Ferme une session de caisse
     */
    public void fermerCaisse(Long id, double soldeFinEspeces, String commentaire) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Caisse caisse = mapResultSetToCaisse(rs);
                    caisse.fermerCaisse(soldeFinEspeces);
                    caisse.setCommentaire(commentaire);
                    update(caisse);
                }
            }
        }
    }

    /**
     * Met Ã  jour le statut d'une caisse
     */
    public void updateStatut(Long id, StatutCaisse statut) throws SQLException {
        String sql = "UPDATE caisses SET statut = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche une caisse par ID
     */
    public Optional<Caisse> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCaisse(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recherche une caisse par numÃ©ro
     */
    public Optional<Caisse> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE numero_caisse = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCaisse(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne la caisse actuellement ouverte pour un caissier
     */
    public Optional<Caisse> findCaisseOuverte(Long caissierId) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE caissier_id = ? AND statut = 'OUVERTE' " +
                "ORDER BY date_ouverture DESC LIMIT 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, caissierId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCaisse(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne toutes les caisses
     */
    public List<Caisse> findAll() throws SQLException {
        String sql = "SELECT * FROM caisses ORDER BY date_ouverture DESC";
        List<Caisse> caisses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                caisses.add(mapResultSetToCaisse(rs));
            }
        }
        return caisses;
    }

    /**
     * Retourne les caisses par caissier
     */
    public List<Caisse> findByCaissier(Long caissierId) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE caissier_id = ? ORDER BY date_ouverture DESC";
        List<Caisse> caisses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, caissierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caisses.add(mapResultSetToCaisse(rs));
                }
            }
        }
        return caisses;
    }

    /**
     * Retourne les caisses par statut
     */
    public List<Caisse> findByStatut(StatutCaisse statut) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE statut = ? ORDER BY date_ouverture DESC";
        List<Caisse> caisses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caisses.add(mapResultSetToCaisse(rs));
                }
            }
        }
        return caisses;
    }

    /**
     * Retourne les caisses par pÃ©riode
     */
    public List<Caisse> findByPeriode(java.time.LocalDate debut, java.time.LocalDate fin) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE DATE(date_ouverture) BETWEEN ? AND ? " +
                "ORDER BY date_ouverture DESC";
        List<Caisse> caisses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caisses.add(mapResultSetToCaisse(rs));
                }
            }
        }
        return caisses;
    }

    /**
     * Supprime une caisse
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM caisses WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Convertit un ResultSet en Caisse
     */
    private Caisse mapResultSetToCaisse(ResultSet rs) throws SQLException {
        Caisse caisse = new Caisse();
        caisse.setId(rs.getLong("id"));
        caisse.setNumeroCaisse(rs.getString("numero_caisse"));
        caisse.setCaissier(utilisateurDAO.findById(rs.getLong("caissier_id")).orElse(null));
        
        Timestamp dateOuverture = rs.getTimestamp("date_ouverture");
        if (dateOuverture != null) {
            caisse.setDateOuverture(dateOuverture.toLocalDateTime());
        }
        
        Timestamp dateFermeture = rs.getTimestamp("date_fermeture");
        if (dateFermeture != null) {
            caisse.setDateFermeture(dateFermeture.toLocalDateTime());
        }
        
        caisse.setSoldeDepartEspeces(rs.getDouble("solde_depart_especes"));
        caisse.setSoldeFinEspeces(rs.getDouble("solde_fin_especes"));
        caisse.setTotalVentesEspeces(rs.getDouble("total_ventes_especes"));
        caisse.setTotalVentesCarte(rs.getDouble("total_ventes_carte"));
        caisse.setTotalVentesAutre(rs.getDouble("total_ventes_autre"));
        caisse.setTotalDepenses(rs.getDouble("total_depenses"));
        caisse.setEcart(rs.getDouble("ecart"));
        caisse.setStatut(StatutCaisse.valueOf(rs.getString("statut")));
        caisse.setCommentaire(rs.getString("commentaire"));
        
        return caisse;
    }
}


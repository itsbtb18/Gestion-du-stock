package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Lot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des lots de produits
 */
public class LotDAO {

    private final DatabaseConnection dbConnection;

    public LotDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final ProduitDAO produitDAO = new ProduitDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    /**
     * Sauvegarde un nouveau lot
     */
    public Lot save(Lot lot) throws SQLException {
        String sql = "INSERT INTO lots (numero_lot, produit_id, quantite, date_fabrication, " +
                "date_expiration, fournisseur_id, bon_commande_id, emplacement, actif, commentaire) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, lot.getNumeroLot());
            stmt.setLong(2, lot.getProduit().getId());
            stmt.setInt(3, lot.getQuantite());
            
            if (lot.getDateFabrication() != null) {
                stmt.setDate(4, Date.valueOf(lot.getDateFabrication()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            if (lot.getDateExpiration() != null) {
                stmt.setDate(5, Date.valueOf(lot.getDateExpiration()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            if (lot.getFournisseur() != null) {
                stmt.setLong(6, lot.getFournisseur().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            if (lot.getBonCommande() != null) {
                stmt.setLong(7, lot.getBonCommande().getId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }
            
            stmt.setString(8, lot.getEmplacement());
            stmt.setBoolean(9, lot.isActif());
            stmt.setString(10, lot.getCommentaire());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    lot.setId(rs.getLong(1));
                }
            }

            return lot;
        }
    }

    /**
     * Met Ã  jour un lot
     */
    public void update(Lot lot) throws SQLException {
        String sql = "UPDATE lots SET quantite = ?, date_fabrication = ?, date_expiration = ?, " +
                "emplacement = ?, actif = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lot.getQuantite());
            
            if (lot.getDateFabrication() != null) {
                stmt.setDate(2, Date.valueOf(lot.getDateFabrication()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            if (lot.getDateExpiration() != null) {
                stmt.setDate(3, Date.valueOf(lot.getDateExpiration()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            stmt.setString(4, lot.getEmplacement());
            stmt.setBoolean(5, lot.isActif());
            stmt.setString(6, lot.getCommentaire());
            stmt.setLong(7, lot.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Met Ã  jour la quantitÃ© d'un lot
     */
    public void updateQuantite(Long id, int quantite) throws SQLException {
        String sql = "UPDATE lots SET quantite = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Supprime un lot
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM lots WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche un lot par ID
     */
    public Optional<Lot> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM lots WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLot(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recherche un lot par numÃ©ro
     */
    public Optional<Lot> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM lots WHERE numero_lot = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLot(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne tous les lots
     */
    public List<Lot> findAll() throws SQLException {
        String sql = "SELECT * FROM lots ORDER BY date_expiration ASC";
        List<Lot> lots = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lots.add(mapResultSetToLot(rs));
            }
        }
        return lots;
    }

    /**
     * Retourne les lots actifs
     */
    public List<Lot> findActive() throws SQLException {
        String sql = "SELECT * FROM lots WHERE actif = true ORDER BY date_expiration ASC";
        List<Lot> lots = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lots.add(mapResultSetToLot(rs));
            }
        }
        return lots;
    }

    /**
     * Retourne les lots par produit
     */
    public List<Lot> findByProduit(Long produitId) throws SQLException {
        String sql = "SELECT * FROM lots WHERE produit_id = ? ORDER BY date_expiration ASC";
        List<Lot> lots = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, produitId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lots.add(mapResultSetToLot(rs));
                }
            }
        }
        return lots;
    }

    /**
     * Retourne les lots expirÃ©s
     */
    public List<Lot> findExpires() throws SQLException {
        String sql = "SELECT * FROM lots WHERE date_expiration < CURRENT_DATE AND actif = true " +
                "ORDER BY date_expiration ASC";
        List<Lot> lots = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lots.add(mapResultSetToLot(rs));
            }
        }
        return lots;
    }

    /**
     * Retourne les lots qui expirent bientÃ´t
     */
    public List<Lot> findExpirantBientot(int joursAvance) throws SQLException {
        String sql = "SELECT * FROM lots WHERE date_expiration BETWEEN CURRENT_DATE AND " +
                "DATEADD('DAY', ?, CURRENT_DATE) AND actif = true ORDER BY date_expiration ASC";
        List<Lot> lots = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, joursAvance);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lots.add(mapResultSetToLot(rs));
                }
            }
        }
        return lots;
    }

    /**
     * Retourne les lots par emplacement
     */
    public List<Lot> findByEmplacement(String emplacement) throws SQLException {
        String sql = "SELECT * FROM lots WHERE emplacement = ? ORDER BY date_expiration ASC";
        List<Lot> lots = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, emplacement);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lots.add(mapResultSetToLot(rs));
                }
            }
        }
        return lots;
    }

    /**
     * Convertit un ResultSet en Lot
     */
    private Lot mapResultSetToLot(ResultSet rs) throws SQLException {
        Lot lot = new Lot();
        lot.setId(rs.getLong("id"));
        lot.setNumeroLot(rs.getString("numero_lot"));
        lot.setProduit(produitDAO.findById(rs.getLong("produit_id")).orElse(null));
        lot.setQuantite(rs.getInt("quantite"));
        
        Date dateFabrication = rs.getDate("date_fabrication");
        if (dateFabrication != null) {
            lot.setDateFabrication(dateFabrication.toLocalDate());
        }
        
        Date dateExpiration = rs.getDate("date_expiration");
        if (dateExpiration != null) {
            lot.setDateExpiration(dateExpiration.toLocalDate());
        }
        
        Long fournisseurId = rs.getLong("fournisseur_id");
        if (!rs.wasNull()) {
            lot.setFournisseur(fournisseurDAO.findById(fournisseurId).orElse(null));
        }
        
        // Note: BonCommande relation would require BonCommandeDAO
        // For now, just storing the ID if needed
        
        lot.setEmplacement(rs.getString("emplacement"));
        lot.setActif(rs.getBoolean("actif"));
        lot.setCommentaire(rs.getString("commentaire"));
        
        return lot;
    }
}


package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.TransfertStock;
import org.example.model.entity.StatutTransfert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransfertStockDAO {

    private final DatabaseConnection dbConnection;

    public TransfertStockDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final EmplacementDAO emplacementDAO = new EmplacementDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final LotDAO lotDAO = new LotDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public TransfertStock save(TransfertStock transfert) throws SQLException {
        String sql = "INSERT INTO transferts_stock (numero_transfert, date_transfert, " +
                "emplacement_source_id, emplacement_destination_id, produit_id, lot_id, quantite, " +
                "demande_par_user_id, valide_par_user_id, statut, motif, commentaire) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, transfert.getNumeroTransfert());
            stmt.setTimestamp(2, Timestamp.valueOf(transfert.getDateTransfert()));
            stmt.setLong(3, transfert.getEmplacementSource().getId());
            stmt.setLong(4, transfert.getEmplacementDestination().getId());
            stmt.setLong(5, transfert.getProduit().getId());
            
            if (transfert.getLot() != null) {
                stmt.setLong(6, transfert.getLot().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setInt(7, transfert.getQuantite());
            stmt.setLong(8, transfert.getDemandePar().getId());
            
            if (transfert.getValidePar() != null) {
                stmt.setLong(9, transfert.getValidePar().getId());
            } else {
                stmt.setNull(9, Types.BIGINT);
            }
            
            stmt.setString(10, transfert.getStatut().name());
            stmt.setString(11, transfert.getMotif());
            stmt.setString(12, transfert.getCommentaire());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    transfert.setId(rs.getLong(1));
                }
            }

            return transfert;
        }
    }

    public void update(TransfertStock transfert) throws SQLException {
        String sql = "UPDATE transferts_stock SET statut = ?, valide_par_user_id = ?, " +
                "motif = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transfert.getStatut().name());
            
            if (transfert.getValidePar() != null) {
                stmt.setLong(2, transfert.getValidePar().getId());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            
            stmt.setString(3, transfert.getMotif());
            stmt.setString(4, transfert.getCommentaire());
            stmt.setLong(5, transfert.getId());

            stmt.executeUpdate();
        }
    }

    public void updateStatut(Long id, StatutTransfert statut, Long validePar) throws SQLException {
        String sql = "UPDATE transferts_stock SET statut = ?, valide_par_user_id = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            
            if (validePar != null) {
                stmt.setLong(2, validePar);
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM transferts_stock WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Optional<TransfertStock> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM transferts_stock WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransfert(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<TransfertStock> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM transferts_stock WHERE numero_transfert = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransfert(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<TransfertStock> findAll() throws SQLException {
        String sql = "SELECT * FROM transferts_stock ORDER BY date_transfert DESC";
        List<TransfertStock> transferts = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transferts.add(mapResultSetToTransfert(rs));
            }
        }
        return transferts;
    }

    public List<TransfertStock> findByStatut(StatutTransfert statut) throws SQLException {
        String sql = "SELECT * FROM transferts_stock WHERE statut = ? ORDER BY date_transfert DESC";
        List<TransfertStock> transferts = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transferts.add(mapResultSetToTransfert(rs));
                }
            }
        }
        return transferts;
    }

    public List<TransfertStock> findByEmplacementSource(Long emplacementId) throws SQLException {
        String sql = "SELECT * FROM transferts_stock WHERE emplacement_source_id = ? " +
                "ORDER BY date_transfert DESC";
        List<TransfertStock> transferts = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, emplacementId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transferts.add(mapResultSetToTransfert(rs));
                }
            }
        }
        return transferts;
    }

    public List<TransfertStock> findByEmplacementDestination(Long emplacementId) throws SQLException {
        String sql = "SELECT * FROM transferts_stock WHERE emplacement_destination_id = ? " +
                "ORDER BY date_transfert DESC";
        List<TransfertStock> transferts = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, emplacementId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transferts.add(mapResultSetToTransfert(rs));
                }
            }
        }
        return transferts;
    }

    public List<TransfertStock> findByProduit(Long produitId) throws SQLException {
        String sql = "SELECT * FROM transferts_stock WHERE produit_id = ? ORDER BY date_transfert DESC";
        List<TransfertStock> transferts = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, produitId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transferts.add(mapResultSetToTransfert(rs));
                }
            }
        }
        return transferts;
    }

    public List<TransfertStock> findEnAttente() throws SQLException {
        return findByStatut(StatutTransfert.EN_ATTENTE);
    }

    private TransfertStock mapResultSetToTransfert(ResultSet rs) throws SQLException {
        TransfertStock transfert = new TransfertStock();
        transfert.setId(rs.getLong("id"));
        transfert.setNumeroTransfert(rs.getString("numero_transfert"));
        
        Timestamp dateTransfert = rs.getTimestamp("date_transfert");
        if (dateTransfert != null) {
            transfert.setDateTransfert(dateTransfert.toLocalDateTime());
        }
        
        transfert.setEmplacementSource(emplacementDAO.findById(rs.getLong("emplacement_source_id")).orElse(null));
        transfert.setEmplacementDestination(emplacementDAO.findById(rs.getLong("emplacement_destination_id")).orElse(null));
        transfert.setProduit(produitDAO.findById(rs.getLong("produit_id")).orElse(null));
        
        Long lotId = rs.getLong("lot_id");
        if (!rs.wasNull()) {
            transfert.setLot(lotDAO.findById(lotId).orElse(null));
        }
        
        transfert.setQuantite(rs.getInt("quantite"));
        transfert.setDemandePar(utilisateurDAO.findById(rs.getLong("demande_par_user_id")).orElse(null));
        
        Long valideParId = rs.getLong("valide_par_user_id");
        if (!rs.wasNull()) {
            transfert.setValidePar(utilisateurDAO.findById(valideParId).orElse(null));
        }
        
        transfert.setStatut(StatutTransfert.valueOf(rs.getString("statut")));
        transfert.setMotif(rs.getString("motif"));
        transfert.setCommentaire(rs.getString("commentaire"));
        
        return transfert;
    }
}


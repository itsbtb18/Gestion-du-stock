package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Retour;
import org.example.model.entity.LigneRetour;
import org.example.model.entity.TypeRetour;
import org.example.model.entity.StatutRetour;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RetourDAO {

    private final DatabaseConnection dbConnection;

    public RetourDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final VenteDAO venteDAO = new VenteDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();

    public Retour save(Retour retour) throws SQLException {
        String sql = "INSERT INTO retours (numero_retour, date_retour, vente_originale_id, client_id, " +
                "traite_par_user_id, motif, type_retour, statut, montant_total, montant_rembourse, " +
                "mode_paiement, numero_credit_note, commentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, retour.getNumeroRetour());
                stmt.setTimestamp(2, Timestamp.valueOf(retour.getDateRetour()));
                stmt.setLong(3, retour.getVenteOriginale().getId());
                stmt.setLong(4, retour.getClient().getId());
                stmt.setLong(5, retour.getTraiteParUser().getId());
                stmt.setString(6, retour.getMotif());
                stmt.setString(7, retour.getTypeRetour().name());
                stmt.setString(8, retour.getStatut().name());
                stmt.setDouble(9, retour.getMontantTotal());
                stmt.setDouble(10, retour.getMontantRembourse());
                stmt.setString(11, retour.getModePaiement());
                stmt.setString(12, retour.getNumeroCreditNote());
                stmt.setString(13, retour.getCommentaire());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        retour.setId(rs.getLong(1));
                    }
                }
            }

            saveLignes(conn, retour);

            conn.commit();
            return retour;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private void saveLignes(Connection conn, Retour retour) throws SQLException {
        String sql = "INSERT INTO lignes_retour (retour_id, produit_id, quantite_retournee, " +
                "quantite_originale, prix_unitaire, montant_ligne, raison_retour, produit_endommage, " +
                "commentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (LigneRetour ligne : retour.getLignes()) {
                stmt.setLong(1, retour.getId());
                stmt.setLong(2, ligne.getProduit().getId());
                stmt.setInt(3, ligne.getQuantiteRetournee());
                stmt.setInt(4, ligne.getQuantiteOriginale());
                stmt.setDouble(5, ligne.getPrixUnitaire());
                stmt.setDouble(6, ligne.getMontantLigne());
                stmt.setString(7, ligne.getRaisonRetour());
                stmt.setBoolean(8, ligne.isProduitEndommage());
                stmt.setString(9, ligne.getCommentaire());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void update(Retour retour) throws SQLException {
        String sql = "UPDATE retours SET statut = ?, montant_rembourse = ?, mode_paiement = ?, " +
                "numero_credit_note = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, retour.getStatut().name());
            stmt.setDouble(2, retour.getMontantRembourse());
            stmt.setString(3, retour.getModePaiement());
            stmt.setString(4, retour.getNumeroCreditNote());
            stmt.setString(5, retour.getCommentaire());
            stmt.setLong(6, retour.getId());

            stmt.executeUpdate();
        }
    }

    public void updateStatut(Long id, StatutRetour statut) throws SQLException {
        String sql = "UPDATE retours SET statut = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public Optional<Retour> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM retours WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Retour retour = mapResultSetToRetour(rs);
                    retour.setLignes(findLignesByRetourId(retour.getId()));
                    return Optional.of(retour);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Retour> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM retours WHERE numero_retour = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Retour retour = mapResultSetToRetour(rs);
                    retour.setLignes(findLignesByRetourId(retour.getId()));
                    return Optional.of(retour);
                }
            }
        }
        return Optional.empty();
    }

    private List<LigneRetour> findLignesByRetourId(Long retourId) throws SQLException {
        String sql = "SELECT * FROM lignes_retour WHERE retour_id = ?";
        List<LigneRetour> lignes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, retourId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lignes.add(mapResultSetToLigne(rs));
                }
            }
        }
        return lignes;
    }

    public List<Retour> findAll() throws SQLException {
        String sql = "SELECT * FROM retours ORDER BY date_retour DESC";
        List<Retour> retours = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Retour retour = mapResultSetToRetour(rs);
                retour.setLignes(findLignesByRetourId(retour.getId()));
                retours.add(retour);
            }
        }
        return retours;
    }

    public List<Retour> findByClient(Long clientId) throws SQLException {
        String sql = "SELECT * FROM retours WHERE client_id = ? ORDER BY date_retour DESC";
        List<Retour> retours = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Retour retour = mapResultSetToRetour(rs);
                    retour.setLignes(findLignesByRetourId(retour.getId()));
                    retours.add(retour);
                }
            }
        }
        return retours;
    }

    public List<Retour> findByStatut(StatutRetour statut) throws SQLException {
        String sql = "SELECT * FROM retours WHERE statut = ? ORDER BY date_retour DESC";
        List<Retour> retours = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Retour retour = mapResultSetToRetour(rs);
                    retour.setLignes(findLignesByRetourId(retour.getId()));
                    retours.add(retour);
                }
            }
        }
        return retours;
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM retours WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Retour mapResultSetToRetour(ResultSet rs) throws SQLException {
        Retour retour = new Retour();
        retour.setId(rs.getLong("id"));
        retour.setNumeroRetour(rs.getString("numero_retour"));
        
        Timestamp dateRetour = rs.getTimestamp("date_retour");
        if (dateRetour != null) {
            retour.setDateRetour(dateRetour.toLocalDateTime());
        }
        
        retour.setVenteOriginale(venteDAO.findById(rs.getLong("vente_originale_id")).orElse(null));
        retour.setClient(clientDAO.findById(rs.getLong("client_id")).orElse(null));
        retour.setTraiteParUser(utilisateurDAO.findById(rs.getLong("traite_par_user_id")).orElse(null));
        retour.setMotif(rs.getString("motif"));
        retour.setTypeRetour(TypeRetour.valueOf(rs.getString("type_retour")));
        retour.setStatut(StatutRetour.valueOf(rs.getString("statut")));
        retour.setMontantTotal(rs.getDouble("montant_total"));
        retour.setMontantRembourse(rs.getDouble("montant_rembourse"));
        retour.setModePaiement(rs.getString("mode_paiement"));
        retour.setNumeroCreditNote(rs.getString("numero_credit_note"));
        retour.setCommentaire(rs.getString("commentaire"));
        
        return retour;
    }

    private LigneRetour mapResultSetToLigne(ResultSet rs) throws SQLException {
        LigneRetour ligne = new LigneRetour();
        ligne.setId(rs.getLong("id"));
        ligne.setProduit(produitDAO.findById(rs.getLong("produit_id")).orElse(null));
        ligne.setQuantiteRetournee(rs.getInt("quantite_retournee"));
        ligne.setQuantiteOriginale(rs.getInt("quantite_originale"));
        ligne.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        ligne.setMontantLigne(rs.getDouble("montant_ligne"));
        ligne.setRaisonRetour(rs.getString("raison_retour"));
        ligne.setProduitEndommage(rs.getBoolean("produit_endommage"));
        ligne.setCommentaire(rs.getString("commentaire"));
        return ligne;
    }
}


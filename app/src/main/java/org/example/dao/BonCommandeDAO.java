package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.BonCommande;
import org.example.model.entity.LigneBonCommande;
import org.example.model.entity.StatutCommande;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des bons de commande
 */
public class BonCommandeDAO {

    private final DatabaseConnection dbConnection;

    public BonCommandeDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();

    /**
     * Sauvegarde un nouveau bon de commande avec ses lignes
     */
    public BonCommande save(BonCommande bonCommande) throws SQLException {
        String sql = "INSERT INTO bons_commande (numero, date_commande, date_livraison_prevue, " +
                "date_livraison_reelle, fournisseur_id, commande_par_user_id, statut, montant_total, " +
                "montant_paye, mode_paiement, commentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, bonCommande.getNumero());
                stmt.setTimestamp(2, Timestamp.valueOf(bonCommande.getDateCommande()));
                stmt.setDate(3, bonCommande.getDateLivraisonPrevue() != null ? 
                        Date.valueOf(bonCommande.getDateLivraisonPrevue()) : null);
                stmt.setDate(4, bonCommande.getDateLivraisonReelle() != null ? 
                        Date.valueOf(bonCommande.getDateLivraisonReelle()) : null);
                stmt.setLong(5, bonCommande.getFournisseur().getId());
                stmt.setLong(6, bonCommande.getCommandeParUser().getId());
                stmt.setString(7, bonCommande.getStatut().name());
                stmt.setDouble(8, bonCommande.getMontantTotal());
                stmt.setDouble(9, bonCommande.getMontantPaye());
                stmt.setString(10, bonCommande.getModePaiement());
                stmt.setString(11, bonCommande.getCommentaire());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bonCommande.setId(rs.getLong(1));
                    }
                }
            }

            // Sauvegarder les lignes
            saveLignes(conn, bonCommande);

            conn.commit();
            return bonCommande;

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

    /**
     * Sauvegarde les lignes d'un bon de commande
     */
    private void saveLignes(Connection conn, BonCommande bonCommande) throws SQLException {
        String sql = "INSERT INTO lignes_bon_commande (bon_commande_id, produit_id, quantite_commandee, " +
                "quantite_recue, prix_unitaire, montant_ligne, commentaire) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (LigneBonCommande ligne : bonCommande.getLignes()) {
                stmt.setLong(1, bonCommande.getId());
                stmt.setLong(2, ligne.getProduit().getId());
                stmt.setInt(3, ligne.getQuantiteCommandee());
                stmt.setInt(4, ligne.getQuantiteRecue());
                stmt.setDouble(5, ligne.getPrixUnitaire());
                stmt.setDouble(6, ligne.getMontantLigne());
                stmt.setString(7, ligne.getCommentaire());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Met Ã  jour un bon de commande
     */
    public void update(BonCommande bonCommande) throws SQLException {
        String sql = "UPDATE bons_commande SET date_livraison_prevue = ?, date_livraison_reelle = ?, " +
                "statut = ?, montant_total = ?, montant_paye = ?, mode_paiement = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, bonCommande.getDateLivraisonPrevue() != null ? 
                    Date.valueOf(bonCommande.getDateLivraisonPrevue()) : null);
            stmt.setDate(2, bonCommande.getDateLivraisonReelle() != null ? 
                    Date.valueOf(bonCommande.getDateLivraisonReelle()) : null);
            stmt.setString(3, bonCommande.getStatut().name());
            stmt.setDouble(4, bonCommande.getMontantTotal());
            stmt.setDouble(5, bonCommande.getMontantPaye());
            stmt.setString(6, bonCommande.getModePaiement());
            stmt.setString(7, bonCommande.getCommentaire());
            stmt.setLong(8, bonCommande.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Met Ã  jour le statut d'un bon de commande
     */
    public void updateStatut(Long id, StatutCommande statut) throws SQLException {
        String sql = "UPDATE bons_commande SET statut = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Met Ã  jour la quantitÃ© reÃ§ue pour une ligne de commande
     */
    public void updateQuantiteRecue(Long ligneId, int quantite) throws SQLException {
        String sql = "UPDATE lignes_bon_commande SET quantite_recue = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setLong(2, ligneId);
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche un bon de commande par ID avec ses lignes
     */
    public Optional<BonCommande> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM bons_commande WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BonCommande bc = mapResultSetToBonCommande(rs);
                    bc.setLignes(findLignesByBonCommandeId(bc.getId()));
                    return Optional.of(bc);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recherche un bon de commande par numÃ©ro
     */
    public Optional<BonCommande> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM bons_commande WHERE numero = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BonCommande bc = mapResultSetToBonCommande(rs);
                    bc.setLignes(findLignesByBonCommandeId(bc.getId()));
                    return Optional.of(bc);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne toutes les lignes d'un bon de commande
     */
    private List<LigneBonCommande> findLignesByBonCommandeId(Long bonCommandeId) throws SQLException {
        String sql = "SELECT * FROM lignes_bon_commande WHERE bon_commande_id = ?";
        List<LigneBonCommande> lignes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bonCommandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lignes.add(mapResultSetToLigne(rs));
                }
            }
        }
        return lignes;
    }

    /**
     * Retourne tous les bons de commande
     */
    public List<BonCommande> findAll() throws SQLException {
        String sql = "SELECT * FROM bons_commande ORDER BY date_commande DESC";
        List<BonCommande> commandes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BonCommande bc = mapResultSetToBonCommande(rs);
                bc.setLignes(findLignesByBonCommandeId(bc.getId()));
                commandes.add(bc);
            }
        }
        return commandes;
    }

    /**
     * Retourne les bons de commande par fournisseur
     */
    public List<BonCommande> findByFournisseur(Long fournisseurId) throws SQLException {
        String sql = "SELECT * FROM bons_commande WHERE fournisseur_id = ? ORDER BY date_commande DESC";
        List<BonCommande> commandes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, fournisseurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BonCommande bc = mapResultSetToBonCommande(rs);
                    bc.setLignes(findLignesByBonCommandeId(bc.getId()));
                    commandes.add(bc);
                }
            }
        }
        return commandes;
    }

    /**
     * Retourne les bons de commande par statut
     */
    public List<BonCommande> findByStatut(StatutCommande statut) throws SQLException {
        String sql = "SELECT * FROM bons_commande WHERE statut = ? ORDER BY date_commande DESC";
        List<BonCommande> commandes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BonCommande bc = mapResultSetToBonCommande(rs);
                    bc.setLignes(findLignesByBonCommandeId(bc.getId()));
                    commandes.add(bc);
                }
            }
        }
        return commandes;
    }

    /**
     * Supprime un bon de commande (cascade sur les lignes)
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM bons_commande WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Convertit un ResultSet en BonCommande
     */
    private BonCommande mapResultSetToBonCommande(ResultSet rs) throws SQLException {
        BonCommande bc = new BonCommande();
        bc.setId(rs.getLong("id"));
        bc.setNumero(rs.getString("numero"));
        
        Timestamp dateCommande = rs.getTimestamp("date_commande");
        if (dateCommande != null) {
            bc.setDateCommande(dateCommande.toLocalDateTime());
        }
        
        Date datePrevue = rs.getDate("date_livraison_prevue");
        if (datePrevue != null) {
            bc.setDateLivraisonPrevue(datePrevue.toLocalDate());
        }
        
        Date dateReelle = rs.getDate("date_livraison_reelle");
        if (dateReelle != null) {
            bc.setDateLivraisonReelle(dateReelle.toLocalDate());
        }
        
        bc.setFournisseur(fournisseurDAO.findById(rs.getLong("fournisseur_id")).orElse(null));
        bc.setCommandeParUser(utilisateurDAO.findById(rs.getLong("commande_par_user_id")).orElse(null));
        bc.setStatut(StatutCommande.valueOf(rs.getString("statut")));
        bc.setMontantTotal(rs.getDouble("montant_total"));
        bc.setMontantPaye(rs.getDouble("montant_paye"));
        bc.setModePaiement(rs.getString("mode_paiement"));
        bc.setCommentaire(rs.getString("commentaire"));
        
        return bc;
    }

    /**
     * Convertit un ResultSet en LigneBonCommande
     */
    private LigneBonCommande mapResultSetToLigne(ResultSet rs) throws SQLException {
        LigneBonCommande ligne = new LigneBonCommande();
        ligne.setId(rs.getLong("id"));
        ligne.setProduit(produitDAO.findById(rs.getLong("produit_id")).orElse(null));
        ligne.setQuantiteCommandee(rs.getInt("quantite_commandee"));
        ligne.setQuantiteRecue(rs.getInt("quantite_recue"));
        ligne.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        ligne.setMontantLigne(rs.getDouble("montant_ligne"));
        ligne.setCommentaire(rs.getString("commentaire"));
        return ligne;
    }
}


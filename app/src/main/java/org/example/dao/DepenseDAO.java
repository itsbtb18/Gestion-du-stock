package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Depense;
import org.example.model.entity.CategorieDepense;
import org.example.model.entity.FrequenceDepense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepenseDAO {

    private final DatabaseConnection dbConnection;

    public DepenseDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Depense save(Depense depense) throws SQLException {
        String sql = "INSERT INTO depenses (numero, date_depense, categorie, description, montant, " +
                "mode_paiement, fournisseur_id, saisi_par_user_id, numero_facture, recurrente, " +
                "frequence, justificatif, commentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, depense.getNumero());
            stmt.setDate(2, Date.valueOf(depense.getDateDepense()));
            stmt.setString(3, depense.getCategorie().name());
            stmt.setString(4, depense.getDescription());
            stmt.setDouble(5, depense.getMontant());
            stmt.setString(6, depense.getModePaiement());
            
            if (depense.getFournisseur() != null) {
                stmt.setLong(7, depense.getFournisseur().getId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }
            
            stmt.setLong(8, depense.getSaisiParUser().getId());
            stmt.setString(9, depense.getNumeroFacture());
            stmt.setBoolean(10, depense.isRecurrente());
            
            if (depense.getFrequence() != null) {
                stmt.setString(11, depense.getFrequence().name());
            } else {
                stmt.setNull(11, Types.VARCHAR);
            }
            
            stmt.setString(12, depense.getJustificatif());
            stmt.setString(13, depense.getCommentaire());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    depense.setId(rs.getLong(1));
                }
            }

            return depense;
        }
    }

    public void update(Depense depense) throws SQLException {
        String sql = "UPDATE depenses SET date_depense = ?, categorie = ?, description = ?, montant = ?, " +
                "mode_paiement = ?, fournisseur_id = ?, numero_facture = ?, recurrente = ?, " +
                "frequence = ?, justificatif = ?, commentaire = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(depense.getDateDepense()));
            stmt.setString(2, depense.getCategorie().name());
            stmt.setString(3, depense.getDescription());
            stmt.setDouble(4, depense.getMontant());
            stmt.setString(5, depense.getModePaiement());
            
            if (depense.getFournisseur() != null) {
                stmt.setLong(6, depense.getFournisseur().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setString(7, depense.getNumeroFacture());
            stmt.setBoolean(8, depense.isRecurrente());
            
            if (depense.getFrequence() != null) {
                stmt.setString(9, depense.getFrequence().name());
            } else {
                stmt.setNull(9, Types.VARCHAR);
            }
            
            stmt.setString(10, depense.getJustificatif());
            stmt.setString(11, depense.getCommentaire());
            stmt.setLong(12, depense.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM depenses WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Optional<Depense> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM depenses WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDepense(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Depense> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM depenses WHERE numero = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDepense(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Depense> findAll() throws SQLException {
        String sql = "SELECT * FROM depenses ORDER BY date_depense DESC";
        List<Depense> depenses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                depenses.add(mapResultSetToDepense(rs));
            }
        }
        return depenses;
    }

    public List<Depense> findByCategorie(CategorieDepense categorie) throws SQLException {
        String sql = "SELECT * FROM depenses WHERE categorie = ? ORDER BY date_depense DESC";
        List<Depense> depenses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categorie.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    depenses.add(mapResultSetToDepense(rs));
                }
            }
        }
        return depenses;
    }

    public List<Depense> findByPeriode(java.time.LocalDate debut, java.time.LocalDate fin) throws SQLException {
        String sql = "SELECT * FROM depenses WHERE date_depense BETWEEN ? AND ? ORDER BY date_depense DESC";
        List<Depense> depenses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    depenses.add(mapResultSetToDepense(rs));
                }
            }
        }
        return depenses;
    }

    public List<Depense> findRecurrentes() throws SQLException {
        String sql = "SELECT * FROM depenses WHERE recurrente = true ORDER BY date_depense DESC";
        List<Depense> depenses = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                depenses.add(mapResultSetToDepense(rs));
            }
        }
        return depenses;
    }

    public double getTotalByCategorie(CategorieDepense categorie, java.time.LocalDate debut, 
                                      java.time.LocalDate fin) throws SQLException {
        String sql = "SELECT SUM(montant) FROM depenses WHERE categorie = ? AND date_depense BETWEEN ? AND ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categorie.name());
            stmt.setDate(2, Date.valueOf(debut));
            stmt.setDate(3, Date.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public double getTotalByPeriode(java.time.LocalDate debut, java.time.LocalDate fin) throws SQLException {
        String sql = "SELECT SUM(montant) FROM depenses WHERE date_depense BETWEEN ? AND ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    private Depense mapResultSetToDepense(ResultSet rs) throws SQLException {
        Depense depense = new Depense();
        depense.setId(rs.getLong("id"));
        depense.setNumero(rs.getString("numero"));
        
        Date dateDepense = rs.getDate("date_depense");
        if (dateDepense != null) {
            depense.setDateDepense(dateDepense.toLocalDate());
        }
        
        depense.setCategorie(CategorieDepense.valueOf(rs.getString("categorie")));
        depense.setDescription(rs.getString("description"));
        depense.setMontant(rs.getDouble("montant"));
        depense.setModePaiement(rs.getString("mode_paiement"));
        
        Long fournisseurId = rs.getLong("fournisseur_id");
        if (!rs.wasNull()) {
            depense.setFournisseur(fournisseurDAO.findById(fournisseurId).orElse(null));
        }
        
        depense.setSaisiParUser(utilisateurDAO.findById(rs.getLong("saisi_par_user_id")).orElse(null));
        depense.setNumeroFacture(rs.getString("numero_facture"));
        depense.setRecurrente(rs.getBoolean("recurrente"));
        
        String frequence = rs.getString("frequence");
        if (frequence != null) {
            depense.setFrequence(FrequenceDepense.valueOf(frequence));
        }
        
        depense.setJustificatif(rs.getString("justificatif"));
        depense.setCommentaire(rs.getString("commentaire"));
        
        return depense;
    }
}


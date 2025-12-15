package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.Vente;
import org.example.model.entity.LigneVente;
import org.example.model.entity.Produit;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VenteDAO {
    
    private final DatabaseConnection dbConnection;
    
    public VenteDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Vente> findAll() {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM ventes ORDER BY date_vente DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Vente vente = mapResultSetToVente(rs);
                loadLignesVente(vente);
                ventes.add(vente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all sales: " + e.getMessage());
        }
        
        return ventes;
    }
    
    public Optional<Vente> findById(Long id) {
        String sql = "SELECT * FROM ventes WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Vente vente = mapResultSetToVente(rs);
                loadLignesVente(vente);
                return Optional.of(vente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding sale by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Optional<Vente> findByNumero(String numero) {
        String sql = "SELECT * FROM ventes WHERE numero = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numero);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Vente vente = mapResultSetToVente(rs);
                loadLignesVente(vente);
                return Optional.of(vente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding sale by numero: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public List<Vente> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM ventes WHERE CAST(date_vente AS DATE) BETWEEN ? AND ? " +
                    "ORDER BY date_vente DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vente vente = mapResultSetToVente(rs);
                loadLignesVente(vente);
                ventes.add(vente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding sales by date range: " + e.getMessage());
        }
        
        return ventes;
    }
    
    public List<Vente> findByClient(Long clientId) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM ventes WHERE client_id = ? ORDER BY date_vente DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vente vente = mapResultSetToVente(rs);
                loadLignesVente(vente);
                ventes.add(vente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding sales by client: " + e.getMessage());
        }
        
        return ventes;
    }
    
    public List<Vente> findToday() {
        LocalDate today = LocalDate.now();
        return findByDateRange(today, today);
    }
    
    public Vente save(Vente vente) {
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); 
            
            String venteSql = "INSERT INTO ventes (numero, date_vente, client_id, vendeur_id, " +
                            "montant_total, montant_remise, montant_tva, montant_final, " +
                            "mode_paiement, statut, commentaire) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(venteSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, vente.getNumero());
                pstmt.setTimestamp(2, Timestamp.valueOf(vente.getDateVente()));
                
                if (vente.getClient() != null) {
                    pstmt.setLong(3, vente.getClient().getId());
                } else {
                    pstmt.setNull(3, Types.BIGINT);
                }
                
                pstmt.setLong(4, vente.getVendeurId());
                pstmt.setDouble(5, vente.getMontantTotal());
                pstmt.setDouble(6, vente.getMontantRemise());
                pstmt.setDouble(7, vente.getMontantTVA());
                pstmt.setDouble(8, vente.getMontantFinal());
                pstmt.setString(9, vente.getModePaiement());
                pstmt.setString(10, vente.getStatut());
                pstmt.setString(11, vente.getCommentaire());
                
                pstmt.executeUpdate();
                
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    vente.setId(generatedKeys.getLong(1));
                }
            }
            
            if (vente.getLignes() != null && !vente.getLignes().isEmpty()) {
                String ligneSql = "INSERT INTO lignes_vente (vente_id, produit_id, quantite, " +
                                "prix_unitaire, remise, sous_total) VALUES (?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement pstmt = conn.prepareStatement(ligneSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (LigneVente ligne : vente.getLignes()) {
                        pstmt.setLong(1, vente.getId());
                        pstmt.setLong(2, ligne.getProduit().getId());
                        pstmt.setInt(3, ligne.getQuantite());
                        pstmt.setDouble(4, ligne.getPrixUnitaire());
                        pstmt.setDouble(5, ligne.getRemise());
                        pstmt.setDouble(6, ligne.getSousTotal());
                        
                        pstmt.executeUpdate();
                        
                        ResultSet generatedKeys = pstmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            ligne.setId(generatedKeys.getLong(1));
                        }
                    }
                }
            }
            
            conn.commit(); 
            System.out.println("Sale saved successfully: " + vente.getNumero());
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            System.err.println("Error saving sale: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
        
        return vente;
    }
    
    public boolean updateStatut(Long venteId, String statut) {
        String sql = "UPDATE ventes SET statut = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            pstmt.setLong(2, venteId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating sale status: " + e.getMessage());
        }
        
        return false;
    }
    
    public double[] getStatistics(LocalDate startDate, LocalDate endDate) {
        double[] stats = new double[3]; 
        String sql = "SELECT COUNT(*) as nb_ventes, SUM(montant_final) as total, " +
                    "AVG(montant_final) as panier_moyen " +
                    "FROM ventes " +
                    "WHERE CAST(date_vente AS DATE) BETWEEN ? AND ? AND statut = 'VALIDEE'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats[0] = rs.getDouble("nb_ventes");
                stats[1] = rs.getDouble("total");
                stats[2] = rs.getDouble("panier_moyen");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting sales statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    private void loadLignesVente(Vente vente) {
        String sql = "SELECT lv.*, p.code, p.nom, p.prix " +
                    "FROM lignes_vente lv " +
                    "JOIN produits p ON lv.produit_id = p.id " +
                    "WHERE lv.vente_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, vente.getId());
            ResultSet rs = pstmt.executeQuery();
            
            List<LigneVente> lignes = new ArrayList<>();
            
            while (rs.next()) {
                LigneVente ligne = new LigneVente();
                ligne.setId(rs.getLong("id"));
                ligne.setQuantite(rs.getInt("quantite"));
                ligne.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                ligne.setRemise(rs.getDouble("remise"));
                ligne.setSousTotal(rs.getDouble("sous_total"));
                
                Produit produit = new Produit();
                produit.setId(rs.getLong("produit_id"));
                produit.setCode(rs.getString("code"));
                produit.setNom(rs.getString("nom"));
                produit.setPrix(rs.getDouble("prix"));
                
                ligne.setProduit(produit);
                lignes.add(ligne);
            }
            
            vente.setLignes(lignes);
            
        } catch (SQLException e) {
            System.err.println("Error loading sale lines: " + e.getMessage());
        }
    }
    
    private Vente mapResultSetToVente(ResultSet rs) throws SQLException {
        Vente vente = new Vente();
        
        vente.setId(rs.getLong("id"));
        vente.setNumero(rs.getString("numero"));
        
        Timestamp dateVente = rs.getTimestamp("date_vente");
        if (dateVente != null) {
            vente.setDateVente(dateVente.toLocalDateTime());
        }
        
        vente.setVendeurId(rs.getLong("vendeur_id"));
        vente.setMontantTotal(rs.getDouble("montant_total"));
        vente.setMontantRemise(rs.getDouble("montant_remise"));
        vente.setMontantTVA(rs.getDouble("montant_tva"));
        vente.setMontantFinal(rs.getDouble("montant_final"));
        vente.setModePaiement(rs.getString("mode_paiement"));
        vente.setStatut(rs.getString("statut"));
        vente.setCommentaire(rs.getString("commentaire"));
        
        return vente;
    }
    
    public int count() {
        String sql = "SELECT COUNT(*) FROM ventes";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting sales: " + e.getMessage());
        }
        
        return 0;
    }
    
    public List<Vente> findRecent(int limit) {
        String sql = "SELECT * FROM ventes ORDER BY date_vente DESC LIMIT ?";
        List<Vente> ventes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ventes.add(mapResultSetToVente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding recent sales: " + e.getMessage());
        }
        
        return ventes;
    }
}


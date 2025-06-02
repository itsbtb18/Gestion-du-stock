package org.example.DAO;

import org.example.db.DBConnection;
import org.example.model.achat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchatDB {
    private Connection connection;

    public AchatDB() {
        this.connection = DBConnection.connect();
    }

    public boolean ajouter(achat achat) {
        String sql = "INSERT INTO achat (date_achat, montant_total, fournisseur) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, achat.getDateAchat());
            pstmt.setDouble(2, achat.getMontantTotal());
            pstmt.setString(3, achat.getFournisseur());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    achat.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'achat : " + e.getMessage());
            return false;
        }
    }

    public achat getById(int id) {
        String sql = "SELECT * FROM achat WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireAchatDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'achat : " + e.getMessage());
        }
        return null;
    }

    public List<achat> getTout() {
        List<achat> achats = new ArrayList<>();
        String sql = "SELECT * FROM achat";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                achats.add(extraireAchatDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des achats : " + e.getMessage());
        }
        return achats;
    }

    private achat extraireAchatDuResultSet(ResultSet rs) throws SQLException {
        return new achat(
                rs.getInt("id"),
                rs.getString("date_achat"),
                rs.getDouble("montant_total"),
                rs.getString("fournisseur")
        );
    }

    public boolean modifier(achat achat) {
        String sql = "UPDATE achat SET date_achat = ?, montant_total = ?, fournisseur = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, achat.getDateAchat());
            pstmt.setDouble(2, achat.getMontantTotal());
            pstmt.setString(3, achat.getFournisseur());
            pstmt.setInt(4, achat.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'achat : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM achat WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'achat : " + e.getMessage());
            return false;
        }
    }

    public List<achat> rechercherParDate(String date) {
        List<achat> achats = new ArrayList<>();
        String sql = "SELECT * FROM achat WHERE date_achat LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + date + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                achats.add(extraireAchatDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des achats : " + e.getMessage());
        }
        return achats;
    }
}
package org.example.DAO;

import org.example.db.DBConnection;
import org.example.model.vente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDB {

    public boolean ajouter(vente vente) {
        String sql = "INSERT INTO vente (date, montant_total, mode_paiement, client_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vente.getDate());
            pstmt.setDouble(2, vente.getMontantTotal());
            pstmt.setString(3, vente.getModePaiement());
            pstmt.setInt(4, vente.getClientId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la vente : " + e.getMessage());
            return false;
        }
    }

    public vente getById(int id) {
        String sql = "SELECT * FROM vente WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new vente(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getDouble("montant_total"),
                        rs.getString("mode_paiement"),
                        rs.getInt("client_id")
                );
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la vente : " + e.getMessage());
        }
        return null;
    }

    public List<vente> getTout() {
        List<vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ventes.add(new vente(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getDouble("montant_total"),
                        rs.getString("mode_paiement"),
                        rs.getInt("client_id")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ventes : " + e.getMessage());
        }
        return ventes;
    }

    public boolean modifier(vente vente) {
        String sql = "UPDATE vente SET date = ?, montant_total = ?, mode_paiement = ?, client_id = ? WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vente.getDate());
            pstmt.setDouble(2, vente.getMontantTotal());
            pstmt.setString(3, vente.getModePaiement());
            pstmt.setInt(4, vente.getClientId());
            pstmt.setInt(5, vente.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la vente : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM vente WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la vente : " + e.getMessage());
            return false;
        }
    }

    public List<vente> rechercherParDate(String date) {
        List<vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente WHERE date LIKE ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + date + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ventes.add(new vente(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getDouble("montant_total"),
                        rs.getString("mode_paiement"),
                        rs.getInt("client_id")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ventes par date : " + e.getMessage());
        }
        return ventes;
    }
}
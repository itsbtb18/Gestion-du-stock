package org.example.dao;

import org.example.db.DBConnection;
import org.example.model.client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDB {
    private Connection connection;

    public ClientDB() {
        this.connection = DBConnection.connect();
    }

    public boolean ajouter(client client) {
        String sql = "INSERT INTO client (nom, prenom, telephone) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getTelephone());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    // L'ID est géré automatiquement par la base de données
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du client : " + e.getMessage());
            return false;
        }
    }

    public client getById(int id) {
        String sql = "SELECT * FROM client WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireClientDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du client : " + e.getMessage());
        }
        return null;
    }

    public List<client> getTout() {
        List<client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(extraireClientDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des clients : " + e.getMessage());
        }
        return clients;
    }

    private client extraireClientDuResultSet(ResultSet rs) throws SQLException {
        return new client(
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone")
        );
    }

    public boolean modifier(client client, int id) {
        String sql = "UPDATE client SET nom = ?, prenom = ?, telephone = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getTelephone());
            pstmt.setInt(4, id);

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du client : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM client WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client : " + e.getMessage());
            return false;
        }
    }

    public List<client> rechercherParNom(String nom) {
        List<client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client WHERE nom LIKE ? OR prenom LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            pstmt.setString(2, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                clients.add(extraireClientDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des clients : " + e.getMessage());
        }
        return clients;
    }
}
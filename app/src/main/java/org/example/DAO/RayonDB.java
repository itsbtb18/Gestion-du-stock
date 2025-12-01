package org.example.DAO;

import org.example.db.DBConnection;
import org.example.model.rayon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RayonDB {
    private Connection connection;

    public RayonDB() {
        this.connection = DBConnection.connect();
    }

    public boolean ajouter(rayon rayon) {
        String sql = "INSERT INTO rayon (nom) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, rayon.getNom());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du rayon : " + e.getMessage());
            return false;
        }
    }

    public rayon getById(int id) {
        String sql = "SELECT * FROM rayon WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new rayon(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        null // description n'est pas utilisée dans la classe rayon
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du rayon : " + e.getMessage());
        }
        return null;
    }

    public List<rayon> getTout() {
        List<rayon> rayons = new ArrayList<>();
        String sql = "SELECT * FROM rayon";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rayons.add(new rayon(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        null // description n'est pas utilisée dans la classe rayon
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des rayons : " + e.getMessage());
        }
        return rayons;
    }

    public boolean modifier(rayon rayon) {
        String sql = "UPDATE rayon SET nom = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, rayon.getNom());
            pstmt.setInt(2, rayon.getId());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du rayon : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM rayon WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du rayon : " + e.getMessage());
            return false;
        }
    }

    public List<rayon> rechercherParNom(String nom) {
        List<rayon> rayons = new ArrayList<>();
        String sql = "SELECT * FROM rayon WHERE nom LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rayons.add(new rayon(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        null // description n'est pas utilisée dans la classe rayon
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des rayons : " + e.getMessage());
        }
        return rayons;
    }
}
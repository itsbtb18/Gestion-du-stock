package org.example.dao;

import org.example.db.DBConnection;
import org.example.model.categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDB {
    private Connection connection;

    public CategorieDB() {
        this.connection = DBConnection.connect();
    }

    public boolean ajouter(categorie categorie) {
        String sql = "INSERT INTO categorie (nom) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, categorie.getNom());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    categorie.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
            return false;
        }
    }

    public categorie getById(int id) {
        String sql = "SELECT * FROM categorie WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireCategorieDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la catégorie : " + e.getMessage());
        }
        return null;
    }

    public List<categorie> getTout() {
        List<categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(extraireCategorieDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories : " + e.getMessage());
        }
        return categories;
    }

    private categorie extraireCategorieDuResultSet(ResultSet rs) throws SQLException {
        return new categorie(
                rs.getInt("id"),
                rs.getString("nom")
        );
    }

    public boolean modifier(categorie categorie) {
        String sql = "UPDATE categorie SET nom = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categorie.getNom());
            pstmt.setInt(2, categorie.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la catégorie : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM categorie WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la catégorie : " + e.getMessage());
            return false;
        }
    }

    public List<categorie> rechercherParNom(String nom) {
        List<categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie WHERE nom LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                categories.add(extraireCategorieDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des catégories : " + e.getMessage());
        }
        return categories;
    }
}
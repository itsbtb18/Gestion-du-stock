package org.example.DAO;

import org.example.db.DBConnection;
import org.example.model.Fournisseur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDB {
    private Connection connection;

    public FournisseurDB() {
        this.connection = DBConnection.connect();
    }

    public boolean ajouter(Fournisseur fournisseur) {
        String sql = "INSERT INTO fournisseur (nom, adresse, telephone) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getTelephone());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    fournisseur.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du fournisseur : " + e.getMessage());
            return false;
        }
    }

    public Fournisseur getById(int id) {
        String sql = "SELECT * FROM fournisseur WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireFournisseurDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du fournisseur : " + e.getMessage());
        }
        return null;
    }

    public List<Fournisseur> getTout() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fournisseurs.add(extraireFournisseurDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fournisseurs : " + e.getMessage());
        }
        return fournisseurs;
    }

    private Fournisseur extraireFournisseurDuResultSet(ResultSet rs) throws SQLException {
        return new Fournisseur(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("adresse"),
                rs.getString("telephone")
        );
    }

    public boolean modifier(Fournisseur fournisseur) {
        String sql = "UPDATE fournisseur SET nom = ?, adresse = ?, telephone = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getTelephone());
            pstmt.setInt(4, fournisseur.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du fournisseur : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM fournisseur WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur : " + e.getMessage());
            return false;
        }
    }

    public List<Fournisseur> rechercherParNom(String nom) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur WHERE nom LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                fournisseurs.add(extraireFournisseurDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des fournisseurs : " + e.getMessage());
        }
        return fournisseurs;
    }
}
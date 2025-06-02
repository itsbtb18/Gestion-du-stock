package org.example.DAO;

import org.example.db.DBConnection;
import org.example.model.produit;
import org.example.model.categorie;
import org.example.model.Fournisseur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDB {
    private Connection connection;
    private CategorieDB categorieDB;
    private FournisseurDB fournisseurDB;

    public ProduitDB() {
        this.connection = DBConnection.connect();
        this.categorieDB = new CategorieDB();
        this.fournisseurDB = new FournisseurDB();
    }

    public boolean ajouter(produit produit) {
        String sql = "INSERT INTO produit (id, nom, prix_achat, prix_vente, date_expiration, code_bar, id_categorie, id_fournisseur) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, produit.getId());
            pstmt.setString(2, produit.getNom());
            pstmt.setDouble(3, produit.getPrixAchat());
            pstmt.setDouble(4, produit.getPrixVente());
            pstmt.setDate(5, produit.getDateExpiration() != null ? new java.sql.Date(produit.getDateExpiration().getTime()) : null);
            pstmt.setString(6, produit.getCodeBar());
            pstmt.setInt(7, produit.getCategorie().getId());
            pstmt.setInt(8, produit.getFournisseur().getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
            return false;
        }
    }

    public produit getById(String id) {
        String sql = "SELECT * FROM produit WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireProduitDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du produit : " + e.getMessage());
        }
        return null;
    }

    public List<produit> getTout() {
        List<produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                produits.add(extraireProduitDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }
        return produits;
    }

    private produit extraireProduitDuResultSet(ResultSet rs) throws SQLException {
        produit p = new produit();
        p.setId(rs.getString("id"));
        p.setNom(rs.getString("nom"));
        p.setPrixAchat(rs.getDouble("prix_achat"));
        p.setPrixVente(rs.getDouble("prix_vente"));
        p.setDateExpiration(rs.getDate("date_expiration"));
        p.setCodeBar(rs.getString("code_bar"));
        p.setCategorie(categorieDB.getById(rs.getInt("id_categorie")));
        p.setFournisseur(fournisseurDB.getById(rs.getInt("id_fournisseur")));
        return p;
    }

    public boolean modifier(produit produit) {
        String sql = "UPDATE produit SET nom = ?, prix_achat = ?, prix_vente = ?, date_expiration = ?, code_bar = ?, id_categorie = ?, id_fournisseur = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setDouble(2, produit.getPrixAchat());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setDate(4, produit.getDateExpiration() != null ? new java.sql.Date(produit.getDateExpiration().getTime()) : null);
            pstmt.setString(5, produit.getCodeBar());
            pstmt.setInt(6, produit.getCategorie().getId());
            pstmt.setInt(7, produit.getFournisseur().getId());
            pstmt.setString(8, produit.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du produit : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(String id) {
        String sql = "DELETE FROM produit WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit : " + e.getMessage());
            return false;
        }
    }

    public List<produit> rechercherParNom(String nom) {
        List<produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE nom LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                produits.add(extraireProduitDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits : " + e.getMessage());
        }
        return produits;
    }
}
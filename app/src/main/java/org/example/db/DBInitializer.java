package org.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.example.db.DBConnection.connect;
import static org.example.db.DBConnection.createTables;

public class DBInitializer {

    public static void initializeDatabase() {
        try {
            // Création des tables
            createTables();

            // Insertion des données initiales
            insertTestData();

            System.out.println("Base de données initialisée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertTestData() {
        insertRayons();
        insertCategories();
        insertFournisseurs();
        insertClients();
        insertProduits();
    }

    private static void insertRayons() {
        String sql = "INSERT INTO rayon (nom, description) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Rayon 1
            pstmt.setString(1, "Alimentaire");
            pstmt.setString(2, "Produits alimentaires");
            pstmt.executeUpdate();

            // Rayon 2
            pstmt.setString(1, "Électronique");
            pstmt.setString(2, "Produits électroniques");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur insertion rayons: " + e.getMessage());
        }
    }

    private static void insertCategories() {
        String sql = "INSERT INTO categorie (nom) VALUES (?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Catégorie 1
            pstmt.setString(1, "Fruits");
            pstmt.executeUpdate();

            // Catégorie 2
            pstmt.setString(1, "Légumes");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur insertion catégories: " + e.getMessage());
        }
    }

    private static void insertFournisseurs() {
        String sql = "INSERT INTO fournisseur (nom, adresse, telephone) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Fournisseur 1
            pstmt.setString(1, "Fournisseur A");
            pstmt.setString(2, "123 rue Commerce");
            pstmt.setString(3, "0601020304");
            pstmt.executeUpdate();

            // Fournisseur 2
            pstmt.setString(1, "Fournisseur B");
            pstmt.setString(2, "456 rue Business");
            pstmt.setString(3, "0605040302");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur insertion fournisseurs: " + e.getMessage());
        }
    }

    private static void insertClients() {
        String sql = "INSERT INTO client (nom, prenom, telephone) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Client 1
            pstmt.setString(1, "Dupont");
            pstmt.setString(2, "Jean");
            pstmt.setString(3, "0611223344");
            pstmt.executeUpdate();

            // Client 2
            pstmt.setString(1, "Martin");
            pstmt.setString(2, "Marie");
            pstmt.setString(3, "0655667788");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur insertion clients: " + e.getMessage());
        }
    }

    private static void insertProduits() {
        String sql = "INSERT INTO produit (id, nom, prix_achat, prix_vente, code_bar, id_categorie, id_fournisseur) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Produit 1
            pstmt.setString(1, "P001");
            pstmt.setString(2, "Pommes");
            pstmt.setDouble(3, 1.0);
            pstmt.setDouble(4, 2.0);
            pstmt.setString(5, "3456789012");
            pstmt.setInt(6, 1);
            pstmt.setInt(7, 1);
            pstmt.executeUpdate();

            // Produit 2
            pstmt.setString(1, "P002");
            pstmt.setString(2, "Carottes");
            pstmt.setDouble(3, 0.5);
            pstmt.setDouble(4, 1.5);
            pstmt.setString(5, "3456789013");
            pstmt.setInt(6, 2);
            pstmt.setInt(7, 1);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur insertion produits: " + e.getMessage());
        }
    }
}
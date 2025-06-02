package org.example.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection{
    private static final String DB_URL = "jdbc:sqlite:stock.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // Lire le contenu du fichier SQL
            String sql = new String(
                    DBConnection.class.getResourceAsStream("/Script.sql").readAllBytes(),
                    StandardCharsets.UTF_8
            );
            // Exécuter chaque instruction SQL
            for (String instruction : sql.split(";")) {
                if (!instruction.trim().isEmpty()) {
                    stmt.execute(instruction);
                }
            }
        } catch (SQLException | IOException e) {
            System.out.println("Erreur de création des tables: " + e.getMessage());
        }
    }}
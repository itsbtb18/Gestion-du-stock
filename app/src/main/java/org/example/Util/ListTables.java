package org.example.util;

import org.example.db.DatabaseConnection;
import java.sql.*;

public class ListTables {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});
            
            System.out.println("Existing tables:");
            System.out.println("================");
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("- " + tableName);
            }
        } catch (Exception e) {
            org.example.util.LoggerUtil.logError(ListTables.class, "Failed to list database tables", e);
        }
    }
}

package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.StoreSettings;

import java.sql.*;
import java.util.Optional;

public class StoreSettingsDAO {
    
    private final DatabaseConnection dbConnection;
    
    public StoreSettingsDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public Optional<StoreSettings> findByStoreId(Long storeId) {
        String sql = "SELECT * FROM store_settings WHERE store_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToSettings(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding store settings: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public StoreSettings save(StoreSettings settings) {
        String sql = "INSERT INTO store_settings (store_id, allow_negative_stock, " +
                    "require_manager_approval, default_vat_rate, max_discount_percent, " +
                    "invoice_footer_text, invoice_header_text, loyalty_program_enabled, " +
                    "low_stock_notifications_enabled, expiration_alerts_enabled, " +
                    "expiration_alert_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, settings.getStoreId());
            pstmt.setBoolean(2, settings.isAllowNegativeStock());
            pstmt.setBoolean(3, settings.isRequireManagerApproval());
            pstmt.setDouble(4, settings.getDefaultVatRate());
            pstmt.setDouble(5, settings.getMaxDiscountPercent());
            pstmt.setString(6, settings.getInvoiceFooterText());
            pstmt.setString(7, settings.getInvoiceHeaderText());
            pstmt.setBoolean(8, settings.isLoyaltyProgramEnabled());
            pstmt.setBoolean(9, settings.isLowStockNotificationsEnabled());
            pstmt.setBoolean(10, settings.isExpirationAlertsEnabled());
            pstmt.setInt(11, settings.getExpirationAlertDays());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                settings.setId(rs.getLong(1));
            }
            
            return settings;
            
        } catch (SQLException e) {
            System.err.println("Error saving store settings: " + e.getMessage());
            throw new RuntimeException("Failed to save store settings", e);
        }
    }
    
    public boolean update(StoreSettings settings) {
        String sql = "UPDATE store_settings SET allow_negative_stock = ?, " +
                    "require_manager_approval = ?, default_vat_rate = ?, max_discount_percent = ?, " +
                    "invoice_footer_text = ?, invoice_header_text = ?, loyalty_program_enabled = ?, " +
                    "low_stock_notifications_enabled = ?, expiration_alerts_enabled = ?, " +
                    "expiration_alert_days = ? WHERE store_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, settings.isAllowNegativeStock());
            pstmt.setBoolean(2, settings.isRequireManagerApproval());
            pstmt.setDouble(3, settings.getDefaultVatRate());
            pstmt.setDouble(4, settings.getMaxDiscountPercent());
            pstmt.setString(5, settings.getInvoiceFooterText());
            pstmt.setString(6, settings.getInvoiceHeaderText());
            pstmt.setBoolean(7, settings.isLoyaltyProgramEnabled());
            pstmt.setBoolean(8, settings.isLowStockNotificationsEnabled());
            pstmt.setBoolean(9, settings.isExpirationAlertsEnabled());
            pstmt.setInt(10, settings.getExpirationAlertDays());
            pstmt.setLong(11, settings.getStoreId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating store settings: " + e.getMessage());
            return false;
        }
    }
    
    private StoreSettings mapResultSetToSettings(ResultSet rs) throws SQLException {
        StoreSettings settings = new StoreSettings();
        settings.setId(rs.getLong("id"));
        settings.setStoreId(rs.getLong("store_id"));
        settings.setAllowNegativeStock(rs.getBoolean("allow_negative_stock"));
        settings.setRequireManagerApproval(rs.getBoolean("require_manager_approval"));
        settings.setDefaultVatRate(rs.getDouble("default_vat_rate"));
        settings.setMaxDiscountPercent(rs.getDouble("max_discount_percent"));
        settings.setInvoiceFooterText(rs.getString("invoice_footer_text"));
        settings.setInvoiceHeaderText(rs.getString("invoice_header_text"));
        settings.setLoyaltyProgramEnabled(rs.getBoolean("loyalty_program_enabled"));
        settings.setLowStockNotificationsEnabled(rs.getBoolean("low_stock_notifications_enabled"));
        settings.setExpirationAlertsEnabled(rs.getBoolean("expiration_alerts_enabled"));
        settings.setExpirationAlertDays(rs.getInt("expiration_alert_days"));
        
        return settings;
    }
}

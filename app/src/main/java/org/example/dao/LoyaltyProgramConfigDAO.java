package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.entity.LoyaltyProgramConfig;
import org.example.model.entity.LoyaltyProgramConfig.RewardType;

import java.sql.*;
import java.util.Optional;

public class LoyaltyProgramConfigDAO {
    
    private final DatabaseConnection dbConnection;
    
    public LoyaltyProgramConfigDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public Optional<LoyaltyProgramConfig> findByStoreId(Long storeId) {
        String sql = "SELECT * FROM loyalty_program_config WHERE store_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToConfig(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding loyalty config: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public LoyaltyProgramConfig save(LoyaltyProgramConfig config) {
        String sql = "INSERT INTO loyalty_program_config (store_id, enabled, points_per_currency_unit, " +
                    "minimum_purchase_amount, reward_threshold, reward_type, reward_value, " +
                    "points_expiration_days, allow_partial_redemption, minimum_redemption_points, " +
                    "max_redemption_percent_of_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, config.getStoreId());
            pstmt.setBoolean(2, config.isEnabled());
            pstmt.setInt(3, config.getPointsPerCurrencyUnit());
            pstmt.setDouble(4, config.getMinimumPurchaseAmount());
            pstmt.setInt(5, config.getRewardThreshold());
            pstmt.setString(6, config.getRewardType().name());
            pstmt.setDouble(7, config.getRewardValue());
            pstmt.setInt(8, config.getPointsExpirationDays());
            pstmt.setBoolean(9, config.isAllowPartialRedemption());
            pstmt.setInt(10, config.getMinimumRedemptionPoints());
            pstmt.setDouble(11, config.getMaxRedemptionPercentOfTotal());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                config.setId(rs.getLong(1));
            }
            
            return config;
            
        } catch (SQLException e) {
            System.err.println("Error saving loyalty config: " + e.getMessage());
            throw new RuntimeException("Failed to save loyalty config", e);
        }
    }
    
    public boolean update(LoyaltyProgramConfig config) {
        String sql = "UPDATE loyalty_program_config SET enabled = ?, points_per_currency_unit = ?, " +
                    "minimum_purchase_amount = ?, reward_threshold = ?, reward_type = ?, reward_value = ?, " +
                    "points_expiration_days = ?, allow_partial_redemption = ?, minimum_redemption_points = ?, " +
                    "max_redemption_percent_of_total = ? WHERE store_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, config.isEnabled());
            pstmt.setInt(2, config.getPointsPerCurrencyUnit());
            pstmt.setDouble(3, config.getMinimumPurchaseAmount());
            pstmt.setInt(4, config.getRewardThreshold());
            pstmt.setString(5, config.getRewardType().name());
            pstmt.setDouble(6, config.getRewardValue());
            pstmt.setInt(7, config.getPointsExpirationDays());
            pstmt.setBoolean(8, config.isAllowPartialRedemption());
            pstmt.setInt(9, config.getMinimumRedemptionPoints());
            pstmt.setDouble(10, config.getMaxRedemptionPercentOfTotal());
            pstmt.setLong(11, config.getStoreId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating loyalty config: " + e.getMessage());
            return false;
        }
    }
    
    private LoyaltyProgramConfig mapResultSetToConfig(ResultSet rs) throws SQLException {
        LoyaltyProgramConfig config = new LoyaltyProgramConfig();
        config.setId(rs.getLong("id"));
        config.setStoreId(rs.getLong("store_id"));
        config.setEnabled(rs.getBoolean("enabled"));
        config.setPointsPerCurrencyUnit(rs.getInt("points_per_currency_unit"));
        config.setMinimumPurchaseAmount(rs.getDouble("minimum_purchase_amount"));
        config.setRewardThreshold(rs.getInt("reward_threshold"));
        config.setRewardType(RewardType.valueOf(rs.getString("reward_type")));
        config.setRewardValue(rs.getDouble("reward_value"));
        config.setPointsExpirationDays(rs.getInt("points_expiration_days"));
        config.setAllowPartialRedemption(rs.getBoolean("allow_partial_redemption"));
        config.setMinimumRedemptionPoints(rs.getInt("minimum_redemption_points"));
        config.setMaxRedemptionPercentOfTotal(rs.getDouble("max_redemption_percent_of_total"));
        
        return config;
    }
}

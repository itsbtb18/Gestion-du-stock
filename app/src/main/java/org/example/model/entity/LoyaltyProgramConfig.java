package org.example.model.entity;

/**
 * LoyaltyProgramConfig - Configuration for store's loyalty program
 * Defines rules for earning and redeeming loyalty points
 */
public class LoyaltyProgramConfig {
    
    private Long id;
    private Long storeId; // Foreign key to Store
    private boolean enabled;
    
    // Earning Rules
    private int pointsPerCurrencyUnit; // e.g., 10 points per 1 DH spent
    private double minimumPurchaseAmount; // Minimum purchase to earn points
    
    // Redemption Rules
    private int rewardThreshold; // Points needed to get reward (e.g., 100 points)
    private RewardType rewardType; // PERCENTAGE or FIXED_AMOUNT
    private double rewardValue; // Value of reward (e.g., 10% or 10 DH)
    private int pointsExpirationDays; // Days until points expire (0 = never)
    
    // Advanced Rules
    private boolean allowPartialRedemption; // Allow using any amount of points
    private int minimumRedemptionPoints; // Minimum points to redeem
    private double maxRedemptionPercentOfTotal; // Max % of sale that can be paid with points
    
    // Constructors
    public LoyaltyProgramConfig() {
        // Set default values
        this.enabled = true;
        this.pointsPerCurrencyUnit = 10; // 10 points per DH
        this.minimumPurchaseAmount = 0.0;
        this.rewardThreshold = 100; // 100 points
        this.rewardType = RewardType.PERCENTAGE;
        this.rewardValue = 5.0; // 5% discount
        this.pointsExpirationDays = 0; // Never expire
        this.allowPartialRedemption = true;
        this.minimumRedemptionPoints = 50;
        this.maxRedemptionPercentOfTotal = 50.0; // Max 50% of purchase
    }
    
    public LoyaltyProgramConfig(Long storeId) {
        this();
        this.storeId = storeId;
    }
    
    // Business logic
    
    /**
     * Calculate points earned from a purchase amount
     */
    public int calculatePointsEarned(double purchaseAmount) {
        if (!enabled || purchaseAmount < minimumPurchaseAmount) {
            return 0;
        }
        
        return (int) (purchaseAmount * pointsPerCurrencyUnit);
    }
    
    /**
     * Calculate discount amount from points
     */
    public double calculateDiscountFromPoints(int points, double purchaseAmount) {
        if (!enabled || points < minimumRedemptionPoints) {
            return 0.0;
        }
        
        // Check if threshold is met
        if (!allowPartialRedemption && points < rewardThreshold) {
            return 0.0;
        }
        
        double discount = 0.0;
        
        if (allowPartialRedemption) {
            // Calculate proportional discount
            double ratio = (double) points / rewardThreshold;
            
            switch (rewardType) {
                case PERCENTAGE:
                    discount = purchaseAmount * (rewardValue / 100.0) * ratio;
                    break;
                case FIXED_AMOUNT:
                    discount = rewardValue * ratio;
                    break;
            }
        } else {
            // All-or-nothing: must have at least rewardThreshold points
            if (points >= rewardThreshold) {
                int rewardsEarned = points / rewardThreshold;
                
                switch (rewardType) {
                    case PERCENTAGE:
                        discount = purchaseAmount * (rewardValue / 100.0) * rewardsEarned;
                        break;
                    case FIXED_AMOUNT:
                        discount = rewardValue * rewardsEarned;
                        break;
                }
            }
        }
        
        // Apply maximum redemption limit
        double maxDiscount = purchaseAmount * (maxRedemptionPercentOfTotal / 100.0);
        return Math.min(discount, maxDiscount);
    }
    
    /**
     * Calculate how many points will be consumed for a given discount
     */
    public int calculatePointsToConsume(double discountAmount, double purchaseAmount) {
        if (!enabled || discountAmount <= 0) {
            return 0;
        }
        
        // Calculate based on reward type
        int pointsNeeded = 0;
        
        switch (rewardType) {
            case PERCENTAGE:
                double discountPercent = (discountAmount / purchaseAmount) * 100.0;
                pointsNeeded = (int) Math.ceil((discountPercent / rewardValue) * rewardThreshold);
                break;
            case FIXED_AMOUNT:
                double ratio = discountAmount / rewardValue;
                pointsNeeded = (int) Math.ceil(ratio * rewardThreshold);
                break;
        }
        
        return pointsNeeded;
    }
    
    /**
     * Get display description of earning rule
     */
    public String getEarningRuleDescription() {
        return String.format("Gagnez %d points pour chaque %d DH d'achat", 
                pointsPerCurrencyUnit, 1);
    }
    
    /**
     * Get display description of reward rule
     */
    public String getRewardRuleDescription() {
        String rewardDesc = rewardType == RewardType.PERCENTAGE 
                ? String.format("%.0f%% de réduction", rewardValue)
                : String.format("%.2f DH de réduction", rewardValue);
        
        return String.format("%d points = %s", rewardThreshold, rewardDesc);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStoreId() {
        return storeId;
    }
    
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getPointsPerCurrencyUnit() {
        return pointsPerCurrencyUnit;
    }
    
    public void setPointsPerCurrencyUnit(int pointsPerCurrencyUnit) {
        this.pointsPerCurrencyUnit = pointsPerCurrencyUnit;
    }
    
    public double getMinimumPurchaseAmount() {
        return minimumPurchaseAmount;
    }
    
    public void setMinimumPurchaseAmount(double minimumPurchaseAmount) {
        this.minimumPurchaseAmount = minimumPurchaseAmount;
    }
    
    public int getRewardThreshold() {
        return rewardThreshold;
    }
    
    public void setRewardThreshold(int rewardThreshold) {
        this.rewardThreshold = rewardThreshold;
    }
    
    public RewardType getRewardType() {
        return rewardType;
    }
    
    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }
    
    public double getRewardValue() {
        return rewardValue;
    }
    
    public void setRewardValue(double rewardValue) {
        this.rewardValue = rewardValue;
    }
    
    public int getPointsExpirationDays() {
        return pointsExpirationDays;
    }
    
    public void setPointsExpirationDays(int pointsExpirationDays) {
        this.pointsExpirationDays = pointsExpirationDays;
    }
    
    public boolean isAllowPartialRedemption() {
        return allowPartialRedemption;
    }
    
    public void setAllowPartialRedemption(boolean allowPartialRedemption) {
        this.allowPartialRedemption = allowPartialRedemption;
    }
    
    public int getMinimumRedemptionPoints() {
        return minimumRedemptionPoints;
    }
    
    public void setMinimumRedemptionPoints(int minimumRedemptionPoints) {
        this.minimumRedemptionPoints = minimumRedemptionPoints;
    }
    
    public double getMaxRedemptionPercentOfTotal() {
        return maxRedemptionPercentOfTotal;
    }
    
    public void setMaxRedemptionPercentOfTotal(double maxRedemptionPercentOfTotal) {
        this.maxRedemptionPercentOfTotal = maxRedemptionPercentOfTotal;
    }
    
    @Override
    public String toString() {
        return "LoyaltyProgramConfig{" +
                "enabled=" + enabled +
                ", pointsPerUnit=" + pointsPerCurrencyUnit +
                ", rewardThreshold=" + rewardThreshold +
                ", rewardValue=" + rewardValue +
                '}';
    }
    
    // Enum
    public enum RewardType {
        PERCENTAGE("Pourcentage"),
        FIXED_AMOUNT("Montant fixe");
        
        private final String displayName;
        
        RewardType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

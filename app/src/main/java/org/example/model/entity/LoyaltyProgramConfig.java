package org.example.model.entity;

public class LoyaltyProgramConfig {
    
    private Long id;
    private Long storeId; 
    private boolean enabled;
    
    private int pointsPerCurrencyUnit; 
    private double minimumPurchaseAmount; 
    
    private int rewardThreshold; 
    private RewardType rewardType; 
    private double rewardValue; 
    private int pointsExpirationDays; 
    
    private boolean allowPartialRedemption; 
    private int minimumRedemptionPoints; 
    private double maxRedemptionPercentOfTotal; 
    
    public LoyaltyProgramConfig() {
        
        this.enabled = true;
        this.pointsPerCurrencyUnit = 10; 
        this.minimumPurchaseAmount = 0.0;
        this.rewardThreshold = 100; 
        this.rewardType = RewardType.PERCENTAGE;
        this.rewardValue = 5.0; 
        this.pointsExpirationDays = 0; 
        this.allowPartialRedemption = true;
        this.minimumRedemptionPoints = 50;
        this.maxRedemptionPercentOfTotal = 50.0; 
    }
    
    public LoyaltyProgramConfig(Long storeId) {
        this();
        this.storeId = storeId;
    }
    
    public int calculatePointsEarned(double purchaseAmount) {
        if (!enabled || purchaseAmount < minimumPurchaseAmount) {
            return 0;
        }
        
        return (int) (purchaseAmount * pointsPerCurrencyUnit);
    }
    
    public double calculateDiscountFromPoints(int points, double purchaseAmount) {
        if (!enabled || points < minimumRedemptionPoints) {
            return 0.0;
        }
        
        if (!allowPartialRedemption && points < rewardThreshold) {
            return 0.0;
        }
        
        double discount = 0.0;
        
        if (allowPartialRedemption) {
            
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
        
        double maxDiscount = purchaseAmount * (maxRedemptionPercentOfTotal / 100.0);
        return Math.min(discount, maxDiscount);
    }
    
    public int calculatePointsToConsume(double discountAmount, double purchaseAmount) {
        if (!enabled || discountAmount <= 0) {
            return 0;
        }
        
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
    
    public String getEarningRuleDescription() {
        return String.format("Gagnez %d points pour chaque %d DH d'achat", 
                pointsPerCurrencyUnit, 1);
    }
    
    public String getRewardRuleDescription() {
        String rewardDesc = rewardType == RewardType.PERCENTAGE 
                ? String.format("%.0f%% de réduction", rewardValue)
                : String.format("%.2f DH de réduction", rewardValue);
        
        return String.format("%d points = %s", rewardThreshold, rewardDesc);
    }
    
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

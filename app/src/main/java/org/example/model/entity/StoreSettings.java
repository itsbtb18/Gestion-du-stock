package org.example.model.entity;

/**
 * StoreSettings - Entity representing configurable settings for a store
 * Each store has one StoreSettings instance
 */
public class StoreSettings {
    
    private Long id;
    private Long storeId; // Foreign key to Store
    private Store store; // Navigation property
    
    // Stock Management Settings
    private boolean allowNegativeStock;
    private boolean requireManagerApproval; // For discounts, returns, etc.
    
    // Financial Settings
    private double defaultVatRate; // VAT/TVA rate (e.g., 0.20 for 20%)
    private double maxDiscountPercent; // Maximum discount allowed without approval
    
    // Receipt/Invoice Settings
    private String invoiceFooterText; // Footer text on receipts
    private String invoiceHeaderText; // Header text (e.g., "Merci pour votre visite")
    
    // Loyalty Program Settings
    private boolean loyaltyProgramEnabled;
    
    // Notification Settings
    private boolean lowStockNotificationsEnabled;
    private boolean expirationAlertsEnabled;
    private int expirationAlertDays; // Alert N days before expiration
    
    // Constructors
    public StoreSettings() {
        // Set default values
        this.allowNegativeStock = false;
        this.requireManagerApproval = true;
        this.defaultVatRate = 0.20; // 20% VAT
        this.maxDiscountPercent = 10.0; // 10% max discount
        this.loyaltyProgramEnabled = true;
        this.lowStockNotificationsEnabled = true;
        this.expirationAlertsEnabled = true;
        this.expirationAlertDays = 30;
        this.invoiceFooterText = "Merci pour votre visite!";
        this.invoiceHeaderText = "";
    }
    
    public StoreSettings(Long storeId) {
        this();
        this.storeId = storeId;
    }
    
    // Business logic
    public boolean isDiscountApprovalRequired(double discountPercent) {
        return requireManagerApproval && discountPercent > maxDiscountPercent;
    }
    
    public String getFormattedVatRate() {
        return String.format("%.0f%%", defaultVatRate * 100);
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
    
    public Store getStore() {
        return store;
    }
    
    public void setStore(Store store) {
        this.store = store;
    }
    
    public boolean isAllowNegativeStock() {
        return allowNegativeStock;
    }
    
    public void setAllowNegativeStock(boolean allowNegativeStock) {
        this.allowNegativeStock = allowNegativeStock;
    }
    
    public boolean isRequireManagerApproval() {
        return requireManagerApproval;
    }
    
    public void setRequireManagerApproval(boolean requireManagerApproval) {
        this.requireManagerApproval = requireManagerApproval;
    }
    
    public double getDefaultVatRate() {
        return defaultVatRate;
    }
    
    public void setDefaultVatRate(double defaultVatRate) {
        this.defaultVatRate = defaultVatRate;
    }
    
    public double getMaxDiscountPercent() {
        return maxDiscountPercent;
    }
    
    public void setMaxDiscountPercent(double maxDiscountPercent) {
        this.maxDiscountPercent = maxDiscountPercent;
    }
    
    public String getInvoiceFooterText() {
        return invoiceFooterText;
    }
    
    public void setInvoiceFooterText(String invoiceFooterText) {
        this.invoiceFooterText = invoiceFooterText;
    }
    
    public String getInvoiceHeaderText() {
        return invoiceHeaderText;
    }
    
    public void setInvoiceHeaderText(String invoiceHeaderText) {
        this.invoiceHeaderText = invoiceHeaderText;
    }
    
    public boolean isLoyaltyProgramEnabled() {
        return loyaltyProgramEnabled;
    }
    
    public void setLoyaltyProgramEnabled(boolean loyaltyProgramEnabled) {
        this.loyaltyProgramEnabled = loyaltyProgramEnabled;
    }
    
    public boolean isLowStockNotificationsEnabled() {
        return lowStockNotificationsEnabled;
    }
    
    public void setLowStockNotificationsEnabled(boolean lowStockNotificationsEnabled) {
        this.lowStockNotificationsEnabled = lowStockNotificationsEnabled;
    }
    
    public boolean isExpirationAlertsEnabled() {
        return expirationAlertsEnabled;
    }
    
    public void setExpirationAlertsEnabled(boolean expirationAlertsEnabled) {
        this.expirationAlertsEnabled = expirationAlertsEnabled;
    }
    
    public int getExpirationAlertDays() {
        return expirationAlertDays;
    }
    
    public void setExpirationAlertDays(int expirationAlertDays) {
        this.expirationAlertDays = expirationAlertDays;
    }
    
    @Override
    public String toString() {
        return "StoreSettings{" +
                "storeId=" + storeId +
                ", vatRate=" + (defaultVatRate * 100) + "%" +
                ", loyaltyEnabled=" + loyaltyProgramEnabled +
                '}';
    }
}

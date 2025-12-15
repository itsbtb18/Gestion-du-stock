package org.example.model.entity;

public class StoreSettings {
    
    private Long id;
    private Long storeId; 
    private Store store; 
    
    private boolean allowNegativeStock;
    private boolean requireManagerApproval; 
    
    private double defaultVatRate; 
    private double maxDiscountPercent; 
    
    private String invoiceFooterText; 
    private String invoiceHeaderText; 
    
    private boolean loyaltyProgramEnabled;
    
    private boolean lowStockNotificationsEnabled;
    private boolean expirationAlertsEnabled;
    private int expirationAlertDays; 
    
    public StoreSettings() {
        
        this.allowNegativeStock = false;
        this.requireManagerApproval = true;
        this.defaultVatRate = 0.20; 
        this.maxDiscountPercent = 10.0; 
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
    
    public boolean isDiscountApprovalRequired(double discountPercent) {
        return requireManagerApproval && discountPercent > maxDiscountPercent;
    }
    
    public String getFormattedVatRate() {
        return String.format("%.0f%%", defaultVatRate * 100);
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

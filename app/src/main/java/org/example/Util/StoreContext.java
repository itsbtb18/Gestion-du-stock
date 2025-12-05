package org.example.util;

import org.example.model.entity.Store;
import org.example.model.entity.StoreSettings;
import org.example.model.entity.LoyaltyProgramConfig;

/**
 * StoreContext - Singleton class for managing the current active store context
 * Similar to SessionManager but for store-level context
 * Holds the current store, its settings, and loyalty program configuration
 */
public class StoreContext {
    
    private static volatile StoreContext instance;
    private Store currentStore;
    private StoreSettings storeSettings;
    private LoyaltyProgramConfig loyaltyConfig;
    
    private StoreContext() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance
     * Thread-safe using double-checked locking
     */
    public static StoreContext getInstance() {
        if (instance == null) {
            synchronized (StoreContext.class) {
                if (instance == null) {
                    instance = new StoreContext();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize the store context with store, settings, and loyalty config
     * Should be called at application startup after loading store from database
     */
    public void initializeStore(Store store, StoreSettings settings, LoyaltyProgramConfig loyaltyConfig) {
        this.currentStore = store;
        this.storeSettings = settings;
        this.loyaltyConfig = loyaltyConfig;
        System.out.println("Store context initialized: " + store.getName());
    }
    
    /**
     * Get the current active store
     */
    public Store getCurrentStore() {
        return currentStore;
    }
    
    /**
     * Get the current store's settings
     */
    public StoreSettings getStoreSettings() {
        return storeSettings;
    }
    
    /**
     * Get the current store's loyalty program configuration
     */
    public LoyaltyProgramConfig getLoyaltyConfig() {
        return loyaltyConfig;
    }
    
    /**
     * Check if a store is currently loaded
     */
    public boolean isStoreLoaded() {
        return currentStore != null;
    }
    
    /**
     * Get the current store ID
     */
    public Long getCurrentStoreId() {
        return currentStore != null ? currentStore.getId() : null;
    }
    
    /**
     * Get the VAT rate from settings (or default if not set)
     */
    public double getVatRate() {
        if (storeSettings != null) {
            return storeSettings.getDefaultVatRate();
        }
        return 0.20; // Default 20%
    }
    
    /**
     * Check if negative stock is allowed
     */
    public boolean isNegativeStockAllowed() {
        if (storeSettings != null) {
            return storeSettings.isAllowNegativeStock();
        }
        return false; // Default: don't allow
    }
    
    /**
     * Check if manager approval is required for discounts
     */
    public boolean isManagerApprovalRequired(double discountPercent) {
        if (storeSettings != null) {
            return storeSettings.isDiscountApprovalRequired(discountPercent);
        }
        return discountPercent > 10.0; // Default: require approval above 10%
    }
    
    /**
     * Check if loyalty program is enabled
     */
    public boolean isLoyaltyProgramEnabled() {
        if (storeSettings != null && loyaltyConfig != null) {
            return storeSettings.isLoyaltyProgramEnabled() && loyaltyConfig.isEnabled();
        }
        return false;
    }
    
    /**
     * Update the settings (after editing in UI)
     */
    public void updateSettings(StoreSettings newSettings) {
        this.storeSettings = newSettings;
        System.out.println("Store settings updated");
    }
    
    /**
     * Update the loyalty config (after editing in UI)
     */
    public void updateLoyaltyConfig(LoyaltyProgramConfig newConfig) {
        this.loyaltyConfig = newConfig;
        System.out.println("Loyalty program config updated");
    }
    
    /**
     * Update the store info (after editing in UI)
     */
    public void updateStore(Store newStore) {
        this.currentStore = newStore;
        System.out.println("Store info updated: " + newStore.getName());
    }
    
    /**
     * Clear the store context (for testing or switching stores)
     */
    public void clearContext() {
        this.currentStore = null;
        this.storeSettings = null;
        this.loyaltyConfig = null;
        System.out.println("Store context cleared");
    }
    
    /**
     * Get invoice footer text
     */
    public String getInvoiceFooterText() {
        if (storeSettings != null && storeSettings.getInvoiceFooterText() != null) {
            return storeSettings.getInvoiceFooterText();
        }
        return "Merci pour votre visite!";
    }
    
    /**
     * Get invoice header text
     */
    public String getInvoiceHeaderText() {
        if (storeSettings != null && storeSettings.getInvoiceHeaderText() != null) {
            return storeSettings.getInvoiceHeaderText();
        }
        return "";
    }
    
    @Override
    public String toString() {
        if (currentStore == null) {
            return "StoreContext{no store loaded}";
        }
        return "StoreContext{" +
                "store=" + currentStore.getName() +
                ", vatRate=" + (getVatRate() * 100) + "%" +
                ", loyaltyEnabled=" + isLoyaltyProgramEnabled() +
                '}';
    }
    
    /**
     * Prevent cloning of singleton instance
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton is not allowed");
    }
}

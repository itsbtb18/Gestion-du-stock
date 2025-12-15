package org.example.util;

import org.example.model.entity.Store;
import org.example.model.entity.StoreSettings;
import org.example.model.entity.LoyaltyProgramConfig;

public class StoreContext {
    
    private static volatile StoreContext instance;
    private Store currentStore;
    private StoreSettings storeSettings;
    private LoyaltyProgramConfig loyaltyConfig;
    
    private StoreContext() {
        
    }
    
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
    
    public void initializeStore(Store store, StoreSettings settings, LoyaltyProgramConfig loyaltyConfig) {
        this.currentStore = store;
        this.storeSettings = settings;
        this.loyaltyConfig = loyaltyConfig;
        System.out.println("Store context initialized: " + store.getName());
    }
    
    public Store getCurrentStore() {
        return currentStore;
    }
    
    public StoreSettings getStoreSettings() {
        return storeSettings;
    }
    
    public LoyaltyProgramConfig getLoyaltyConfig() {
        return loyaltyConfig;
    }
    
    public boolean isStoreLoaded() {
        return currentStore != null;
    }
    
    public Long getCurrentStoreId() {
        return currentStore != null ? currentStore.getId() : null;
    }
    
    public double getVatRate() {
        if (storeSettings != null) {
            return storeSettings.getDefaultVatRate();
        }
        return 0.20; 
    }
    
    public boolean isNegativeStockAllowed() {
        if (storeSettings != null) {
            return storeSettings.isAllowNegativeStock();
        }
        return false; 
    }
    
    public boolean isManagerApprovalRequired(double discountPercent) {
        if (storeSettings != null) {
            return storeSettings.isDiscountApprovalRequired(discountPercent);
        }
        return discountPercent > 10.0; 
    }
    
    public boolean isLoyaltyProgramEnabled() {
        if (storeSettings != null && loyaltyConfig != null) {
            return storeSettings.isLoyaltyProgramEnabled() && loyaltyConfig.isEnabled();
        }
        return false;
    }
    
    public void updateSettings(StoreSettings newSettings) {
        this.storeSettings = newSettings;
        System.out.println("Store settings updated");
    }
    
    public void updateLoyaltyConfig(LoyaltyProgramConfig newConfig) {
        this.loyaltyConfig = newConfig;
        System.out.println("Loyalty program config updated");
    }
    
    public void updateStore(Store newStore) {
        this.currentStore = newStore;
        System.out.println("Store info updated: " + newStore.getName());
    }
    
    public void clearContext() {
        this.currentStore = null;
        this.storeSettings = null;
        this.loyaltyConfig = null;
        System.out.println("Store context cleared");
    }
    
    public String getInvoiceFooterText() {
        if (storeSettings != null && storeSettings.getInvoiceFooterText() != null) {
            return storeSettings.getInvoiceFooterText();
        }
        return "Merci pour votre visite!";
    }
    
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
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton is not allowed");
    }
}

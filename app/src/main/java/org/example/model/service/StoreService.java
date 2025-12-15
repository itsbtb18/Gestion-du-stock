package org.example.model.service;

import org.example.dao.StoreDAO;
import org.example.dao.StoreSettingsDAO;
import org.example.dao.LoyaltyProgramConfigDAO;
import org.example.model.entity.Store;
import org.example.model.entity.StoreSettings;
import org.example.model.entity.LoyaltyProgramConfig;
import org.example.util.StoreContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class StoreService {
    
    private static StoreService instance;
    private final StoreDAO storeDAO;
    private final StoreSettingsDAO settingsDAO;
    private final LoyaltyProgramConfigDAO loyaltyDAO;
    
    private StoreService() {
        this.storeDAO = new StoreDAO();
        this.settingsDAO = new StoreSettingsDAO();
        this.loyaltyDAO = new LoyaltyProgramConfigDAO();
    }
    
    public static synchronized StoreService getInstance() {
        if (instance == null) {
            instance = new StoreService();
        }
        return instance;
    }
    
    public void initializeDefaultStore() {
        List<Store> stores = storeDAO.findAll();
        
        if (stores.isEmpty()) {
            System.out.println("No stores found. Creating default store...");
            
            Store defaultStore = new Store();
            defaultStore.setCode("MAIN");
            defaultStore.setName("Reb7a - Point de Vente Principal");
            defaultStore.setAddress("Adresse du magasin");
            defaultStore.setPhone("+212 XXX-XXXXXX");
            defaultStore.setEmail("contact@reb7a.com");
            defaultStore.setCurrency("MAD");
            defaultStore.setLanguage("fr");
            defaultStore.setCreatedDate(LocalDateTime.now());
            defaultStore.setActive(true);
            
            Store savedStore = storeDAO.save(defaultStore);
            
            StoreSettings defaultSettings = new StoreSettings();
            defaultSettings.setStoreId(savedStore.getId());
            defaultSettings.setAllowNegativeStock(false);
            defaultSettings.setRequireManagerApproval(true);
            defaultSettings.setDefaultVatRate(0.20); 
            defaultSettings.setMaxDiscountPercent(10.0); 
            defaultSettings.setInvoiceFooterText("Merci pour votre visite !");
            defaultSettings.setInvoiceHeaderText("Reb7a - Point de Vente");
            defaultSettings.setLoyaltyProgramEnabled(true);
            defaultSettings.setLowStockNotificationsEnabled(true);
            defaultSettings.setExpirationAlertsEnabled(true);
            defaultSettings.setExpirationAlertDays(30);
            
            settingsDAO.save(defaultSettings);
            
            LoyaltyProgramConfig defaultLoyalty = new LoyaltyProgramConfig();
            defaultLoyalty.setStoreId(savedStore.getId());
            defaultLoyalty.setEnabled(true);
            defaultLoyalty.setPointsPerCurrencyUnit(10); 
            defaultLoyalty.setMinimumPurchaseAmount(0.0);
            defaultLoyalty.setRewardThreshold(100); 
            defaultLoyalty.setRewardType(LoyaltyProgramConfig.RewardType.PERCENTAGE);
            defaultLoyalty.setRewardValue(5.0); 
            defaultLoyalty.setPointsExpirationDays(365); 
            defaultLoyalty.setAllowPartialRedemption(true);
            defaultLoyalty.setMinimumRedemptionPoints(50);
            defaultLoyalty.setMaxRedemptionPercentOfTotal(0.30); 
            
            loyaltyDAO.save(defaultLoyalty);
            
            StoreContext.getInstance().initializeStore(savedStore, defaultSettings, defaultLoyalty);
            
            System.out.println("Default store initialized: " + savedStore.getDisplayName());
        } else {
            
            Store activeStore = stores.stream()
                    .filter(Store::isActive)
                    .findFirst()
                    .orElse(stores.get(0));
            
            Optional<StoreSettings> settings = settingsDAO.findByStoreId(activeStore.getId());
            Optional<LoyaltyProgramConfig> loyalty = loyaltyDAO.findByStoreId(activeStore.getId());
            
            if (settings.isPresent() && loyalty.isPresent()) {
                StoreContext.getInstance().initializeStore(activeStore, settings.get(), loyalty.get());
                System.out.println("Store loaded: " + activeStore.getDisplayName());
            } else {
                System.err.println("Warning: Store found but settings/loyalty config missing!");
            }
        }
    }
    
    public List<Store> getAllStores() {
        return storeDAO.findAll();
    }
    
    public Optional<Store> getStoreById(Long id) {
        return storeDAO.findById(id);
    }
    
    public Optional<Store> getStoreByCode(String code) {
        return storeDAO.findByCode(code);
    }
    
    public Store createStore(Store store) {
        
        if (store.getCode() == null || store.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Store code is required");
        }
        if (store.getName() == null || store.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Store name is required");
        }
        
        Optional<Store> existing = storeDAO.findByCode(store.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Store with code " + store.getCode() + " already exists");
        }
        
        store.setCreatedDate(LocalDateTime.now());
        Store savedStore = storeDAO.save(store);
        
        StoreSettings defaultSettings = new StoreSettings();
        defaultSettings.setStoreId(savedStore.getId());
        defaultSettings.setAllowNegativeStock(false);
        defaultSettings.setRequireManagerApproval(true);
        defaultSettings.setDefaultVatRate(0.20);
        defaultSettings.setMaxDiscountPercent(10.0);
        defaultSettings.setLoyaltyProgramEnabled(true);
        defaultSettings.setLowStockNotificationsEnabled(true);
        defaultSettings.setExpirationAlertsEnabled(true);
        defaultSettings.setExpirationAlertDays(30);
        settingsDAO.save(defaultSettings);
        
        LoyaltyProgramConfig defaultLoyalty = new LoyaltyProgramConfig();
        defaultLoyalty.setStoreId(savedStore.getId());
        defaultLoyalty.setEnabled(true);
        defaultLoyalty.setPointsPerCurrencyUnit(10);
        defaultLoyalty.setMinimumPurchaseAmount(0.0);
        defaultLoyalty.setRewardThreshold(100);
        defaultLoyalty.setRewardType(LoyaltyProgramConfig.RewardType.PERCENTAGE);
        defaultLoyalty.setRewardValue(5.0);
        defaultLoyalty.setPointsExpirationDays(365);
        defaultLoyalty.setAllowPartialRedemption(true);
        defaultLoyalty.setMinimumRedemptionPoints(50);
        defaultLoyalty.setMaxRedemptionPercentOfTotal(0.30);
        loyaltyDAO.save(defaultLoyalty);
        
        return savedStore;
    }
    
    public boolean updateStore(Store store) {
        if (store.getId() == null) {
            throw new IllegalArgumentException("Store ID is required for update");
        }
        
        boolean updated = storeDAO.update(store);
        
        if (updated) {
            
            Store currentStore = StoreContext.getInstance().getCurrentStore();
            if (currentStore != null && currentStore.getId().equals(store.getId())) {
                StoreContext.getInstance().updateStore(store);
            }
        }
        
        return updated;
    }
    
    public boolean updateSettings(StoreSettings settings) {
        if (settings.getStoreId() == null) {
            throw new IllegalArgumentException("Store ID is required for settings update");
        }
        
        boolean updated = settingsDAO.update(settings);
        
        if (updated) {
            
            Store currentStore = StoreContext.getInstance().getCurrentStore();
            if (currentStore != null && currentStore.getId().equals(settings.getStoreId())) {
                StoreContext.getInstance().updateSettings(settings);
            }
        }
        
        return updated;
    }
    
    public boolean updateLoyaltyConfig(LoyaltyProgramConfig config) {
        if (config.getStoreId() == null) {
            throw new IllegalArgumentException("Store ID is required for loyalty config update");
        }
        
        boolean updated = loyaltyDAO.update(config);
        
        if (updated) {
            
            Store currentStore = StoreContext.getInstance().getCurrentStore();
            if (currentStore != null && currentStore.getId().equals(config.getStoreId())) {
                Optional<StoreSettings> settings = settingsDAO.findByStoreId(config.getStoreId());
                if (settings.isPresent()) {
                    StoreContext.getInstance().initializeStore(currentStore, settings.get(), config);
                }
            }
        }
        
        return updated;
    }
    
    public Optional<StoreSettings> getStoreSettings(Long storeId) {
        return settingsDAO.findByStoreId(storeId);
    }
    
    public Optional<LoyaltyProgramConfig> getLoyaltyConfig(Long storeId) {
        return loyaltyDAO.findByStoreId(storeId);
    }
}

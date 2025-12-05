package org.example.model.service;

import org.example.dao.PromotionDAO;
import org.example.model.entity.Promotion;
import org.example.model.entity.Promotion.PromotionScope;
import org.example.util.StoreContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PromotionService - Business logic for Promotion management
 * Follows Singleton pattern like other services in the application
 */
public class PromotionService {
    
    private static PromotionService instance;
    private final PromotionDAO promotionDAO;
    
    private PromotionService() {
        this.promotionDAO = new PromotionDAO();
    }
    
    public static synchronized PromotionService getInstance() {
        if (instance == null) {
            instance = new PromotionService();
        }
        return instance;
    }
    
    /**
     * Get all promotions for current store
     */
    public List<Promotion> getAllPromotions() {
        Long storeId = StoreContext.getInstance().getCurrentStore().getId();
        return promotionDAO.findByStoreId(storeId);
    }
    
    /**
     * Get all active promotions for current store
     */
    public List<Promotion> getActivePromotions() {
        Long storeId = StoreContext.getInstance().getCurrentStore().getId();
        return promotionDAO.findActiveByStoreId(storeId).stream()
                .filter(Promotion::isCurrentlyActive)
                .collect(Collectors.toList());
    }
    
    /**
     * Get applicable promotions for a product
     * Returns promotions that apply to the product, sorted by priority (highest first)
     */
    public List<Promotion> getApplicablePromotions(Long productId, Long categoryId) {
        List<Promotion> activePromotions = getActivePromotions();
        
        return activePromotions.stream()
                .filter(promo -> promo.appliesTo(productId, categoryId))
                .sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate best discount for a product
     * Returns the highest discount amount from all applicable promotions
     */
    public double calculateBestDiscount(Long productId, Long categoryId, double originalPrice) {
        List<Promotion> applicablePromotions = getApplicablePromotions(productId, categoryId);
        
        if (applicablePromotions.isEmpty()) {
            return 0.0;
        }
        
        // Find promotion that gives maximum discount
        return applicablePromotions.stream()
                .mapToDouble(promo -> promo.calculateDiscount(originalPrice))
                .max()
                .orElse(0.0);
    }
    
    /**
     * Get the best promotion for a product
     * Returns the promotion that gives the highest discount
     */
    public Optional<Promotion> getBestPromotion(Long productId, Long categoryId, double originalPrice) {
        List<Promotion> applicablePromotions = getApplicablePromotions(productId, categoryId);
        
        if (applicablePromotions.isEmpty()) {
            return Optional.empty();
        }
        
        return applicablePromotions.stream()
                .max((p1, p2) -> Double.compare(
                        p1.calculateDiscount(originalPrice),
                        p2.calculateDiscount(originalPrice)
                ));
    }
    
    /**
     * Get promotion by ID
     */
    public Optional<Promotion> getPromotionById(Long id) {
        return promotionDAO.findById(id);
    }
    
    /**
     * Create a new promotion
     */
    public Promotion createPromotion(Promotion promotion) {
        // Validate promotion
        validatePromotion(promotion);
        
        // Set store ID from context if not set
        if (promotion.getStoreId() == null) {
            promotion.setStoreId(StoreContext.getInstance().getCurrentStore().getId());
        }
        
        promotion.setCreatedDate(LocalDateTime.now());
        
        return promotionDAO.save(promotion);
    }
    
    /**
     * Update existing promotion
     */
    public boolean updatePromotion(Promotion promotion) {
        if (promotion.getId() == null) {
            throw new IllegalArgumentException("Promotion ID is required for update");
        }
        
        validatePromotion(promotion);
        
        return promotionDAO.update(promotion);
    }
    
    /**
     * Delete a promotion
     */
    public boolean deletePromotion(Long id) {
        return promotionDAO.delete(id);
    }
    
    /**
     * Activate a promotion
     */
    public boolean activatePromotion(Long id) {
        Optional<Promotion> promoOpt = promotionDAO.findById(id);
        if (promoOpt.isPresent()) {
            Promotion promo = promoOpt.get();
            promo.setActive(true);
            return promotionDAO.update(promo);
        }
        return false;
    }
    
    /**
     * Deactivate a promotion
     */
    public boolean deactivatePromotion(Long id) {
        Optional<Promotion> promoOpt = promotionDAO.findById(id);
        if (promoOpt.isPresent()) {
            Promotion promo = promoOpt.get();
            promo.setActive(false);
            return promotionDAO.update(promo);
        }
        return false;
    }
    
    /**
     * Validate promotion data
     */
    private void validatePromotion(Promotion promotion) {
        if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name is required");
        }
        
        if (promotion.getType() == null) {
            throw new IllegalArgumentException("Promotion type is required");
        }
        
        if (promotion.getValue() <= 0) {
            throw new IllegalArgumentException("Promotion value must be positive");
        }
        
        if (promotion.getScope() == null) {
            throw new IllegalArgumentException("Promotion scope is required");
        }
        
        // Validate target IDs for specific scopes
        if (promotion.getScope() != PromotionScope.STORE_WIDE) {
            if (promotion.getTargetIds() == null || promotion.getTargetIds().trim().isEmpty()) {
                throw new IllegalArgumentException("Target IDs are required for " + promotion.getScope() + " scope");
            }
        }
        
        // Validate dates
        if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
            if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }
    }
}

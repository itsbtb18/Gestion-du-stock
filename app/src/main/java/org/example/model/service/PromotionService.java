package org.example.model.service;

import org.example.dao.PromotionDAO;
import org.example.model.entity.Promotion;
import org.example.model.entity.Promotion.PromotionScope;
import org.example.util.StoreContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    public List<Promotion> getAllPromotions() {
        Long storeId = StoreContext.getInstance().getCurrentStore().getId();
        return promotionDAO.findByStoreId(storeId);
    }
    
    public List<Promotion> getActivePromotions() {
        Long storeId = StoreContext.getInstance().getCurrentStore().getId();
        return promotionDAO.findActiveByStoreId(storeId).stream()
                .filter(Promotion::isCurrentlyActive)
                .collect(Collectors.toList());
    }
    
    public List<Promotion> getApplicablePromotions(Long productId, Long categoryId) {
        List<Promotion> activePromotions = getActivePromotions();
        
        return activePromotions.stream()
                .filter(promo -> promo.appliesTo(productId, categoryId))
                .sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()))
                .collect(Collectors.toList());
    }
    
    public double calculateBestDiscount(Long productId, Long categoryId, double originalPrice) {
        List<Promotion> applicablePromotions = getApplicablePromotions(productId, categoryId);
        
        if (applicablePromotions.isEmpty()) {
            return 0.0;
        }
        
        return applicablePromotions.stream()
                .mapToDouble(promo -> promo.calculateDiscount(originalPrice))
                .max()
                .orElse(0.0);
    }
    
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
    
    public Optional<Promotion> getPromotionById(Long id) {
        return promotionDAO.findById(id);
    }
    
    public Promotion createPromotion(Promotion promotion) {
        
        validatePromotion(promotion);
        
        if (promotion.getStoreId() == null) {
            promotion.setStoreId(StoreContext.getInstance().getCurrentStore().getId());
        }
        
        promotion.setCreatedDate(LocalDateTime.now());
        
        return promotionDAO.save(promotion);
    }
    
    public boolean updatePromotion(Promotion promotion) {
        if (promotion.getId() == null) {
            throw new IllegalArgumentException("Promotion ID is required for update");
        }
        
        validatePromotion(promotion);
        
        return promotionDAO.update(promotion);
    }
    
    public boolean deletePromotion(Long id) {
        return promotionDAO.delete(id);
    }
    
    public boolean activatePromotion(Long id) {
        Optional<Promotion> promoOpt = promotionDAO.findById(id);
        if (promoOpt.isPresent()) {
            Promotion promo = promoOpt.get();
            promo.setActive(true);
            return promotionDAO.update(promo);
        }
        return false;
    }
    
    public boolean deactivatePromotion(Long id) {
        Optional<Promotion> promoOpt = promotionDAO.findById(id);
        if (promoOpt.isPresent()) {
            Promotion promo = promoOpt.get();
            promo.setActive(false);
            return promotionDAO.update(promo);
        }
        return false;
    }
    
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
        
        if (promotion.getScope() != PromotionScope.STORE_WIDE) {
            if (promotion.getTargetIds() == null || promotion.getTargetIds().trim().isEmpty()) {
                throw new IllegalArgumentException("Target IDs are required for " + promotion.getScope() + " scope");
            }
        }
        
        if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
            if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }
    }
}

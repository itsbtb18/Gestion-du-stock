package org.example.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Promotion {
    
    private Long id;
    private Long storeId; 
    private String name;
    private String description;
    private PromotionType type; 
    private double value; 
    private PromotionScope scope; 
    private String targetIds; 
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private int priority; 
    private LocalDateTime createdDate;
    
    public Promotion() {
        this.active = true;
        this.priority = 0;
        this.createdDate = LocalDateTime.now();
        this.type = PromotionType.PERCENTAGE;
        this.scope = PromotionScope.STORE_WIDE;
    }
    
    public Promotion(Long storeId, String name, PromotionType type, double value, 
                    LocalDate startDate, LocalDate endDate) {
        this();
        this.storeId = storeId;
        this.name = name;
        this.type = type;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public boolean isCurrentlyActive() {
        if (!active) return false;
        
        LocalDate now = LocalDate.now();
        
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        
        return true;
    }
    
    public double calculateDiscount(double originalPrice) {
        if (!isCurrentlyActive()) {
            return 0.0;
        }
        
        switch (type) {
            case PERCENTAGE:
                return originalPrice * (value / 100.0);
            case FIXED_AMOUNT:
                return Math.min(value, originalPrice); 
            default:
                return 0.0;
        }
    }
    
    public boolean appliesTo(Long productId, Long categoryId) {
        if (!isCurrentlyActive()) {
            return false;
        }
        
        switch (scope) {
            case STORE_WIDE:
                return true;
            case CATEGORY:
                return categoryId != null && getTargetIdList().contains(categoryId);
            case PRODUCT:
                return productId != null && getTargetIdList().contains(productId);
            default:
                return false;
        }
    }
    
    public List<Long> getTargetIdList() {
        if (targetIds == null || targetIds.trim().isEmpty()) {
            return List.of();
        }
        
        return Arrays.stream(targetIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
    
    public void setTargetIdList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            this.targetIds = "";
        } else {
            this.targetIds = ids.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }
    
    public String getDisplayDescription() {
        StringBuilder sb = new StringBuilder();
        
        switch (type) {
            case PERCENTAGE:
                sb.append(String.format("%.0f%% de réduction", value));
                break;
            case FIXED_AMOUNT:
                sb.append(String.format("%.2f DH de réduction", value));
                break;
        }
        
        if (description != null && !description.isEmpty()) {
            sb.append(" - ").append(description);
        }
        
        return sb.toString();
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public PromotionType getType() {
        return type;
    }
    
    public void setType(PromotionType type) {
        this.type = type;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public PromotionScope getScope() {
        return scope;
    }
    
    public void setScope(PromotionScope scope) {
        this.scope = scope;
    }
    
    public String getTargetIds() {
        return targetIds;
    }
    
    public void setTargetIds(String targetIds) {
        this.targetIds = targetIds;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", active=" + active +
                '}';
    }
    
    public enum PromotionType {
        PERCENTAGE("Pourcentage"),
        FIXED_AMOUNT("Montant fixe");
        
        private final String displayName;
        
        PromotionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PromotionScope {
        STORE_WIDE("Tout le magasin"),
        CATEGORY("Catégorie"),
        PRODUCT("Produit");
        
        private final String displayName;
        
        PromotionScope(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

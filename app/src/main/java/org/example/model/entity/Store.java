package org.example.model.entity;

import java.time.LocalDateTime;

public class Store {
    
    private Long id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String logoPath; 
    private String currency; 
    private String language; 
    private LocalDateTime createdDate;
    private boolean active;
    
    public Store() {
        this.createdDate = LocalDateTime.now();
        this.active = true;
        this.currency = "MAD"; 
        this.language = "fr"; 
    }
    
    public Store(String code, String name, String address, String phone, String email) {
        this();
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
    
    public String getDisplayName() {
        return name + " (" + code + ")";
    }
    
    public boolean hasLogo() {
        return logoPath != null && !logoPath.isEmpty();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getLogoPath() {
        return logoPath;
    }
    
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}

package org.example.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Unit tests for Produit entity
 */
class ProduitTest {
    
    private Produit produit;
    private Categorie categorie;
    
    @BeforeEach
    void setUp() {
        categorie = new Categorie();
        categorie.setId(1L);
        categorie.setCode("CAT001");
        categorie.setNom("Alimentation");
        
        produit = new Produit("PROD001", "Lait", "Lait frais", 5.50, 100, categorie, "L");
        produit.setId(1L);
        produit.setSeuilAlerte(10);
    }
    
    @Test
    @DisplayName("Should create product with valid data")
    void testProductCreation() {
        assertThat(produit.getId()).isEqualTo(1L);
        assertThat(produit.getCode()).isEqualTo("PROD001");
        assertThat(produit.getNom()).isEqualTo("Lait");
        assertThat(produit.getPrix()).isEqualTo(5.50);
        assertThat(produit.getQuantiteStock()).isEqualTo(100);
        assertThat(produit.isActif()).isTrue();
    }
    
    @Test
    @DisplayName("Should detect low stock correctly")
    void testIsStockBas() {
        produit.setQuantiteStock(15);
        assertThat(produit.isStockBas()).isFalse();
        
        produit.setQuantiteStock(10);
        assertThat(produit.isStockBas()).isTrue();
        
        produit.setQuantiteStock(5);
        assertThat(produit.isStockBas()).isTrue();
    }
    
    @Test
    @DisplayName("Should detect expiration correctly")
    void testExpiration() {
        // Not expired
        produit.setDateExpiration(LocalDate.now().plusDays(60));
        assertThat(produit.isExpire()).isFalse();
        assertThat(produit.isExpireSoon(30)).isFalse();
        
        // Expiring soon
        produit.setDateExpiration(LocalDate.now().plusDays(20));
        assertThat(produit.isExpire()).isFalse();
        assertThat(produit.isExpireSoon(30)).isTrue();
        
        // Expired
        produit.setDateExpiration(LocalDate.now().minusDays(1));
        assertThat(produit.isExpire()).isTrue();
    }
    
    @Test
    @DisplayName("Should add stock correctly")
    void testAjouterStock() {
        produit.setQuantiteStock(50);
        produit.ajouterStock(30);
        
        assertThat(produit.getQuantiteStock()).isEqualTo(80);
    }
    
    @Test
    @DisplayName("Should remove stock correctly")
    void testRetirerStock() {
        produit.setQuantiteStock(50);
        produit.retirerStock(20);
        
        assertThat(produit.getQuantiteStock()).isEqualTo(30);
    }
    
    @Test
    @DisplayName("Should throw exception when removing more stock than available")
    void testRetirerStockInsuffisant() {
        produit.setQuantiteStock(10);
        
        assertThrows(IllegalArgumentException.class, () -> {
            produit.retirerStock(20);
        });
    }
    
    @Test
    @DisplayName("Should handle null expiration date")
    void testNullExpirationDate() {
        produit.setDateExpiration(null);
        
        assertThat(produit.isExpire()).isFalse();
        assertThat(produit.isExpireSoon(30)).isFalse();
    }
}

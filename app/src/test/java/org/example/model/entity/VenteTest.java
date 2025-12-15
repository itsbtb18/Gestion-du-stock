package org.example.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;

/**
 * Unit tests for Vente entity
 */
class VenteTest {
    
    private Vente vente;
    private Client client;
    private Utilisateur vendeur;
    private Produit produit1;
    private Produit produit2;
    
    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setNom("Dupont");
        client.setPrenom("Jean");
        
        vendeur = new Utilisateur();
        vendeur.setId(1L);
        vendeur.setUsername("vendeur1");
        
        produit1 = new Produit();
        produit1.setId(1L);
        produit1.setCode("PROD001");
        produit1.setNom("Produit 1");
        produit1.setPrix(10.0);
        
        produit2 = new Produit();
        produit2.setId(2L);
        produit2.setCode("PROD002");
        produit2.setNom("Produit 2");
        produit2.setPrix(20.0);
        
        vente = new Vente(client, vendeur);
    }
    
    @Test
    @DisplayName("Should create vente with basic data")
    void testVenteCreation() {
        assertThat(vente.getClient()).isEqualTo(client);
        assertThat(vente.getVendeur()).isEqualTo(vendeur);
        assertThat(vente.getStatut()).isEqualTo("EN_COURS");
        assertThat(vente.getNumero()).isNotNull();
        assertThat(vente.getDateVente()).isNotNull();
        assertThat(vente.getLignes()).isEmpty();
    }
    
    @Test
    @DisplayName("Should add line items correctly")
    void testAjouterLigne() {
        LigneVente ligne1 = new LigneVente();
        ligne1.setProduit(produit1);
        ligne1.setQuantite(2);
        ligne1.setPrixUnitaire(10.0);
        ligne1.setSousTotal(20.0);
        
        vente.ajouterLigne(ligne1);
        
        assertThat(vente.getLignes()).hasSize(1);
        assertThat(vente.getNombreArticles()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should calculate totals correctly")
    void testRecalculerMontants() {
        LigneVente ligne1 = new LigneVente();
        ligne1.setProduit(produit1);
        ligne1.setQuantite(2);
        ligne1.setPrixUnitaire(10.0);
        ligne1.setSousTotal(20.0);
        
        LigneVente ligne2 = new LigneVente();
        ligne2.setProduit(produit2);
        ligne2.setQuantite(1);
        ligne2.setPrixUnitaire(20.0);
        ligne2.setSousTotal(20.0);
        
        vente.ajouterLigne(ligne1);
        vente.ajouterLigne(ligne2);
        
        // Total = 20 + 20 = 40
        assertThat(vente.getMontantTotal()).isEqualTo(40.0);
        
        // With TVA (20% on 40 = 8)
        assertThat(vente.getMontantTVA()).isEqualTo(8.0);
        
        // Final = 40 + 8 = 48
        assertThat(vente.getMontantFinal()).isEqualTo(48.0);
    }
    
    @Test
    @DisplayName("Should count articles correctly")
    void testGetNombreArticles() {
        LigneVente ligne1 = new LigneVente();
        ligne1.setQuantite(2);
        
        LigneVente ligne2 = new LigneVente();
        ligne2.setQuantite(3);
        
        vente.setLignes(new ArrayList<>());
        vente.getLignes().add(ligne1);
        vente.getLignes().add(ligne2);
        
        assertThat(vente.getNombreArticles()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("Should validate sale correctly")
    void testValider() {
        vente.valider();
        
        assertThat(vente.getStatut()).isEqualTo("VALIDEE");
    }
    
    @Test
    @DisplayName("Should cancel sale correctly")
    void testAnnuler() {
        vente.annuler();
        
        assertThat(vente.getStatut()).isEqualTo("ANNULEE");
    }
    
    @Test
    @DisplayName("Should build vente using Builder pattern")
    void testBuilder() {
        LigneVente ligne = new LigneVente();
        ligne.setProduit(produit1);
        ligne.setQuantite(1);
        ligne.setPrixUnitaire(10.0);
        ligne.setSousTotal(10.0);
        
        Vente builtVente = new Vente.Builder()
            .withClient(client)
            .withVendeur(vendeur)
            .addLigne(ligne)
            .withModePaiement("CARTE")
            .withStatut("VALIDEE")
            .build();
        
        assertThat(builtVente.getClient()).isEqualTo(client);
        assertThat(builtVente.getVendeur()).isEqualTo(vendeur);
        assertThat(builtVente.getModePaiement()).isEqualTo("CARTE");
        assertThat(builtVente.getStatut()).isEqualTo("VALIDEE");
        assertThat(builtVente.getLignes()).hasSize(1);
    }
}

-- ============================================
-- DONNÉES MARCHÉ ALGÉRIEN - REB7A POS SYSTEM
-- Version: 1.0.0
-- Description: Données réalistes pour le marché algérien
-- Devise: DZD (Dinar Algérien)
-- ============================================

-- ============================================
-- CATÉGORIES POUR LE MARCHÉ ALGÉRIEN
-- ============================================

DELETE FROM categories WHERE code LIKE 'ALG%' OR code LIKE 'CAT%';

INSERT INTO categories (code, nom, description, actif) VALUES
('ALG-EPICERIE', 'Épicerie', 'Produits d''épicerie sèche, conserves, pâtes, riz', TRUE),
('ALG-BOISSONS', 'Boissons', 'Eau, jus, sodas, boissons gazeuses', TRUE),
('ALG-LAITIER', 'Produits Laitiers', 'Lait, yaourt, fromage, leben', TRUE),
('ALG-BOULANG', 'Boulangerie/Pâtisserie', 'Pain, gâteaux, viennoiseries', TRUE),
('ALG-VIANDES', 'Viandes & Volailles', 'Viande bovine, ovine, poulet, dinde', TRUE),
('ALG-POISSONS', 'Poissons & Fruits de mer', 'Poissons frais, sardines, crevettes', TRUE),
('ALG-FRUITS', 'Fruits', 'Fruits frais de saison', TRUE),
('ALG-LEGUMES', 'Légumes', 'Légumes frais et locaux', TRUE),
('ALG-HYGIENE', 'Hygiène & Beauté', 'Produits d''hygiène personnelle et beauté', TRUE),
('ALG-ENTRET', 'Entretien Ménager', 'Produits de nettoyage et entretien', TRUE),
('ALG-BEBE', 'Bébé & Puériculture', 'Couches, lait infantile, soins bébé', TRUE),
('ALG-CONFIS', 'Confiserie & Snacks', 'Chocolat, bonbons, biscuits, chips', TRUE),
('ALG-SURGELE', 'Surgelés', 'Produits surgelés, glaces', TRUE),
('ALG-CONDIM', 'Condiments & Épices', 'Épices, sauces, assaisonnements', TRUE),
('ALG-TRADITI', 'Produits Traditionnels', 'Couscous, hrira, produits du terroir', TRUE);

-- ============================================
-- PRODUITS MARCHÉ ALGÉRIEN (Prix en DZD)
-- ============================================

DELETE FROM produits WHERE code LIKE 'DZ%';

-- ===================== ÉPICERIE =====================
INSERT INTO produits (code, nom, description, prix, quantite_stock, seuil_alerte, categorie_id, unite, fournisseur, emplacement, actif) VALUES
-- Huiles
('DZ-HUILE-SAFIA-1L', 'Huile Safia 1L', 'Huile de table végétale Safia', 390.00, 150, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'bouteille', 'Cevital', 'A1-01', TRUE),
('DZ-HUILE-SAFIA-5L', 'Huile Safia 5L', 'Huile de table végétale Safia bidon', 1850.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'bidon', 'Cevital', 'A1-02', TRUE),
('DZ-HUILE-OLIVE-1L', 'Huile d''Olive Vierge 1L', 'Huile d''olive extra vierge Kabylie', 1500.00, 60, 10, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'bouteille', 'Huilerie Kabyle', 'A1-03', TRUE),
('DZ-HUILE-FLEURIAL', 'Huile Fleurial 1L', 'Huile de tournesol', 420.00, 100, 15, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'bouteille', 'Cevital', 'A1-04', TRUE),

-- Sucre
('DZ-SUCRE-1KG', 'Sucre Blanc 1kg', 'Sucre blanc raffiné', 130.00, 300, 50, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'Cevital', 'A2-01', TRUE),
('DZ-SUCRE-5KG', 'Sucre Blanc 5kg', 'Sucre blanc raffiné sac', 620.00, 120, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'Cevital', 'A2-02', TRUE),
('DZ-SUCRE-GLACE', 'Sucre Glace 500g', 'Sucre glace pour pâtisserie', 95.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'sachet', 'Cevital', 'A2-03', TRUE),

-- Farines et Semoules
('DZ-FARINE-1KG', 'Farine Blanche 1kg', 'Farine de blé tout usage', 75.00, 200, 40, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'AGRODIV', 'A3-01', TRUE),
('DZ-FARINE-5KG', 'Farine Blanche 5kg', 'Farine de blé tout usage sac', 350.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'AGRODIV', 'A3-02', TRUE),
('DZ-SEMOULE-1KG', 'Semoule Fine 1kg', 'Semoule de blé dur fine', 110.00, 180, 30, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'SIM', 'A3-03', TRUE),
('DZ-SEMOULE-5KG', 'Semoule Fine 5kg', 'Semoule de blé dur fine sac', 520.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'SIM', 'A3-04', TRUE),
('DZ-COUSCOUS-1KG', 'Couscous Moyen 1kg', 'Couscous traditionnel grain moyen', 180.00, 150, 25, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'SIM', 'A3-05', TRUE),

-- Pâtes
('DZ-PATES-RYMCO', 'Pâtes Rymco 500g', 'Pâtes spaghetti Rymco', 95.00, 200, 30, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'sachet', 'Rymco', 'A4-01', TRUE),
('DZ-PATES-AMOR', 'Pâtes Amor Ben Amor 500g', 'Pâtes coquillettes', 90.00, 180, 30, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'sachet', 'Amor Ben Amor', 'A4-02', TRUE),
('DZ-VERMICELLE', 'Vermicelles Cheveux d''ange 500g', 'Vermicelles pour soupe', 85.00, 150, 25, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'sachet', 'Rymco', 'A4-03', TRUE),

-- Riz et Légumineuses
('DZ-RIZ-MEDIUM-1KG', 'Riz Rond 1kg', 'Riz grain rond', 180.00, 200, 30, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'ONAB', 'A5-01', TRUE),
('DZ-RIZ-LONG-1KG', 'Riz Long 1kg', 'Riz grain long parfumé', 280.00, 150, 25, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'kg', 'ONAB', 'A5-02', TRUE),
('DZ-LENTILLES', 'Lentilles Vertes 500g', 'Lentilles vertes séchées', 180.00, 120, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'sachet', 'ONAB', 'A5-03', TRUE),
('DZ-POIS-CHICHE', 'Pois Chiches 500g', 'Pois chiches séchés', 170.00, 130, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'sachet', 'ONAB', 'A5-04', TRUE),
('DZ-HARICOTS', 'Haricots Blancs 500g', 'Haricots blancs secs', 195.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'sachet', 'ONAB', 'A5-05', TRUE),

-- Conserves
('DZ-TOMATE-CONC', 'Double Concentré Tomate 400g', 'Concentré de tomates', 145.00, 200, 30, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'boîte', 'Benamor', 'A6-01', TRUE),
('DZ-TOMATE-PELEE', 'Tomates Pelées 400g', 'Tomates pelées en conserve', 120.00, 150, 25, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'boîte', 'Benamor', 'A6-02', TRUE),
('DZ-HARISSA', 'Harissa 380g', 'Sauce piquante traditionnelle', 180.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'pot', 'CAB', 'A6-03', TRUE),
('DZ-THON-CONSERVE', 'Thon à l''huile 160g', 'Thon en conserve à l''huile', 420.00, 120, 20, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'boîte', 'Pescado', 'A6-04', TRUE),
('DZ-SARDINES', 'Sardines à l''huile 125g', 'Sardines en conserve', 180.00, 180, 30, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'boîte', 'Pescado', 'A6-05', TRUE),
('DZ-OLIVES-NOIRES', 'Olives Noires 400g', 'Olives noires dénoyautées', 250.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-EPICERIE'), 'bocal', 'Conserveries Algériennes', 'A6-06', TRUE),

-- ===================== BOISSONS =====================
-- Eau
('DZ-EAU-IFRI-1.5L', 'Eau Ifri 1.5L', 'Eau minérale naturelle Ifri', 50.00, 400, 80, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Ifri', 'B1-01', TRUE),
('DZ-EAU-IFRI-0.5L', 'Eau Ifri 0.5L', 'Eau minérale naturelle Ifri', 30.00, 600, 100, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Ifri', 'B1-02', TRUE),
('DZ-EAU-GUEDILA', 'Eau Guedila 1.5L', 'Eau de source Guedila', 45.00, 350, 60, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Guedila', 'B1-03', TRUE),
('DZ-EAU-SAIDA', 'Eau Saïda 1.5L', 'Eau minérale Saïda', 45.00, 300, 50, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Saïda', 'B1-04', TRUE),
('DZ-EAU-PACK-6', 'Pack Eau Ifri 6x1.5L', 'Pack 6 bouteilles eau Ifri', 280.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'pack', 'Ifri', 'B1-05', TRUE),

-- Jus et Sodas
('DZ-JUS-ROUIBA-1L', 'Jus Rouiba Orange 1L', 'Jus d''orange Rouiba', 160.00, 200, 30, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'NCA Rouiba', 'B2-01', TRUE),
('DZ-JUS-ROUIBA-POMME', 'Jus Rouiba Pomme 1L', 'Jus de pomme Rouiba', 160.00, 180, 30, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'NCA Rouiba', 'B2-02', TRUE),
('DZ-JUS-ROUIBA-MULTI', 'Jus Rouiba Multivitamines 1L', 'Jus multivitamines Rouiba', 180.00, 150, 25, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'NCA Rouiba', 'B2-03', TRUE),
('DZ-COCA-COLA-1L', 'Coca Cola 1L', 'Boisson gazeuse Coca Cola', 140.00, 250, 40, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Fruital', 'B3-01', TRUE),
('DZ-COCA-COLA-2L', 'Coca Cola 2L', 'Boisson gazeuse Coca Cola grande', 210.00, 150, 30, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Fruital', 'B3-02', TRUE),
('DZ-FANTA-1L', 'Fanta Orange 1L', 'Boisson gazeuse Fanta', 130.00, 200, 35, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Fruital', 'B3-03', TRUE),
('DZ-SPRITE-1L', 'Sprite 1L', 'Boisson gazeuse Sprite', 130.00, 180, 30, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Fruital', 'B3-04', TRUE),
('DZ-HAMOUD-SELECTO', 'Hamoud Boualem Selecto 1L', 'Limonade Selecto traditionnelle', 100.00, 200, 35, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Hamoud Boualem', 'B4-01', TRUE),
('DZ-HAMOUD-SLIM', 'Hamoud Boualem Slim 1L', 'Limonade Slim', 95.00, 180, 30, (SELECT id FROM categories WHERE code='ALG-BOISSONS'), 'bouteille', 'Hamoud Boualem', 'B4-02', TRUE),

-- ===================== PRODUITS LAITIERS =====================
('DZ-LAIT-CANDIA-1L', 'Lait Candia 1L', 'Lait demi-écrémé UHT', 95.00, 300, 50, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'L', 'Tchin-Lait Candia', 'C1-01', TRUE),
('DZ-LAIT-VITALAIT', 'Lait Vitalait 1L', 'Lait entier UHT', 90.00, 250, 40, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'L', 'Vitalait', 'C1-02', TRUE),
('DZ-LAIT-SACHET', 'Lait en Sachet 1L', 'Lait pasteurisé sachet', 35.00, 400, 80, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'L', 'Giplait', 'C1-03', TRUE),
('DZ-LAIT-POUDRE-500G', 'Lait en Poudre 500g', 'Lait en poudre instantané', 580.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'sachet', 'Lactalis', 'C1-04', TRUE),
('DZ-YAOURT-SOUMMAM', 'Yaourt Soummam Nature 125g', 'Yaourt nature Soummam', 45.00, 200, 40, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'pot', 'Soummam', 'C2-01', TRUE),
('DZ-YAOURT-FRUITES', 'Yaourt Soummam Fruité 125g', 'Yaourt aux fruits Soummam', 50.00, 180, 35, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'pot', 'Soummam', 'C2-02', TRUE),
('DZ-YAOURT-TIFRA', 'Yaourt Tifra Nature 125g', 'Yaourt nature Tifra', 40.00, 200, 40, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'pot', 'Tifra-Lait', 'C2-03', TRUE),
('DZ-LEBEN-1L', 'Lben 1L', 'Lait fermenté traditionnel', 85.00, 150, 30, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'L', 'Soummam', 'C2-04', TRUE),
('DZ-FROMAGE-VQIRI', 'Fromage La Vache qui Rit 8 portions', 'Fromage fondu 8 portions', 280.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'boîte', 'Bel Algérie', 'C3-01', TRUE),
('DZ-FROMAGE-KIRI', 'Fromage Kiri 6 portions', 'Fromage frais 6 portions', 250.00, 90, 18, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'boîte', 'Bel Algérie', 'C3-02', TRUE),
('DZ-BEURRE-250G', 'Beurre 250g', 'Beurre doux', 480.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-LAITIER'), 'plaquette', 'Soummam', 'C3-03', TRUE),

-- ===================== BOULANGERIE =====================
('DZ-PAIN-BAGUETTE', 'Baguette de Pain', 'Pain baguette tradition', 15.00, 100, 30, (SELECT id FROM categories WHERE code='ALG-BOULANG'), 'pièce', 'Boulangerie Locale', 'D1-01', TRUE),
('DZ-PAIN-GALETTE', 'Galette (Kesra)', 'Pain traditionnel galette', 40.00, 80, 20, (SELECT id FROM categories WHERE code='ALG-BOULANG'), 'pièce', 'Boulangerie Locale', 'D1-02', TRUE),
('DZ-PAIN-MIE', 'Pain de Mie 400g', 'Pain de mie tranché', 120.00, 60, 15, (SELECT id FROM categories WHERE code='ALG-BOULANG'), 'sachet', 'La Baguette Dorée', 'D1-03', TRUE),
('DZ-CROISSANT', 'Croissant au Beurre', 'Croissant frais', 60.00, 50, 15, (SELECT id FROM categories WHERE code='ALG-BOULANG'), 'pièce', 'Boulangerie Locale', 'D2-01', TRUE),
('DZ-GATEAU-SAMSA', 'Samsa (kg)', 'Gâteau traditionnel samsa', 2800.00, 20, 5, (SELECT id FROM categories WHERE code='ALG-BOULANG'), 'kg', 'Pâtisserie Traditionnelle', 'D2-02', TRUE),
('DZ-GATEAU-MAKROUT', 'Makrout (kg)', 'Gâteau traditionnel aux dattes', 2500.00, 25, 5, (SELECT id FROM categories WHERE code='ALG-BOULANG'), 'kg', 'Pâtisserie Traditionnelle', 'D2-03', TRUE),

-- ===================== VIANDES =====================
('DZ-VIANDE-BOEUF', 'Viande Bœuf (kg)', 'Viande bovine fraîche', 2200.00, 30, 10, (SELECT id FROM categories WHERE code='ALG-VIANDES'), 'kg', 'Boucherie Centrale', 'E1-01', TRUE),
('DZ-VIANDE-MOUTON', 'Viande Mouton (kg)', 'Viande ovine fraîche', 2800.00, 25, 8, (SELECT id FROM categories WHERE code='ALG-VIANDES'), 'kg', 'Boucherie Centrale', 'E1-02', TRUE),
('DZ-POULET-ENTIER', 'Poulet Entier (kg)', 'Poulet frais entier', 520.00, 40, 15, (SELECT id FROM categories WHERE code='ALG-VIANDES'), 'kg', 'Aviculture Nationale', 'E2-01', TRUE),
('DZ-ESCALOPE-POULET', 'Escalope de Poulet (kg)', 'Escalope de poulet fraîche', 950.00, 30, 10, (SELECT id FROM categories WHERE code='ALG-VIANDES'), 'kg', 'Aviculture Nationale', 'E2-02', TRUE),
('DZ-DINDE', 'Dinde (kg)', 'Viande de dinde fraîche', 780.00, 25, 8, (SELECT id FROM categories WHERE code='ALG-VIANDES'), 'kg', 'Aviculture Nationale', 'E2-03', TRUE),
('DZ-OEUF-30', 'Œufs (plateau 30)', 'Plateau de 30 œufs frais', 580.00, 60, 15, (SELECT id FROM categories WHERE code='ALG-VIANDES'), 'plateau', 'Aviculture Nationale', 'E3-01', TRUE),
('DZ-OEUF-6', 'Œufs (boîte 6)', 'Boîte de 6 œufs', 130.00, 80, 20, (SELECT id FROM categories WHERE code='ALG-VIANDES'), 'boîte', 'Aviculture Nationale', 'E3-02', TRUE),

-- ===================== FRUITS =====================
('DZ-ORANGE', 'Oranges (kg)', 'Oranges fraîches', 180.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-FRUITS'), 'kg', 'Producteur Local', 'F1-01', TRUE),
('DZ-POMME', 'Pommes (kg)', 'Pommes fraîches', 350.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-FRUITS'), 'kg', 'Producteur Local', 'F1-02', TRUE),
('DZ-BANANE', 'Bananes (kg)', 'Bananes importées', 400.00, 70, 15, (SELECT id FROM categories WHERE code='ALG-FRUITS'), 'kg', 'Importateur', 'F1-03', TRUE),
('DZ-RAISIN', 'Raisin (kg)', 'Raisin frais de saison', 450.00, 50, 10, (SELECT id FROM categories WHERE code='ALG-FRUITS'), 'kg', 'Producteur Local', 'F1-04', TRUE),
('DZ-DATTES-DEGLET', 'Dattes Deglet Nour (kg)', 'Dattes premium Deglet Nour', 1200.00, 40, 10, (SELECT id FROM categories WHERE code='ALG-FRUITS'), 'kg', 'Oasis du Sud', 'F1-05', TRUE),
('DZ-CITRON', 'Citrons (kg)', 'Citrons frais', 250.00, 60, 15, (SELECT id FROM categories WHERE code='ALG-FRUITS'), 'kg', 'Producteur Local', 'F1-06', TRUE),

-- ===================== LÉGUMES =====================
('DZ-TOMATE', 'Tomates (kg)', 'Tomates fraîches', 120.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'kg', 'Marché Gros', 'G1-01', TRUE),
('DZ-OIGNON', 'Oignons (kg)', 'Oignons frais', 80.00, 150, 30, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'kg', 'Marché Gros', 'G1-02', TRUE),
('DZ-PDT', 'Pommes de Terre (kg)', 'Pommes de terre fraîches', 90.00, 200, 40, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'kg', 'Marché Gros', 'G1-03', TRUE),
('DZ-CAROTTE', 'Carottes (kg)', 'Carottes fraîches', 100.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'kg', 'Marché Gros', 'G1-04', TRUE),
('DZ-COURGETTE', 'Courgettes (kg)', 'Courgettes fraîches', 150.00, 60, 12, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'kg', 'Marché Gros', 'G1-05', TRUE),
('DZ-POIVRON', 'Poivrons (kg)', 'Poivrons frais', 280.00, 50, 10, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'kg', 'Marché Gros', 'G1-06', TRUE),
('DZ-LAITUE', 'Salade Laitue', 'Laitue fraîche', 80.00, 40, 10, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'pièce', 'Marché Gros', 'G1-07', TRUE),
('DZ-PERSIL', 'Persil (botte)', 'Persil frais', 30.00, 60, 15, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'botte', 'Marché Gros', 'G1-08', TRUE),
('DZ-CORIANDRE', 'Coriandre (botte)', 'Coriandre fraîche', 30.00, 50, 12, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'botte', 'Marché Gros', 'G1-09', TRUE),
('DZ-AIL', 'Ail (tête)', 'Ail frais', 50.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-LEGUMES'), 'tête', 'Marché Gros', 'G1-10', TRUE),

-- ===================== HYGIÈNE & BEAUTÉ =====================
('DZ-SAVON-VENUS', 'Savon Vénus 125g', 'Savon de toilette', 95.00, 150, 30, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'pièce', 'Henkel Algérie', 'H1-01', TRUE),
('DZ-SAVON-LUX', 'Savon Lux 100g', 'Savon parfumé Lux', 120.00, 120, 25, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'pièce', 'Unilever', 'H1-02', TRUE),
('DZ-GEL-DOUCHE', 'Gel Douche 250ml', 'Gel douche parfumé', 280.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'bouteille', 'Unilever', 'H1-03', TRUE),
('DZ-SHAMPOING-HEAD', 'Shampooing Head & Shoulders 200ml', 'Shampooing antipelliculaire', 650.00, 60, 12, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'bouteille', 'P&G', 'H2-01', TRUE),
('DZ-SHAMPOING-CLEAR', 'Shampooing Clear 200ml', 'Shampooing Clear', 580.00, 70, 15, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'bouteille', 'Unilever', 'H2-02', TRUE),
('DZ-DENTIFRICE-SIGNAL', 'Dentifrice Signal 100ml', 'Dentifrice fluoré', 220.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'tube', 'Unilever', 'H3-01', TRUE),
('DZ-DENTIFRICE-COLGATE', 'Dentifrice Colgate 100ml', 'Dentifrice Colgate', 250.00, 90, 18, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'tube', 'Colgate', 'H3-02', TRUE),
('DZ-BROSSE-DENT', 'Brosse à Dents', 'Brosse à dents adulte', 180.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'pièce', 'Oral-B', 'H3-03', TRUE),
('DZ-DEODORANT', 'Déodorant Rexona 50ml', 'Déodorant anti-transpirant', 450.00, 60, 12, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'pièce', 'Unilever', 'H4-01', TRUE),
('DZ-PAP-TOILETTE', 'Papier Toilette (pack 4)', 'Papier hygiénique 4 rouleaux', 180.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'pack', 'Sirius', 'H5-01', TRUE),
('DZ-MOUCHOIRS', 'Mouchoirs en Papier (pack)', 'Mouchoirs en papier', 90.00, 120, 25, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'pack', 'Sirius', 'H5-02', TRUE),
('DZ-COTON', 'Coton (sachet 100g)', 'Coton hydrophile', 150.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-HYGIENE'), 'sachet', 'Local', 'H5-03', TRUE),

-- ===================== ENTRETIEN MÉNAGER =====================
('DZ-JAVEL-1L', 'Eau de Javel 1L', 'Javel désinfectante', 85.00, 150, 30, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'bouteille', 'Henkel', 'I1-01', TRUE),
('DZ-JAVEL-2L', 'Eau de Javel 2L', 'Javel désinfectante grande', 150.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'bouteille', 'Henkel', 'I1-02', TRUE),
('DZ-LESSIVE-ARIEL', 'Lessive Ariel 2kg', 'Lessive en poudre Ariel', 1100.00, 60, 12, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'sachet', 'P&G', 'I2-01', TRUE),
('DZ-LESSIVE-TIDE', 'Lessive Tide 2kg', 'Lessive en poudre Tide', 980.00, 70, 15, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'sachet', 'P&G', 'I2-02', TRUE),
('DZ-LESSIVE-OMO', 'Lessive Omo 2kg', 'Lessive en poudre Omo', 950.00, 65, 12, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'sachet', 'Henkel', 'I2-03', TRUE),
('DZ-VAISSELLE-MIR', 'Liquide Vaisselle Mir 500ml', 'Détergent vaisselle', 180.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'bouteille', 'Henkel', 'I3-01', TRUE),
('DZ-VAISSELLE-FAIRY', 'Liquide Vaisselle Fairy 500ml', 'Détergent vaisselle Fairy', 280.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'bouteille', 'P&G', 'I3-02', TRUE),
('DZ-NETTOIE-SOL', 'Nettoyant Sol 1L', 'Nettoyant multi-surfaces', 220.00, 90, 18, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'bouteille', 'Local', 'I4-01', TRUE),
('DZ-EPONGE', 'Éponges (pack 3)', 'Éponges à récurer', 120.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'pack', 'Local', 'I4-02', TRUE),
('DZ-SACHET-POUB', 'Sacs Poubelle (rouleau 20)', 'Sacs poubelle 50L', 150.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-ENTRET'), 'rouleau', 'Local', 'I4-03', TRUE),

-- ===================== BÉBÉ & PUÉRICULTURE =====================
('DZ-COUCHE-PAMPERS-M', 'Couches Pampers M (40)', 'Couches bébé taille M', 2200.00, 40, 10, (SELECT id FROM categories WHERE code='ALG-BEBE'), 'paquet', 'P&G', 'J1-01', TRUE),
('DZ-COUCHE-MOLFIX-M', 'Couches Molfix M (40)', 'Couches bébé taille M', 1800.00, 50, 12, (SELECT id FROM categories WHERE code='ALG-BEBE'), 'paquet', 'Hayat', 'J1-02', TRUE),
('DZ-LAIT-INFANTILE', 'Lait Infantile 1er âge 400g', 'Lait en poudre 0-6 mois', 2800.00, 30, 8, (SELECT id FROM categories WHERE code='ALG-BEBE'), 'boîte', 'Nestlé', 'J2-01', TRUE),
('DZ-LINGETTES-BEBE', 'Lingettes Bébé (72)', 'Lingettes nettoyantes bébé', 420.00, 60, 15, (SELECT id FROM categories WHERE code='ALG-BEBE'), 'paquet', 'P&G', 'J2-02', TRUE),

-- ===================== CONFISERIE & SNACKS =====================
('DZ-CHOCOLAT-TWIX', 'Twix', 'Barre chocolatée Twix', 90.00, 100, 25, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'pièce', 'Mars', 'K1-01', TRUE),
('DZ-CHOCOLAT-SNICKERS', 'Snickers', 'Barre chocolatée Snickers', 95.00, 100, 25, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'pièce', 'Mars', 'K1-02', TRUE),
('DZ-CHOCOLAT-KINDER', 'Kinder Bueno', 'Barre Kinder Bueno', 120.00, 80, 20, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'pièce', 'Ferrero', 'K1-03', TRUE),
('DZ-BISCUIT-OREO', 'Biscuits Oreo 154g', 'Biscuits Oreo chocolat', 280.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'paquet', 'Mondelez', 'K2-01', TRUE),
('DZ-BISCUIT-BIMO', 'Biscuits Bimo 200g', 'Biscuits Bimo vanille', 160.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'paquet', 'Bimo', 'K2-02', TRUE),
('DZ-CHIPS-LAYS', 'Chips Lay''s 160g', 'Chips nature Lay''s', 280.00, 80, 18, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'sachet', 'PepsiCo', 'K3-01', TRUE),
('DZ-CHIPS-PRINGLES', 'Pringles Original 165g', 'Chips Pringles', 550.00, 50, 12, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'tube', 'Kellogg''s', 'K3-02', TRUE),
('DZ-BONBON-HARIBO', 'Haribo Fraises Tagada 120g', 'Bonbons Haribo', 250.00, 70, 15, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'sachet', 'Haribo', 'K4-01', TRUE),
('DZ-CHEWING-GUM', 'Chewing-gum Mentos', 'Chewing-gum menthe', 40.00, 150, 30, (SELECT id FROM categories WHERE code='ALG-CONFIS'), 'pièce', 'Mentos', 'K4-02', TRUE),

-- ===================== CONDIMENTS & ÉPICES =====================
('DZ-SEL-1KG', 'Sel de Table 1kg', 'Sel fin iodé', 50.00, 200, 40, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'kg', 'ENASEL', 'L1-01', TRUE),
('DZ-POIVRE-NOIR', 'Poivre Noir Moulu 50g', 'Poivre noir moulu', 180.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'sachet', 'Local', 'L1-02', TRUE),
('DZ-CUMIN', 'Cumin Moulu 50g', 'Cumin moulu traditionnel', 150.00, 90, 18, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'sachet', 'Local', 'L1-03', TRUE),
('DZ-PAPRIKA', 'Paprika Doux 50g', 'Paprika doux moulu', 160.00, 75, 15, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'sachet', 'Local', 'L1-04', TRUE),
('DZ-RAS-HANOUT', 'Ras El Hanout 50g', 'Mélange d''épices traditionnel', 200.00, 60, 12, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'sachet', 'Local', 'L1-05', TRUE),
('DZ-KETCHUP', 'Ketchup Heinz 340g', 'Sauce ketchup', 380.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'bouteille', 'Heinz', 'L2-01', TRUE),
('DZ-MAYONNAISE', 'Mayonnaise 250g', 'Mayonnaise', 320.00, 70, 15, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'pot', 'Local', 'L2-02', TRUE),
('DZ-MOUTARDE', 'Moutarde Dijon 200g', 'Moutarde de Dijon', 280.00, 60, 12, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'pot', 'Amora', 'L2-03', TRUE),
('DZ-VINAIGRE', 'Vinaigre Blanc 1L', 'Vinaigre d''alcool', 120.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-CONDIM'), 'bouteille', 'Local', 'L2-04', TRUE),

-- ===================== PRODUITS TRADITIONNELS =====================
('DZ-COUSCOUS-FIN', 'Couscous Fin SIM 1kg', 'Couscous grain fin traditionnel', 195.00, 120, 25, (SELECT id FROM categories WHERE code='ALG-TRADITI'), 'kg', 'SIM', 'M1-01', TRUE),
('DZ-CHORBA-PKT', 'Chorba Frik (sachet 500g)', 'Blé vert concassé pour chorba', 280.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-TRADITI'), 'sachet', 'Local', 'M1-02', TRUE),
('DZ-MIEL-500G', 'Miel Pur 500g', 'Miel d''abeilles naturel', 1800.00, 40, 10, (SELECT id FROM categories WHERE code='ALG-TRADITI'), 'pot', 'Apiculteur Local', 'M2-01', TRUE),
('DZ-CONFITURE', 'Confiture de Figues 370g', 'Confiture traditionnelle', 380.00, 60, 12, (SELECT id FROM categories WHERE code='ALG-TRADITI'), 'pot', 'Local', 'M2-02', TRUE),
('DZ-CAFE-TURK', 'Café Turc 250g', 'Café moulu traditionnel', 650.00, 80, 15, (SELECT id FROM categories WHERE code='ALG-TRADITI'), 'paquet', 'Café d''Alger', 'M3-01', TRUE),
('DZ-THE-VERT', 'Thé Vert 250g', 'Thé vert en vrac', 420.00, 100, 20, (SELECT id FROM categories WHERE code='ALG-TRADITI'), 'paquet', 'Import Chine', 'M3-02', TRUE),
('DZ-AMLOU', 'Amlou 500g', 'Pâte d''amandes aux argan', 2500.00, 25, 5, (SELECT id FROM categories WHERE code='ALG-TRADITI'), 'pot', 'Artisanal', 'M2-03', TRUE);

-- ============================================
-- FOURNISSEURS ALGÉRIENS
-- ============================================

-- Note: La table fournisseurs n'existe pas dans le schéma actuel
-- Les fournisseurs sont stockés directement dans la colonne 'fournisseur' de produits
-- Voici la liste des fournisseurs principaux pour référence:
-- 
-- Cevital (Huile, Sucre) - Béjaïa
-- SIM (Semoule, Couscous) - Blida  
-- Rymco (Pâtes) - Alger
-- NCA Rouiba (Jus) - Rouiba
-- Hamoud Boualem (Limonades) - Alger
-- Tchin-Lait Candia (Lait) - Béjaïa
-- Soummam (Produits laitiers) - Akbou
-- Ifri (Eau minérale) - Ifri Ouzellaguen
-- Henkel Algérie (Produits ménagers)
-- P&G (Hygiène, Couches)
-- Benamor (Conserves) - Guelma
-- Amor Ben Amor (Pâtes) - Constantine

-- ============================================
-- CLIENTS ALGÉRIENS
-- ============================================

DELETE FROM clients WHERE code LIKE 'CLIDZ%';

INSERT INTO clients (code, nom, prenom, telephone, email, adresse, type_client, total_achats, points_fidelite, actif) VALUES
('CLIDZ001', 'Benali', 'Mohamed', '0555123456', 'm.benali@email.dz', '15 Rue Didouche Mourad, Alger Centre', 'FIDELE', 45000.00, 450, TRUE),
('CLIDZ002', 'Hamidi', 'Fatima', '0661234567', 'f.hamidi@email.dz', '23 Boulevard Amirouche, Tizi Ouzou', 'VIP', 125000.00, 1250, TRUE),
('CLIDZ003', 'Boudiaf', 'Ahmed', '0770123456', 'a.boudiaf@email.dz', '8 Rue Colonel Lotfi, Oran', 'NORMAL', 12500.00, 125, TRUE),
('CLIDZ004', 'Mebarki', 'Khadija', '0550987654', 'k.mebarki@email.dz', '45 Cité des 500 Logements, Constantine', 'FIDELE', 67000.00, 670, TRUE),
('CLIDZ005', 'Zidane', 'Yacine', '0666543210', 'y.zidane@email.dz', '12 Rue 1er Novembre, Blida', 'NORMAL', 8500.00, 85, TRUE),
('CLIDZ006', 'Ait Slimane', 'Nadia', '0778654321', 'n.aitslimane@email.dz', '78 Hai Essalem, Béjaïa', 'VIP', 185000.00, 1850, TRUE),
('CLIDZ007', 'Messaoudi', 'Karim', '0551112233', 'k.messaoudi@email.dz', '3 Rue Larbi Ben M''Hidi, Sétif', 'FIDELE', 38000.00, 380, TRUE),
('CLIDZ008', 'Belkacem', 'Samira', '0667778899', 's.belkacem@email.dz', '56 Avenue de l''ALN, Annaba', 'NORMAL', 15600.00, 156, TRUE);

-- ============================================
-- UTILISATEURS ALGÉRIENS
-- ============================================

-- Mot de passe par défaut: password123 (hashé avec BCrypt)
DELETE FROM utilisateurs WHERE username LIKE 'dz_%';

INSERT INTO utilisateurs (username, password, nom, prenom, email, role, actif) VALUES
('dz_admin', '$2a$12$oq7YVpwQBfWWJ.wZaLbqO.HslUpDWHTzCDI5FVwH3WG7iq5e8dfS', 'Benmoussa', 'Rachid', 'r.benmoussa@reb7a.dz', 'ADMIN', TRUE),
('dz_gerant', '$2a$12$Jo5aVpCYKVJW6oPqhePlm.X3UmYgafVgkZlWmyEihr1lxgFKXY2EG', 'Khelifi', 'Amina', 'a.khelifi@reb7a.dz', 'GERANT', TRUE),
('dz_caissier1', '$2a$12$wWc8WR9gOgsZpK/RvkdvaOwRJqGJu0I8kIebA4si1Uj9eqF6K/Hvq', 'Saidi', 'Omar', 'o.saidi@reb7a.dz', 'CAISSIER', TRUE),
('dz_caissier2', '$2a$12$wWc8WR9gOgsZpK/RvkdvaOwRJqGJu0I8kIebA4si1Uj9eqF6K/Hvq', 'Taleb', 'Houria', 'h.taleb@reb7a.dz', 'CAISSIER', TRUE);

-- ============================================
-- FIN DES DONNÉES MARCHÉ ALGÉRIEN
-- ============================================

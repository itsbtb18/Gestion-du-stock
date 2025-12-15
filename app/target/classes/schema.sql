-- Reb7a POS System - H2 Database Schema
-- Version: 1.0.0
-- Description: Complete database schema for Point of Vente system

-- ============================================
-- DROP TABLES (if exists - for clean setup)
-- ============================================

DROP TABLE IF EXISTS transactions_fidelite;
DROP TABLE IF EXISTS lignes_vente;
DROP TABLE IF EXISTS ventes;
DROP TABLE IF EXISTS cartes_fidelite;
DROP TABLE IF EXISTS mouvements_stock;
DROP TABLE IF EXISTS produits;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS utilisateurs;

-- ============================================
-- CREATE TABLES
-- ============================================

-- Categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE
);

-- Produits table
CREATE TABLE produits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL,
    quantite_stock INT DEFAULT 0,
    seuil_alerte INT DEFAULT 10,
    categorie_id BIGINT,
    unite VARCHAR(20),
    date_expiration DATE,
    fournisseur VARCHAR(200),
    emplacement VARCHAR(100),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categorie_id) REFERENCES categories(id)
);

-- Clients table
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) UNIQUE,
    email VARCHAR(200),
    adresse TEXT,
    type_client VARCHAR(20) DEFAULT 'NORMAL',
    date_inscription DATE DEFAULT CURRENT_DATE,
    total_achats DECIMAL(10,2) DEFAULT 0.00,
    points_fidelite INT DEFAULT 0,
    actif BOOLEAN DEFAULT TRUE
);

-- Cartes fidelite table
CREATE TABLE cartes_fidelite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    date_creation DATE DEFAULT CURRENT_DATE,
    date_expiration DATE,
    points_accumules INT DEFAULT 0,
    points_utilises INT DEFAULT 0,
    statut VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Utilisateurs table
CREATE TABLE utilisateurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(200),
    role VARCHAR(20) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE
);

-- Ventes table
CREATE TABLE ventes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    date_vente TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    client_id BIGINT,
    vendeur_id BIGINT NOT NULL,
    montant_total DECIMAL(10,2) DEFAULT 0.00,
    montant_remise DECIMAL(10,2) DEFAULT 0.00,
    montant_tva DECIMAL(10,2) DEFAULT 0.00,
    montant_final DECIMAL(10,2) DEFAULT 0.00,
    mode_paiement VARCHAR(50),
    statut VARCHAR(20) DEFAULT 'EN_COURS',
    commentaire TEXT,
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (vendeur_id) REFERENCES utilisateurs(id)
);

-- Lignes vente table
CREATE TABLE lignes_vente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vente_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    quantite INT NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    remise DECIMAL(5,2) DEFAULT 0.00,
    sous_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (vente_id) REFERENCES ventes(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produits(id)
);

-- Mouvements stock table
CREATE TABLE mouvements_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produit_id BIGINT NOT NULL,
    type_mouvement VARCHAR(20) NOT NULL,
    quantite INT NOT NULL,
    date_mouvement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    utilisateur_id BIGINT,
    motif TEXT,
    FOREIGN KEY (produit_id) REFERENCES produits(id),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
);

-- Transactions fidelite table
CREATE TABLE transactions_fidelite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carte_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    points INT NOT NULL,
    date_transaction TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vente_id BIGINT,
    description TEXT,
    FOREIGN KEY (carte_id) REFERENCES cartes_fidelite(id) ON DELETE CASCADE,
    FOREIGN KEY (vente_id) REFERENCES ventes(id)
);

-- ============================================
-- CREATE INDEXES for better performance
-- ============================================

CREATE INDEX idx_produit_code ON produits(code);
CREATE INDEX idx_produit_nom ON produits(nom);
CREATE INDEX idx_produit_categorie ON produits(categorie_id);
CREATE INDEX idx_client_telephone ON clients(telephone);
CREATE INDEX idx_vente_date ON ventes(date_vente);
CREATE INDEX idx_vente_client ON ventes(client_id);
CREATE INDEX idx_vente_numero ON ventes(numero);
CREATE INDEX idx_mouvement_date ON mouvements_stock(date_mouvement);
CREATE INDEX idx_mouvement_produit ON mouvements_stock(produit_id);

-- ============================================
-- INSERT SAMPLE DATA
-- ============================================

-- Sample Categories
INSERT INTO categories (code, nom, description) VALUES
('CAT001', 'Alimentation', 'Produits alimentaires'),
('CAT002', 'Boissons', 'Boissons et liquides'),
('CAT003', 'Hygiène', 'Produits d''hygiène et beauté'),
('CAT004', 'Entretien', 'Produits d''entretien ménager'),
('CAT005', 'Épicerie', 'Épicerie sèche et conserves');

-- Sample Products
INSERT INTO produits (code, nom, description, prix, quantite_stock, seuil_alerte, categorie_id, unite, fournisseur, emplacement) VALUES
('PROD001', 'Lait demi-écrémé 1L', 'Lait UHT demi-écrémé', 1.20, 150, 20, 2, 'L', 'Laiterie Centrale', 'A1-01'),
('PROD002', 'Pain de campagne', 'Pain traditionnel 400g', 1.50, 80, 15, 1, 'pièce', 'Boulangerie Locale', 'B2-05'),
('PROD003', 'Eau minérale 1.5L', 'Eau de source naturelle', 0.50, 300, 50, 2, 'L', 'Sources du Sud', 'A2-10'),
('PROD004', 'Pâtes Spaghetti 500g', 'Pâtes de blé dur', 1.80, 200, 30, 5, 'kg', 'Pâtes Italia', 'C1-03'),
('PROD005', 'Savon liquide 300ml', 'Savon antibactérien', 3.50, 120, 25, 3, 'L', 'HygiènePlus', 'D1-02'),
('PROD006', 'Huile d''olive 750ml', 'Huile d''olive extra vierge', 8.90, 60, 10, 5, 'L', 'Méditerranée', 'C2-01'),
('PROD007', 'Riz Basmati 1kg', 'Riz long grain premium', 3.20, 150, 20, 5, 'kg', 'OrientFood', 'C1-05'),
('PROD008', 'Dentifrice 75ml', 'Dentifrice au fluor', 2.50, 90, 15, 3, 'pièce', 'DentalCare', 'D1-06'),
('PROD009', 'Jus d''orange 1L', 'Pur jus sans sucre ajouté', 2.80, 100, 20, 2, 'L', 'FruitsFrais', 'A2-05'),
('PROD010', 'Lessive liquide 2L', 'Lessive concentrée 40 lavages', 12.50, 45, 10, 4, 'L', 'CleanHome', 'D2-01');

-- Sample Admin User (passwords are BCrypt hashed)
-- Plain text passwords: admin/admin, gerant/gerant, caissier1/caisse
INSERT INTO utilisateurs (username, password, nom, prenom, email, role) VALUES
('admin', '$2a$12$oq7YVpwQBfWWJ.wZaLbqO.HslUpDWHTzCDI5FVwH3WG7iq5e8dfS', 'Admin', 'System', 'admin@reb7a.com', 'ADMIN'),
('gerant', '$2a$12$Jo5aVpCYKVJW6oPqhePlm.X3UmYgafVgkZlWmyEihr1lxgFKXY2EG', 'Martin', 'Sophie', 'sophie.martin@reb7a.com', 'GERANT'),
('caissier1', '$2a$12$wWc8WR9gOgsZpK/RvkdvaOwRJqGJu0I8kIebA4si1Uj9eqF6K/Hvq', 'Dubois', 'Pierre', 'pierre.dubois@reb7a.com', 'CAISSIER');

-- Sample Clients
INSERT INTO clients (code, nom, prenom, telephone, email, adresse, type_client, total_achats, points_fidelite) VALUES
('CLI001', 'Dupont', 'Jean', '0612345678', 'jean.dupont@email.com', '15 Rue de la Paix, 75001 Paris', 'FIDELE', 850.50, 8505),
('CLI002', 'Bernard', 'Marie', '0687654321', 'marie.bernard@email.com', '23 Avenue des Champs, 75008 Paris', 'VIP', 1520.00, 15200),
('CLI003', 'Petit', 'Luc', '0698765432', 'luc.petit@email.com', '8 Rue du Commerce, 75015 Paris', 'NORMAL', 125.30, 1253);

-- Sample Loyalty Cards
INSERT INTO cartes_fidelite (numero, client_id, date_expiration, points_accumules) VALUES
('CARD' || UNIX_TIMESTAMP(), 1, DATEADD('YEAR', 2, CURRENT_DATE), 8505),
('CARD' || (UNIX_TIMESTAMP() + 1), 2, DATEADD('YEAR', 2, CURRENT_DATE), 15200);

-- Sample Stock Movements
INSERT INTO mouvements_stock (produit_id, type_mouvement, quantite, utilisateur_id, motif) VALUES
(1, 'ENTREE', 150, 2, 'Réception commande fournisseur'),
(3, 'ENTREE', 300, 2, 'Stock initial'),
(4, 'ENTREE', 200, 2, 'Réception commande'),
(5, 'SORTIE', 5, 3, 'Vente en magasin');

-- ============================================
-- CREATE VIEWS FOR REPORTING
-- ============================================

-- View: Current Stock Levels
CREATE VIEW v_stock_actuel AS
SELECT 
    p.id,
    p.code,
    p.nom,
    c.nom AS categorie,
    p.quantite_stock,
    p.seuil_alerte,
    CASE 
        WHEN p.quantite_stock <= p.seuil_alerte THEN 'BAS'
        WHEN p.quantite_stock = 0 THEN 'RUPTURE'
        ELSE 'NORMAL'
    END AS statut_stock,
    p.prix,
    (p.quantite_stock * p.prix) AS valeur_stock
FROM produits p
LEFT JOIN categories c ON p.categorie_id = c.id
WHERE p.actif = TRUE;

-- View: Sales Summary
CREATE VIEW v_ventes_resume AS
SELECT 
    v.id,
    v.numero,
    v.date_vente,
    c.nom AS client_nom,
    c.prenom AS client_prenom,
    u.username AS vendeur,
    v.montant_total,
    v.montant_remise,
    v.montant_final,
    v.mode_paiement,
    v.statut,
    (SELECT COUNT(*) FROM lignes_vente WHERE vente_id = v.id) AS nombre_articles
FROM ventes v
LEFT JOIN clients c ON v.client_id = c.id
LEFT JOIN utilisateurs u ON v.vendeur_id = u.id;

-- View: Client Statistics
CREATE VIEW v_clients_statistiques AS
SELECT 
    c.id,
    c.code,
    c.nom,
    c.prenom,
    c.type_client,
    c.total_achats,
    c.points_fidelite,
    (SELECT COUNT(*) FROM ventes WHERE client_id = c.id AND statut = 'VALIDEE') AS nombre_achats,
    CASE 
        WHEN (SELECT COUNT(*) FROM ventes WHERE client_id = c.id) > 0 
        THEN c.total_achats / (SELECT COUNT(*) FROM ventes WHERE client_id = c.id)
        ELSE 0 
    END AS panier_moyen
FROM clients c
WHERE c.actif = TRUE;

-- ============================================
-- SAMPLE QUERIES FOR TESTING
-- ============================================

-- Check all tables
-- SELECT COUNT(*) AS count FROM categories;
-- SELECT COUNT(*) AS count FROM produits;
-- SELECT COUNT(*) AS count FROM clients;
-- SELECT COUNT(*) AS count FROM utilisateurs;

-- View stock levels
-- SELECT * FROM v_stock_actuel ORDER BY statut_stock DESC, quantite_stock ASC;

-- View products with low stock
-- SELECT * FROM v_stock_actuel WHERE statut_stock IN ('BAS', 'RUPTURE');

-- ============================================
-- DEFAULT DATA - Admin User
-- ============================================

-- Insert default admin user
-- Username: admin
-- Password: admin123
-- BCrypt hash generated with: BCrypt.hashpw("admin123", BCrypt.gensalt())
INSERT INTO utilisateurs (username, password, nom, prenom, email, role, actif) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'Administrator', 'System', 'admin@reb7a.com', 'ADMIN', true);

-- ============================================
-- GRANTS (if using specific users)
-- ============================================

-- For H2, default user 'sa' has all privileges
-- GRANT ALL ON ALL TO sa;

-- End of schema

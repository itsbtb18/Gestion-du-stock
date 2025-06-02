CREATE TABLE achat (
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       dateAchat TEXT NOT NULL,
                       montantTotal REAL NOT NULL,
                       id_fournisseur TEXT NOT NULL
);

-- Insertion de 3 exemples d'achats
INSERT INTO achat (id,dateAchat, montantTotal, id_fournisseur) VALUES
                                                                   (1,'2024-01-15', 1250.75, 'FOURS001'),
                                                                   (2,'2024-02-03', 890.50, 'FOURS002'),
                                                                   (3,'2024-02-20', 2100.00, 'FOURS001');
-- Création de la table categorie
CREATE TABLE categorie (
                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                           nom TEXT NOT NULL UNIQUE
);

-- Insertion de 3 exemples de catégories de supermarché
INSERT INTO categorie (id,nom) VALUES
                                   (1,'Fruits et Légumes'),
                                   (2,'Produits Laitiers'),
                                   (3,'Boucherie-Charcuterie');
-- Création de la table client
CREATE TABLE client (
                        nom TEXT NOT NULL,
                        prenom TEXT NOT NULL,
                        telephone TEXT PRIMARY KEY
);

-- Insertion de 3 exemples de clients
INSERT INTO client (nom, prenom, telephone) VALUES
                                                ('Benali', 'Ahmed', '0551234567'),
                                                ('Kadi', 'Fatima', '0661987654'),
                                                ('Mokrani', 'Youcef', '0771122334');
CREATE TABLE fournisseur (
                             id INTEGER PRIMARY KEY AUTOINCREMENT,
                             nom TEXT NOT NULL,
                             adresse TEXT NOT NULL,
                             telephone TEXT NOT NULL UNIQUE
);

-- Insertion de 3 exemples de fournisseurs
INSERT INTO fournisseur (id,nom, adresse, telephone) VALUES
                                                         (1,'Distribo Algérie', 'Zone Industrielle Rouiba, Alger', '023456789'),
                                                         (2,'Fresh Market Supply', 'Rue Larbi Ben M''hidi, Oran', '041234567'),
                                                         (3,'Atlas Distribution', 'Avenue Mohamed V, Constantine', '031987654');
CREATE TABLE produit (
                         id TEXT PRIMARY KEY,
                         nom TEXT NOT NULL,
                         id_categorie INTEGER NOT NULL,
                         prixAchat REAL NOT NULL,
                         prixVente REAL NOT NULL,
                         dateExpiration DATE,
                         codeBar TEXT UNIQUE,
                         id_fournisseur INTEGER NOT NULL,
                         FOREIGN KEY (id_categorie) REFERENCES categorie(id),
                         FOREIGN KEY (id_fournisseur) REFERENCES fournisseur(id)
);

-- Insertion de 3 exemples de produits de supermarché
INSERT INTO produit (id, nom, id_categorie, prixAchat, prixVente, dateExpiration, codeBar, id_fournisseur) VALUES
                                                                                                               ('PROD001', 'Pommes Golden 1kg', 1, 180.00, 250.00, '2024-07-15', '3760123456789', 1),
                                                                                                               ('PROD002', 'Lait UHT Candia 1L', 2, 85.00, 120.00, '2024-08-30', '6223000123456', 2),
                                                                                                               ('PROD003', 'Escalope de Poulet 500g', 3, 450.00, 650.00, '2024-06-10', '2130000987654', 3);
CREATE TABLE rayon (
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       nom TEXT NOT NULL UNIQUE
);

-- Insertion de 3 exemples de rayons de supermarché
INSERT INTO rayon (id,nom) VALUES
                               (1,'Rayon Frais'),
                               (2,'Rayon Épicerie'),
                               (3,'Rayon Surgelé');
CREATE TABLE vente (
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       date TEXT NOT NULL,
                       montantTotal REAL NOT NULL,
                       modePaiement TEXT NOT NULL,
                       clientId INTEGER,
                       FOREIGN KEY (clientId) REFERENCES client(telephone)
);

-- Insertion de 3 exemples de ventes
INSERT INTO vente (id,date, montantTotal, modePaiement, clientId) VALUES
                                                                      (1,'2024-06-01 14:30:00', 1250.50, 'Espèces', NULL),
                                                                      (2,'2024-06-01 16:45:00', 890.75, 'Carte Bancaire', NULL),
                                                                      (3,'2024-06-02 10:15:00', 2100.00, 'Chèque', NULL
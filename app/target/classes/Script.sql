-- Suppression des tables si elles existent
DROP TABLE IF EXISTS vente;
DROP TABLE IF EXISTS achat;
DROP TABLE IF EXISTS produit;
DROP TABLE IF EXISTS categorie;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS fournisseur;
DROP TABLE IF EXISTS rayon;

-- Création de la table rayon
CREATE TABLE IF NOT EXISTS rayon (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     nom TEXT NOT NULL,
                                     description TEXT
);

-- Création de la table categorie
CREATE TABLE IF NOT EXISTS categorie (
                                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                                         nom TEXT NOT NULL,
                                         description TEXT
);

-- Création de la table fournisseur
CREATE TABLE IF NOT EXISTS fournisseur (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           nom TEXT NOT NULL,
                                           adresse TEXT,
                                           telephone TEXT,
                                           email TEXT
);

-- Création de la table client
CREATE TABLE IF NOT EXISTS client (
                                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      nom TEXT NOT NULL,
                                      adresse TEXT,
                                      telephone TEXT,
                                      email TEXT
);

-- Création de la table produit
CREATE TABLE IF NOT EXISTS produit (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       nom TEXT NOT NULL,
                                       description TEXT,
                                       prix_unitaire REAL NOT NULL,
                                       quantite_stock INTEGER DEFAULT 0,
                                       id_categorie INTEGER,
                                       id_rayon INTEGER,
                                       FOREIGN KEY (id_categorie) REFERENCES categorie(id),
    FOREIGN KEY (id_rayon) REFERENCES rayon(id)
    );

-- Création de la table achat
CREATE TABLE IF NOT EXISTS achat (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     date_achat DATE NOT NULL,
                                     quantite INTEGER NOT NULL,
                                     prix_total REAL NOT NULL,
                                     id_produit INTEGER,
                                     id_fournisseur INTEGER,
                                     FOREIGN KEY (id_produit) REFERENCES produit(id),
    FOREIGN KEY (id_fournisseur) REFERENCES fournisseur(id)
    );

-- Création de la table vente
CREATE TABLE IF NOT EXISTS vente (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     date_vente DATE NOT NULL,
                                     quantite INTEGER NOT NULL,
                                     prix_total REAL NOT NULL,
                                     id_produit INTEGER,
                                     id_client INTEGER,
                                     FOREIGN KEY (id_produit) REFERENCES produit(id),
    FOREIGN KEY (id_client) REFERENCES client(id)
    );

-- Insertion de données de test
INSERT INTO rayon (nom, description) VALUES
                                         ('Alimentaire', 'Produits alimentaires'),
                                         ('Cosmétiques', 'Produits de beauté');

INSERT INTO categorie (nom, description) VALUES
                                             (1,'Fruits', 'Fruits frais'),
                                             ('Légumes', 'Légumes frais');

INSERT INTO fournisseur (nom, adresse, telephone, email) VALUES
                                                             ('Fournisseur A', 'Adresse A', '0123456789', 'fournisseurA@email.com'),
                                                             ('Fournisseur B', 'Adresse B', '9876543210', 'fournisseurB@email.com');

INSERT INTO client (nom, adresse, telephone, email) VALUES
                                                        ('Client X', 'Adresse X', '1122334455', 'clientX@email.com'),
                                                        ('Client Y', 'Adresse Y', '5544332211', 'clientY@email.com');
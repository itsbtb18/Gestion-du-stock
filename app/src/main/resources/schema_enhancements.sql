-- ============================================
-- SCHEMA ENHANCEMENTS - Additional Tables
-- For Gestion de Stock Application
-- ============================================

-- Fournisseurs (Suppliers) table
CREATE TABLE IF NOT EXISTS fournisseurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    contact VARCHAR(100),
    telephone VARCHAR(20),
    email VARCHAR(100),
    adresse TEXT,
    ville VARCHAR(100),
    pays VARCHAR(50),
    code_postal VARCHAR(10),
    site_web VARCHAR(200),
    numero_tva VARCHAR(50),
    conditions_paiement VARCHAR(50),
    note_performe DECIMAL(3,1) DEFAULT 3.0,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE,
    commentaire TEXT
);

-- Bons de Commande (Purchase Orders) table
CREATE TABLE IF NOT EXISTS bons_commande (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) UNIQUE NOT NULL,
    date_commande DATE NOT NULL,
    date_livraison_prevue DATE,
    date_livraison_reelle DATE,
    fournisseur_id BIGINT NOT NULL,
    commande_par_user_id BIGINT NOT NULL,
    statut VARCHAR(20) DEFAULT 'BROUILLON',
    montant_total DECIMAL(10,2) DEFAULT 0.00,
    montant_paye DECIMAL(10,2) DEFAULT 0.00,
    mode_paiement VARCHAR(50),
    commentaire TEXT,
    FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id),
    FOREIGN KEY (commande_par_user_id) REFERENCES utilisateurs(id)
);

-- Lignes Bon de Commande table
CREATE TABLE IF NOT EXISTS lignes_bon_commande (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bon_commande_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    quantite_commandee INT NOT NULL,
    quantite_recue INT DEFAULT 0,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    montant_ligne DECIMAL(10,2) NOT NULL,
    commentaire TEXT,
    FOREIGN KEY (bon_commande_id) REFERENCES bons_commande(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produits(id)
);

-- Retours (Returns) table
CREATE TABLE IF NOT EXISTS retours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_retour VARCHAR(20) UNIQUE NOT NULL,
    date_retour DATE NOT NULL,
    vente_originale_id BIGINT,
    client_id BIGINT,
    traite_par_user_id BIGINT,
    motif TEXT,
    type_retour VARCHAR(20) DEFAULT 'PARTIEL',
    statut VARCHAR(20) DEFAULT 'EN_COURS',
    montant_total DECIMAL(10,2) DEFAULT 0.00,
    montant_rembourse DECIMAL(10,2) DEFAULT 0.00,
    mode_paiement VARCHAR(50),
    numero_credit_note VARCHAR(20),
    commentaire TEXT,
    FOREIGN KEY (vente_originale_id) REFERENCES ventes(id),
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (traite_par_user_id) REFERENCES utilisateurs(id)
);

-- Lignes Retour table
CREATE TABLE IF NOT EXISTS lignes_retour (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    retour_id BIGINT NOT NULL,
    ligne_vente_originale_id BIGINT,
    produit_id BIGINT NOT NULL,
    quantite_retournee INT NOT NULL,
    quantite_originale INT,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    montant_ligne DECIMAL(10,2) NOT NULL,
    raison_retour TEXT,
    produit_endommage BOOLEAN DEFAULT FALSE,
    commentaire TEXT,
    FOREIGN KEY (retour_id) REFERENCES retours(id) ON DELETE CASCADE,
    FOREIGN KEY (ligne_vente_originale_id) REFERENCES lignes_vente(id),
    FOREIGN KEY (produit_id) REFERENCES produits(id)
);

-- Depenses (Expenses) table
CREATE TABLE IF NOT EXISTS depenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) UNIQUE NOT NULL,
    date_depense DATE NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    mode_paiement VARCHAR(50),
    fournisseur_id BIGINT,
    saisi_par_user_id BIGINT,
    numero_facture VARCHAR(50),
    recurrente BOOLEAN DEFAULT FALSE,
    frequence VARCHAR(20),
    justificatif VARCHAR(255),
    commentaire TEXT,
    FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id),
    FOREIGN KEY (saisi_par_user_id) REFERENCES utilisateurs(id)
);

-- Caisses (Cash Registers) table
CREATE TABLE IF NOT EXISTS caisses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_caisse VARCHAR(20) NOT NULL,
    caissier_id BIGINT NOT NULL,
    date_ouverture TIMESTAMP NOT NULL,
    date_fermeture TIMESTAMP,
    solde_depart_especes DECIMAL(10,2) DEFAULT 0.00,
    solde_fin_especes DECIMAL(10,2),
    total_ventes_especes DECIMAL(10,2) DEFAULT 0.00,
    total_ventes_carte DECIMAL(10,2) DEFAULT 0.00,
    total_ventes_autre DECIMAL(10,2) DEFAULT 0.00,
    total_depenses DECIMAL(10,2) DEFAULT 0.00,
    ecart DECIMAL(10,2),
    statut VARCHAR(20) DEFAULT 'OUVERTE',
    commentaire TEXT,
    FOREIGN KEY (caissier_id) REFERENCES utilisateurs(id)
);

-- Emplacements (Locations/Warehouses) table
CREATE TABLE IF NOT EXISTS emplacements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    adresse TEXT,
    ville VARCHAR(100),
    responsable VARCHAR(100),
    telephone VARCHAR(20),
    date_creation DATE DEFAULT CURRENT_DATE,
    actif BOOLEAN DEFAULT TRUE,
    commentaire TEXT
);

-- Lots (Batch/Lot Tracking) table
CREATE TABLE IF NOT EXISTS lots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_lot VARCHAR(50) UNIQUE NOT NULL,
    produit_id BIGINT NOT NULL,
    quantite INT NOT NULL,
    date_fabrication DATE,
    date_expiration DATE,
    fournisseur_id BIGINT,
    bon_commande_id BIGINT,
    emplacement VARCHAR(100),
    actif BOOLEAN DEFAULT TRUE,
    commentaire TEXT,
    FOREIGN KEY (produit_id) REFERENCES produits(id),
    FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id),
    FOREIGN KEY (bon_commande_id) REFERENCES bons_commande(id)
);

-- Transferts Stock (Stock Transfers) table
CREATE TABLE IF NOT EXISTS transferts_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_transfert VARCHAR(20) UNIQUE NOT NULL,
    date_transfert TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    emplacement_source_id BIGINT NOT NULL,
    emplacement_destination_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    lot_id BIGINT,
    quantite INT NOT NULL,
    demande_par_user_id BIGINT,
    valide_par_user_id BIGINT,
    statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
    motif TEXT,
    commentaire TEXT,
    FOREIGN KEY (emplacement_source_id) REFERENCES emplacements(id),
    FOREIGN KEY (emplacement_destination_id) REFERENCES emplacements(id),
    FOREIGN KEY (produit_id) REFERENCES produits(id),
    FOREIGN KEY (lot_id) REFERENCES lots(id),
    FOREIGN KEY (demande_par_user_id) REFERENCES utilisateurs(id),
    FOREIGN KEY (valide_par_user_id) REFERENCES utilisateurs(id)
);

-- Avis Clients (Customer Feedback) table
CREATE TABLE IF NOT EXISTS avis_clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vente_id BIGINT,
    client_id BIGINT NOT NULL,
    note INT NOT NULL,
    commentaire TEXT,
    categorie VARCHAR(50),
    date_avis DATE DEFAULT CURRENT_DATE,
    traite BOOLEAN DEFAULT FALSE,
    reponse TEXT,
    reponse_par_user_id BIGINT,
    FOREIGN KEY (vente_id) REFERENCES ventes(id),
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (reponse_par_user_id) REFERENCES utilisateurs(id),
    CHECK (note >= 1 AND note <= 5)
);

-- Audit Log table
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_heure TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    utilisateur_id BIGINT,
    action VARCHAR(50) NOT NULL,
    entite VARCHAR(50),
    entite_id BIGINT,
    description TEXT,
    valeur_avant TEXT,
    valeur_apres TEXT,
    adresse_ip VARCHAR(50),
    succes BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    priorite VARCHAR(20) DEFAULT 'NORMALE',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_envoi TIMESTAMP,
    destinataire_id BIGINT,
    lu BOOLEAN DEFAULT FALSE,
    date_lecture TIMESTAMP,
    lien_action VARCHAR(255),
    envoi_email BOOLEAN DEFAULT FALSE,
    envoi_sms BOOLEAN DEFAULT FALSE,
    entite_related VARCHAR(50),
    entite_id BIGINT,
    FOREIGN KEY (destinataire_id) REFERENCES utilisateurs(id)
);

-- ============================================
-- CREATE INDEXES for new tables
-- ============================================

CREATE INDEX idx_fournisseur_code ON fournisseurs(code);
CREATE INDEX idx_fournisseur_nom ON fournisseurs(nom);
CREATE INDEX idx_bon_commande_numero ON bons_commande(numero);
CREATE INDEX idx_bon_commande_fournisseur ON bons_commande(fournisseur_id);
CREATE INDEX idx_bon_commande_date ON bons_commande(date_commande);
CREATE INDEX idx_retour_numero ON retours(numero_retour);
CREATE INDEX idx_retour_client ON retours(client_id);
CREATE INDEX idx_retour_date ON retours(date_retour);
CREATE INDEX idx_depense_date ON depenses(date_depense);
CREATE INDEX idx_depense_categorie ON depenses(categorie);
CREATE INDEX idx_caisse_date_ouverture ON caisses(date_ouverture);
CREATE INDEX idx_caisse_caissier ON caisses(caissier_id);
CREATE INDEX idx_lot_numero ON lots(numero_lot);
CREATE INDEX idx_lot_produit ON lots(produit_id);
CREATE INDEX idx_transfert_numero ON transferts_stock(numero_transfert);
CREATE INDEX idx_avis_client ON avis_clients(client_id);
CREATE INDEX idx_avis_note ON avis_clients(note);
CREATE INDEX idx_audit_utilisateur ON audit_log(utilisateur_id);
CREATE INDEX idx_audit_date ON audit_log(date_heure);
CREATE INDEX idx_notification_destinataire ON notifications(destinataire_id);
CREATE INDEX idx_notification_lu ON notifications(lu);

-- ============================================
-- SAMPLE DATA for new tables
-- ============================================

-- Sample Suppliers
INSERT INTO fournisseurs (code, nom, contact, telephone, email, ville, pays) VALUES
('FOUR001', 'Laiterie Centrale', 'Jean Dupont', '+33123456789', 'contact@laiterie-centrale.fr', 'Paris', 'France'),
('FOUR002', 'Sources du Sud', 'Marie Martin', '+33198765432', 'info@sources-du-sud.fr', 'Marseille', 'France'),
('FOUR003', 'Pâtes Italia', 'Giuseppe Rossi', '+390123456789', 'export@pates-italia.it', 'Rome', 'Italie');

-- Sample Locations
INSERT INTO emplacements (code, nom, type, ville) VALUES
('MAG01', 'Magasin Principal', 'MAGASIN', 'Paris'),
('ENT01', 'Entrepôt Central', 'ENTREPOT', 'Paris'),
('RES01', 'Réserve Magasin', 'RESERVE', 'Paris');

-- Sample Expense
INSERT INTO depenses (numero, date_depense, categorie, description, montant, mode_paiement, saisi_par_user_id) VALUES
('DEP001', CURRENT_DATE, 'ELECTRICITE', 'Facture électricité mois en cours', 450.00, 'Virement', 1);

-- ============================================
-- UPDATE EXISTING TABLES
-- ============================================

-- Add barcode column to produits if not exists
ALTER TABLE produits ADD COLUMN IF NOT EXISTS barcode VARCHAR(20);
ALTER TABLE produits ADD COLUMN IF NOT EXISTS lot_id BIGINT;
ALTER TABLE produits ADD COLUMN IF NOT EXISTS emplacement_id BIGINT;

-- Add niveau_fidelite to clients
ALTER TABLE clients ADD COLUMN IF NOT EXISTS niveau_fidelite VARCHAR(20) DEFAULT 'BRONZE';
ALTER TABLE clients ADD COLUMN IF NOT EXISTS date_anniversaire DATE;

-- Add foreign keys for new relationships
-- ALTER TABLE produits ADD FOREIGN KEY IF NOT EXISTS (lot_id) REFERENCES lots(id);
-- ALTER TABLE produits ADD FOREIGN KEY IF NOT EXISTS (emplacement_id) REFERENCES emplacements(id);

-- End of schema enhancements

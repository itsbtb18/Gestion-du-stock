-- ============================================
-- STORE CONFIGURATION TABLES
-- Extension to existing schema for multi-store support
-- ============================================

-- Stores table
CREATE TABLE IF NOT EXISTS stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(200),
    logo_path VARCHAR(500),
    currency VARCHAR(10) DEFAULT 'MAD',
    language VARCHAR(10) DEFAULT 'fr',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Store Settings table (1:1 relationship with stores)
CREATE TABLE IF NOT EXISTS store_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL UNIQUE,
    allow_negative_stock BOOLEAN DEFAULT FALSE,
    require_manager_approval BOOLEAN DEFAULT TRUE,
    default_vat_rate DECIMAL(5,4) DEFAULT 0.2000,
    max_discount_percent DECIMAL(5,2) DEFAULT 10.00,
    invoice_footer_text TEXT,
    invoice_header_text TEXT,
    loyalty_program_enabled BOOLEAN DEFAULT TRUE,
    low_stock_notifications_enabled BOOLEAN DEFAULT TRUE,
    expiration_alerts_enabled BOOLEAN DEFAULT TRUE,
    expiration_alert_days INT DEFAULT 30,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
);

-- Promotions table
CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL, -- PERCENTAGE, FIXED_AMOUNT
    value DECIMAL(10,2) NOT NULL,
    scope VARCHAR(20) NOT NULL, -- STORE_WIDE, CATEGORY, PRODUCT
    target_ids TEXT, -- Comma-separated IDs
    start_date DATE,
    end_date DATE,
    active BOOLEAN DEFAULT TRUE,
    priority INT DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
);

-- Loyalty Program Configuration table
CREATE TABLE IF NOT EXISTS loyalty_program_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT TRUE,
    points_per_currency_unit INT DEFAULT 10,
    minimum_purchase_amount DECIMAL(10,2) DEFAULT 0.00,
    reward_threshold INT DEFAULT 100,
    reward_type VARCHAR(20) DEFAULT 'PERCENTAGE',
    reward_value DECIMAL(10,2) DEFAULT 5.00,
    points_expiration_days INT DEFAULT 0,
    allow_partial_redemption BOOLEAN DEFAULT TRUE,
    minimum_redemption_points INT DEFAULT 50,
    max_redemption_percent_of_total DECIMAL(5,2) DEFAULT 50.00,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
);

-- ============================================
-- ALTER EXISTING TABLES FOR MULTI-STORE SUPPORT
-- ============================================

-- Add store_id to produits (optional for now, will be used in future)
-- ALTER TABLE produits ADD COLUMN IF NOT EXISTS store_id BIGINT;
-- ALTER TABLE produits ADD FOREIGN KEY IF NOT EXISTS (store_id) REFERENCES stores(id);

-- Add store_id to ventes (optional for now, will be used in future)
-- ALTER TABLE ventes ADD COLUMN IF NOT EXISTS store_id BIGINT;
-- ALTER TABLE ventes ADD FOREIGN KEY IF NOT EXISTS (store_id) REFERENCES stores(id);

-- ============================================
-- INSERT DEFAULT STORE
-- ============================================

-- Insert a default store (for existing installations)
INSERT INTO stores (code, name, address, phone, email, currency, language)
SELECT 'MAIN', 'Reb7a - Point de Vente Principal', 
       'Adresse du magasin', '0600000000', 'contact@reb7a.ma', 'MAD', 'fr'
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE code = 'MAIN');

-- Insert default store settings
INSERT INTO store_settings (store_id, invoice_footer_text)
SELECT id, 'Merci pour votre visite!'
FROM stores 
WHERE code = 'MAIN'
AND NOT EXISTS (SELECT 1 FROM store_settings WHERE store_id = (SELECT id FROM stores WHERE code = 'MAIN'));

-- Insert default loyalty program config
INSERT INTO loyalty_program_config (store_id)
SELECT id
FROM stores 
WHERE code = 'MAIN'
AND NOT EXISTS (SELECT 1 FROM loyalty_program_config WHERE store_id = (SELECT id FROM stores WHERE code = 'MAIN'));

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================

CREATE INDEX IF NOT EXISTS idx_promotions_store_active ON promotions(store_id, active);
CREATE INDEX IF NOT EXISTS idx_promotions_dates ON promotions(start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_stores_active ON stores(active);

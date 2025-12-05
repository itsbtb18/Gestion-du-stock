package org.example.exception;

/**
 * StockException - Custom exception for stock-related errors
 */
public class StockException extends Exception {
    
    public StockException(String message) {
        super(message);
    }
    
    public StockException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // Factory methods for common scenarios
    public static StockException stockInsuffisant(String produit, int demande, int disponible) {
        return new StockException(String.format(
            "Stock insuffisant pour '%s': demandé=%d, disponible=%d",
            produit, demande, disponible
        ));
    }
    
    public static StockException produitIntrouvable(String code) {
        return new StockException("Produit introuvable: " + code);
    }
    
    public static StockException produitExpire(String produit) {
        return new StockException("Produit expiré: " + produit);
    }
    
    public static StockException mouvementInvalide(String raison) {
        return new StockException("Mouvement de stock invalide: " + raison);
    }
}

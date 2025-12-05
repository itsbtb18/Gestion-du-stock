package org.example.model.pattern.factory;

import org.example.model.pattern.strategy.*;

import java.util.Map;

/**
 * Factory for creating PaymentStrategy instances
 * Implements Factory Pattern to centralize payment strategy creation
 * 
 * Usage example:
 * - PaymentStrategy strategy = PaymentStrategyFactory.create(PaymentType.CASH, params);
 * - strategy.effectuerPaiement(montant);
 */
public class PaymentStrategyFactory {
    
    /**
     * Enum for supported payment types
     */
    public enum PaymentType {
        CASH,           // Cash payment
        CARD,           // Credit/debit card
        CHECK,          // Check payment  
        MOBILE,         // Mobile payment (TPE, wallet)
        BANK_TRANSFER,  // Bank transfer
        CREDIT          // Store credit
    }
    
    /**
     * Create a payment strategy based on type and parameters
     * 
     * @param type Payment type
     * @param params Payment-specific parameters
     * @return PaymentStrategy implementation
     * @throws IllegalArgumentException if type is unsupported or params are invalid
     */
    public static PaymentStrategy create(PaymentType type, Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("Payment parameters cannot be null");
        }
        
        return switch (type) {
            case CASH -> createCashPayment(params);
            case CARD -> createCardPayment(params);
            case CHECK -> createCheckPayment(params);
            case MOBILE -> createMobilePayment(params);
            case BANK_TRANSFER -> createBankTransferPayment(params);
            case CREDIT -> createCreditPayment(params);
        };
    }
    
    /**
     * Create cash payment
     * Required params: "montantRecu" (Double)
     */
    private static PaymentStrategy createCashPayment(Map<String, Object> params) {
        Double montantRecu = getDoubleParam(params, "montantRecu");
        if (montantRecu == null || montantRecu <= 0) {
            throw new IllegalArgumentException("montantRecu must be positive");
        }
        return new CashPayment(montantRecu);
    }
    
    /**
     * Create card payment
     * Required params: "numeroCarte" (String), "nomTitulaire" (String), "dateExpiration" (String)
     */
    private static PaymentStrategy createCardPayment(Map<String, Object> params) {
        String numeroCarte = getStringParam(params, "numeroCarte");
        String nomTitulaire = getStringParam(params, "nomTitulaire");
        String dateExpiration = getStringParam(params, "dateExpiration");
        
        if (numeroCarte == null || nomTitulaire == null || dateExpiration == null) {
            throw new IllegalArgumentException("Card payment requires: numeroCarte, nomTitulaire, dateExpiration");
        }
        
        return new CardPayment(numeroCarte, nomTitulaire, dateExpiration);
    }
    
    /**
     * Create check payment
     * Required params: "numeroCheck" (String), "nomBanque" (String)
     * TODO: Implement CheckPayment class
     */
    private static PaymentStrategy createCheckPayment(Map<String, Object> params) {
        String numeroCheck = getStringParam(params, "numeroCheck");
        String nomBanque = getStringParam(params, "nomBanque");
        
        if (numeroCheck == null || nomBanque == null) {
            throw new IllegalArgumentException("Check payment requires: numeroCheck, nomBanque");
        }
        
        // TODO: Implement CheckPayment strategy
        throw new UnsupportedOperationException("Check payment not yet implemented. " +
            "Create CheckPayment class implementing PaymentStrategy");
    }
    
    /**
     * Create mobile payment (TPE, wallet, QR code)
     * Required params: "provider" (String), "transactionId" (String)
     * TODO: Implement MobilePayment class
     */
    private static PaymentStrategy createMobilePayment(Map<String, Object> params) {
        String provider = getStringParam(params, "provider");
        String transactionId = getStringParam(params, "transactionId");
        
        if (provider == null || transactionId == null) {
            throw new IllegalArgumentException("Mobile payment requires: provider, transactionId");
        }
        
        // TODO: Implement MobilePayment strategy
        throw new UnsupportedOperationException("Mobile payment not yet implemented. " +
            "Create MobilePayment class implementing PaymentStrategy");
    }
    
    /**
     * Create bank transfer payment
     * Required params: "numeroReference" (String), "nomBanque" (String)
     * TODO: Implement BankTransferPayment class
     */
    private static PaymentStrategy createBankTransferPayment(Map<String, Object> params) {
        String numeroReference = getStringParam(params, "numeroReference");
        String nomBanque = getStringParam(params, "nomBanque");
        
        if (numeroReference == null || nomBanque == null) {
            throw new IllegalArgumentException("Bank transfer requires: numeroReference, nomBanque");
        }
        
        // TODO: Implement BankTransferPayment strategy
        throw new UnsupportedOperationException("Bank transfer payment not yet implemented. " +
            "Create BankTransferPayment class implementing PaymentStrategy");
    }
    
    /**
     * Create store credit payment
     * Required params: "clientId" (Long), "montantCredit" (Double)
     * TODO: Implement CreditPayment class
     */
    private static PaymentStrategy createCreditPayment(Map<String, Object> params) {
        Long clientId = getLongParam(params, "clientId");
        Double montantCredit = getDoubleParam(params, "montantCredit");
        
        if (clientId == null || montantCredit == null || montantCredit <= 0) {
            throw new IllegalArgumentException("Credit payment requires: clientId, montantCredit (positive)");
        }
        
        // TODO: Implement CreditPayment strategy
        throw new UnsupportedOperationException("Credit payment not yet implemented. " +
            "Create CreditPayment class implementing PaymentStrategy");
    }
    
    // Helper methods for parameter extraction
    
    private static String getStringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : null;
    }
    
    private static Double getDoubleParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for " + key + ": " + value);
        }
    }
    
    private static Long getLongParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for " + key + ": " + value);
        }
    }
    
    /**
     * Convenience method: Create cash payment
     */
    public static PaymentStrategy createCash(double montantRecu) {
        return new CashPayment(montantRecu);
    }
    
    /**
     * Convenience method: Create card payment
     */
    public static PaymentStrategy createCard(String numeroCarte, String nomTitulaire, String dateExpiration) {
        return new CardPayment(numeroCarte, nomTitulaire, dateExpiration);
    }
    
    /**
     * Parse payment type from string
     */
    public static PaymentType parsePaymentType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Payment type cannot be null or empty");
        }
        
        return switch (type.toUpperCase()) {
            case "ESPECES", "CASH", "LIQUIDE" -> PaymentType.CASH;
            case "CARTE", "CARD", "CB" -> PaymentType.CARD;
            case "CHEQUE", "CHECK" -> PaymentType.CHECK;
            case "MOBILE", "TPE", "WALLET" -> PaymentType.MOBILE;
            case "VIREMENT", "TRANSFER", "BANK_TRANSFER" -> PaymentType.BANK_TRANSFER;
            case "CREDIT", "AVOIR" -> PaymentType.CREDIT;
            default -> throw new IllegalArgumentException("Unsupported payment type: " + type);
        };
    }
}

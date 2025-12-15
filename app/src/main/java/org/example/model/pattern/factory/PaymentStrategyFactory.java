package org.example.model.pattern.factory;

import org.example.model.pattern.strategy.*;

import java.util.Map;

public class PaymentStrategyFactory {
    
    public enum PaymentType {
        CASH,           
        CARD,           
        CHECK,          
        MOBILE,         
        BANK_TRANSFER,  
        CREDIT          
    }
    
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
    
    private static PaymentStrategy createCashPayment(Map<String, Object> params) {
        Double montantRecu = getDoubleParam(params, "montantRecu");
        if (montantRecu == null || montantRecu <= 0) {
            throw new IllegalArgumentException("montantRecu must be positive");
        }
        return new CashPayment(montantRecu);
    }
    
    private static PaymentStrategy createCardPayment(Map<String, Object> params) {
        String numeroCarte = getStringParam(params, "numeroCarte");
        String nomTitulaire = getStringParam(params, "nomTitulaire");
        String dateExpiration = getStringParam(params, "dateExpiration");
        
        if (numeroCarte == null || nomTitulaire == null || dateExpiration == null) {
            throw new IllegalArgumentException("Card payment requires: numeroCarte, nomTitulaire, dateExpiration");
        }
        
        return new CardPayment(numeroCarte, nomTitulaire, dateExpiration);
    }
    
    private static PaymentStrategy createCheckPayment(Map<String, Object> params) {
        String numeroCheck = getStringParam(params, "numeroCheck");
        String nomBanque = getStringParam(params, "nomBanque");
        
        if (numeroCheck == null || nomBanque == null) {
            throw new IllegalArgumentException("Check payment requires: numeroCheck, nomBanque");
        }
        
        return new CheckPayment(numeroCheck, nomBanque);
    }
    
    private static PaymentStrategy createMobilePayment(Map<String, Object> params) {
        String provider = getStringParam(params, "provider");
        String transactionId = getStringParam(params, "transactionId");
        
        if (provider == null || transactionId == null) {
            throw new IllegalArgumentException("Mobile payment requires: provider, transactionId");
        }
        
        return new MobilePayment(provider, transactionId);
    }
    
    private static PaymentStrategy createBankTransferPayment(Map<String, Object> params) {
        String numeroReference = getStringParam(params, "numeroReference");
        String nomBanque = getStringParam(params, "nomBanque");
        
        if (numeroReference == null || nomBanque == null) {
            throw new IllegalArgumentException("Bank transfer requires: numeroReference, nomBanque");
        }
        
        return new BankTransferPayment(numeroReference, nomBanque);
    }
    
    private static PaymentStrategy createCreditPayment(Map<String, Object> params) {
        Long clientId = getLongParam(params, "clientId");
        Double montantCredit = getDoubleParam(params, "montantCredit");
        
        if (clientId == null || montantCredit == null || montantCredit <= 0) {
            throw new IllegalArgumentException("Credit payment requires: clientId, montantCredit (positive)");
        }
        
        return new CreditPayment(clientId, montantCredit);
    }
    
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
    
    public static PaymentStrategy createCash(double montantRecu) {
        return new CashPayment(montantRecu);
    }
    
    public static PaymentStrategy createCard(String numeroCarte, String nomTitulaire, String dateExpiration) {
        return new CardPayment(numeroCarte, nomTitulaire, dateExpiration);
    }
    
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

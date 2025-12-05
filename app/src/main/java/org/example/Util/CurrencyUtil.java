package org.example.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * CurrencyUtil - Utility class for currency formatting
 */
public class CurrencyUtil {
    
    private static final NumberFormat EURO_FORMAT = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    
    /**
     * Format amount as Euro currency
     */
    public static String formatEuro(double amount) {
        return EURO_FORMAT.format(amount);
    }
    
    /**
     * Format amount as Euro currency (from Double, handles null)
     */
    public static String formatEuro(Double amount) {
        if (amount == null) {
            return formatEuro(0.0);
        }
        return EURO_FORMAT.format(amount);
    }
    
    /**
     * Parse Euro currency string to double
     */
    public static double parseEuro(String euroString) {
        try {
            // Remove currency symbol and non-numeric characters except decimal separator
            String cleaned = euroString.replace("€", "")
                                      .replace(" ", "")
                                      .replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing Euro string: " + euroString);
            return 0.0;
        }
    }
    
    /**
     * Format percentage
     */
    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }
    
    /**
     * Calculate percentage
     */
    public static double calculatePercentage(double part, double total) {
        if (total == 0) return 0;
        return (part / total) * 100;
    }
}

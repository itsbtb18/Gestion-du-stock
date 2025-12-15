package org.example.util;

import org.example.app.AppConfig;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtil {
    
    private static final NumberFormat EURO_FORMAT = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private static final DecimalFormat DECIMAL_FORMAT;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }
    
    public static DecimalFormat getDecimalFormat() {
        return DECIMAL_FORMAT;
    }
    
    private CurrencyUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    public static String format(double amount) {
        return String.format("%.2f %s", amount, AppConfig.CURRENCY_CODE);
    }
    
    public static String format(Double amount) {
        if (amount == null) {
            return format(0.0);
        }
        return format(amount.doubleValue());
    }
    
    @Deprecated
    public static String formatEuro(double amount) {
        return EURO_FORMAT.format(amount);
    }
    
    @Deprecated
    public static String formatEuro(Double amount) {
        if (amount == null) {
            return formatEuro(0.0);
        }
        return EURO_FORMAT.format(amount);
    }
    
    public static double parse(String currencyString) {
        if (currencyString == null || currencyString.trim().isEmpty()) {
            return 0.0;
        }
        try {
            
            String cleaned = currencyString
                .replace(AppConfig.CURRENCY_CODE, "")
                .replace(AppConfig.CURRENCY_SYMBOL, "")
                .replace("€", "")
                .replace(" ", "")
                .replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            LoggerUtil.logWarning(CurrencyUtil.class, "Error parsing currency string: " + currencyString);
            return 0.0;
        }
    }
    
    @Deprecated
    public static double parseEuro(String euroString) {
        return parse(euroString);
    }
    
    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }
    
    public static String formatPercentageChange(double percentage) {
        String prefix = percentage >= 0 ? "+" : "";
        return prefix + String.format("%.1f%%", percentage);
    }
    
    public static double calculatePercentage(double part, double total) {
        if (total == 0) return 0;
        return (part / total) * 100;
    }
    
    public static String getCurrencyCode() {
        return AppConfig.CURRENCY_CODE;
    }
}

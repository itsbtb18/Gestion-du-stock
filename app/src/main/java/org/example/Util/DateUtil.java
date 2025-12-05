package org.example.util;

import org.example.app.AppConfig;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * DateUtil - Utility class for date operations
 */
public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern(AppConfig.DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER = 
        DateTimeFormatter.ofPattern(AppConfig.DATETIME_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern(AppConfig.TIME_FORMAT);
    
    private DateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Format LocalDate to string
     */
    public static String formaterDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Format LocalDateTime to string
     */
    public static String formaterDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Format LocalDateTime to time only
     */
    public static String formaterHeure(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(TIME_FORMATTER);
    }
    
    /**
     * Parse string to LocalDate
     */
    public static LocalDate parserDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Parse string to LocalDateTime
     */
    public static LocalDateTime parserDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Calculate days between two dates
     */
    public static long joursEntre(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(debut, fin);
    }
    
    /**
     * Check if date is in the past
     */
    public static boolean estPasse(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDate.now());
    }
    
    /**
     * Check if date is in the future
     */
    public static boolean estFutur(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }
    
    /**
     * Check if date is today
     */
    public static boolean estAujourdhui(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now());
    }
    
    /**
     * Get start of month
     */
    public static LocalDate debutMois(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(1);
    }
    
    /**
     * Get end of month
     */
    public static LocalDate finMois(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(date.lengthOfMonth());
    }
    
    /**
     * Get relative date description
     */
    public static String descriptionRelative(LocalDate date) {
        if (date == null) {
            return "";
        }
        
        long jours = joursEntre(LocalDate.now(), date);
        
        if (jours == 0) {
            return "Aujourd'hui";
        } else if (jours == 1) {
            return "Demain";
        } else if (jours == -1) {
            return "Hier";
        } else if (jours > 0) {
            return "Dans " + jours + " jours";
        } else {
            return "Il y a " + Math.abs(jours) + " jours";
        }
    }
}

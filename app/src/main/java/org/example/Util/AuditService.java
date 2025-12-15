package org.example.util;

import org.example.dao.AuditLogDAO;
import org.example.model.entity.AuditLog;
import org.example.model.entity.TypeAction;
import org.example.model.entity.Utilisateur;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class AuditService {
    
    private static AuditService instance;
    private final AuditLogDAO auditLogDAO;
    private final ConcurrentLinkedQueue<AuditLog> pendingLogs = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler;
    
    private AuditService() {
        this.auditLogDAO = new AuditLogDAO();
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("AuditService-Flusher");
            return t;
        });
        
        scheduler.scheduleAtFixedRate(this::flushPendingLogs, 5, 5, TimeUnit.SECONDS);
    }
    
    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }
    
    public void log(TypeAction action, String details) {
        log(action, null, null, details);
    }
    
    public void log(TypeAction action, String entityType, Long entityId, String details) {
        Utilisateur currentUser = SessionManager.getInstance().getCurrentUser();
        
        AuditLog auditLog = new AuditLog();
        auditLog.setUtilisateur(currentUser);
        auditLog.setAction(action);
        auditLog.setEntite(entityType);
        auditLog.setEntiteId(entityId != null ? entityId : 0L);
        auditLog.setDescription(details);
        auditLog.setDateHeure(LocalDateTime.now());
        auditLog.setAdresseIP(getClientIpAddress());
        auditLog.setSucces(true);
        
        pendingLogs.offer(auditLog);
    }
    
    public void logLogin(String username, boolean success, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(success ? TypeAction.CONNEXION : TypeAction.CONNEXION);
        auditLog.setDescription(details + (success ? "" : " - FAILED"));
        auditLog.setDateHeure(LocalDateTime.now());
        auditLog.setAdresseIP(getClientIpAddress());
        auditLog.setSucces(success);
        
        pendingLogs.offer(auditLog);
        
        flushPendingLogs();
    }
    
    public void logLogout() {
        log(TypeAction.DECONNEXION, "User logged out");
    }
    
    public void logCreate(String entityType, Long entityId, String details) {
        log(TypeAction.CREATION, entityType, entityId, details);
    }
    
    public void logUpdate(String entityType, Long entityId, String details) {
        log(TypeAction.MODIFICATION, entityType, entityId, details);
    }
    
    public void logDelete(String entityType, Long entityId, String details) {
        log(TypeAction.SUPPRESSION, entityType, entityId, details);
    }
    
    public void logExport(String entityType, String details) {
        log(TypeAction.EXPORT, entityType, null, details);
    }
    
    public void logSale(Long venteId, double amount, String paymentMethod) {
        log(TypeAction.VENTE, "Vente", venteId, 
            String.format("Montant: %.2f, Mode: %s", amount, paymentMethod));
    }
    
    public void logReturn(Long retourId, double amount, String reason) {
        log(TypeAction.RETOUR, "Retour", retourId, 
            String.format("Montant: %.2f, Raison: %s", amount, reason));
    }
    
    public void logStockMovement(Long produitId, String type, int quantity, String motif) {
        log(TypeAction.MOUVEMENT_STOCK, "Produit", produitId, 
            String.format("Type: %s, Quantité: %d, Motif: %s", type, quantity, motif));
    }
    
    public void logSettingsChange(String settingName, String oldValue, String newValue) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUtilisateur(SessionManager.getInstance().getCurrentUser());
        auditLog.setAction(TypeAction.MODIFICATION);
        auditLog.setEntite("Settings");
        auditLog.setEntiteId(0L);
        auditLog.setDescription("Paramètre modifié: " + settingName);
        auditLog.setValeurAvant(oldValue);
        auditLog.setValeurApres(newValue);
        auditLog.setDateHeure(LocalDateTime.now());
        auditLog.setAdresseIP(getClientIpAddress());
        auditLog.setSucces(true);
        
        pendingLogs.offer(auditLog);
    }
    
    private void flushPendingLogs() {
        AuditLog log;
        while ((log = pendingLogs.poll()) != null) {
            try {
                auditLogDAO.save(log);
            } catch (Exception e) {
                LoggerUtil.logError(AuditService.class, "Failed to save audit log", e);
            }
        }
    }
    
    public List<AuditLog> getLogsForUser(Long userId, int limit) {
        try {
            List<AuditLog> logs = auditLogDAO.findByUtilisateur(userId);
            return logs.stream().limit(limit).toList();
        } catch (Exception e) {
            LoggerUtil.logError(AuditService.class, "Failed to get logs for user", e);
            return List.of();
        }
    }
    
    public List<AuditLog> getRecentLogs(int limit) {
        try {
            return auditLogDAO.findAll().stream()
                .sorted((a, b) -> b.getDateHeure().compareTo(a.getDateHeure()))
                .limit(limit)
                .toList();
        } catch (Exception e) {
            LoggerUtil.logError(AuditService.class, "Failed to get recent logs", e);
            return List.of();
        }
    }
    
    public List<AuditLog> getLogsByAction(TypeAction action, int limit) {
        try {
            List<AuditLog> logs = auditLogDAO.findByAction(action);
            return logs.stream().limit(limit).toList();
        } catch (Exception e) {
            LoggerUtil.logError(AuditService.class, "Failed to get logs by action", e);
            return List.of();
        }
    }
    
    private String getClientIpAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
    
    public void shutdown() {
        flushPendingLogs();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

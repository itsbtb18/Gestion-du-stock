package org.example.util;

import org.example.model.entity.Utilisateur;

public class SessionManager {
    
    private static SessionManager instance;
    private Utilisateur currentUser;
    private String sessionId;
    private long sessionStartTime;
    
    private SessionManager() {
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void startSession(Utilisateur user) {
        this.currentUser = user;
        this.sessionId = generateSessionId();
        this.sessionStartTime = System.currentTimeMillis();
        System.out.println("Session started for user: " + user.getUsername());
    }
    
    public void endSession() {
        if (currentUser != null) {
            System.out.println("Session ended for user: " + currentUser.getUsername());
        }
        this.currentUser = null;
        this.sessionId = null;
        this.sessionStartTime = 0;
    }
    
    public Utilisateur getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public long getSessionDurationMinutes() {
        if (sessionStartTime == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - sessionStartTime) / (1000 * 60);
    }
    
    public boolean hasRole(org.example.model.entity.Role... roles) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasRole(roles);
    }
    
    public boolean isAdmin() {
        return hasRole(org.example.model.entity.Role.ADMIN);
    }
    
    public boolean isManagerOrAdmin() {
        return hasRole(org.example.model.entity.Role.ADMIN, org.example.model.entity.Role.GERANT);
    }
    
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    private static final long SESSION_TIMEOUT_MINUTES = 8 * 60;
    private static final long INACTIVITY_TIMEOUT_MINUTES = 30;
    private long lastActivityTime;
    
    private java.util.List<Runnable> sessionTimeoutListeners = new java.util.ArrayList<>();
    
    public boolean isSessionValid() {
        if (!isLoggedIn()) {
            return false;
        }
        
        if (getSessionDurationMinutes() >= SESSION_TIMEOUT_MINUTES) {
            LoggerUtil.logInfo(SessionManager.class, "Session expired due to maximum duration");
            return false;
        }
        
        if (lastActivityTime > 0) {
            long inactiveMinutes = (System.currentTimeMillis() - lastActivityTime) / (1000 * 60);
            if (inactiveMinutes >= INACTIVITY_TIMEOUT_MINUTES) {
                LoggerUtil.logInfo(SessionManager.class, "Session expired due to inactivity");
                return false;
            }
        }
        
        return true;
    }
    
    public void recordActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }
    
    public void refreshSession() {
        this.sessionStartTime = System.currentTimeMillis();
        this.lastActivityTime = System.currentTimeMillis();
    }
    
    public void addSessionTimeoutListener(Runnable listener) {
        sessionTimeoutListeners.add(listener);
    }
    
    public void removeSessionTimeoutListener(Runnable listener) {
        sessionTimeoutListeners.remove(listener);
    }
    
    public void notifySessionTimeout() {
        for (Runnable listener : sessionTimeoutListeners) {
            try {
                listener.run();
            } catch (Exception e) {
                LoggerUtil.logError(SessionManager.class, "Error notifying session timeout listener", e);
            }
        }
    }
    
    public long getTimeRemainingMinutes() {
        if (!isLoggedIn()) return 0;
        long remaining = SESSION_TIMEOUT_MINUTES - getSessionDurationMinutes();
        return Math.max(0, remaining);
    }
    
    public long getInactivityRemainingMinutes() {
        if (lastActivityTime == 0) return INACTIVITY_TIMEOUT_MINUTES;
        long inactive = (System.currentTimeMillis() - lastActivityTime) / (1000 * 60);
        return Math.max(0, INACTIVITY_TIMEOUT_MINUTES - inactive);
    }
    
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
    
    public String getCurrentUserDisplayName() {
        if (currentUser == null) return "Inconnu";
        return currentUser.getPrenom() + " " + currentUser.getNom();
    }
    
    public String getCurrentUserRoleName() {
        if (currentUser == null || currentUser.getRole() == null) return "N/A";
        return currentUser.getRole().getLibelle();
    }
}

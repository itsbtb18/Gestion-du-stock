package org.example.util;

import org.example.model.entity.Utilisateur;

/**
 * SessionManager - Singleton class for managing user session
 */
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
    
    /**
     * Start a new session for a user
     */
    public void startSession(Utilisateur user) {
        this.currentUser = user;
        this.sessionId = generateSessionId();
        this.sessionStartTime = System.currentTimeMillis();
        System.out.println("Session started for user: " + user.getUsername());
    }
    
    /**
     * End current session
     */
    public void endSession() {
        if (currentUser != null) {
            System.out.println("Session ended for user: " + currentUser.getUsername());
        }
        this.currentUser = null;
        this.sessionId = null;
        this.sessionStartTime = 0;
    }
    
    /**
     * Get current logged-in user
     */
    public Utilisateur getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Get session duration in minutes
     */
    public long getSessionDurationMinutes() {
        if (sessionStartTime == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - sessionStartTime) / (1000 * 60);
    }
    
    /**
     * Check if user has specific role
     */
    public boolean hasRole(org.example.model.entity.Role... roles) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasRole(roles);
    }
    
    /**
     * Generate unique session ID
     */
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    /**
     * Check if session is still valid (timeout after 8 hours)
     */
    public boolean isSessionValid() {
        if (!isLoggedIn()) {
            return false;
        }
        long maxDurationMinutes = 8 * 60; // 8 hours
        return getSessionDurationMinutes() < maxDurationMinutes;
    }
    
    /**
     * Refresh session (update timestamp)
     */
    public void refreshSession() {
        this.sessionStartTime = System.currentTimeMillis();
    }
}

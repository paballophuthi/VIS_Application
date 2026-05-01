package com.vis.utils;

import com.vis.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    public void clearSession() {
        this.currentUser = null;
    }
}
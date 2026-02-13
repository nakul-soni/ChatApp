package com.example.chatapp.model;

public class User {
    private String userId;
    private String username;
    private String email;
    private String fcmToken;

    // Empty constructor required for Firebase
    public User() {
    }

    // Constructor
    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fcmToken = "";
    }

    // Constructor with FCM token
    public User(String userId, String username, String email, String fcmToken) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fcmToken = fcmToken;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}

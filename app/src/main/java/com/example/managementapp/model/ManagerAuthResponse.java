package com.example.managementapp.model;

public class ManagerAuthResponse {
    private String message;
    private String managerId;
    private String managerName;
    private String accessToken;
    private long expiresAt;

    public String getMessage() {
        return message;
    }

    public String getManagerId() {
        return managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}
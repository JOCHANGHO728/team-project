package com.example.managementapp.model;

public class LoginRequest {
    private String managerId;
    private String mPassword;

    public LoginRequest(String managerId, String mPassword) {
        this.managerId = managerId;
        this.mPassword = mPassword;
    }

    public String getManagerId() {
        return managerId;
    }

    public String getMPassword() {
        return mPassword;
    }
}
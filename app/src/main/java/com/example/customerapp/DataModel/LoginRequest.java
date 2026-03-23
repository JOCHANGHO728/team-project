package com.example.customerapp.DataModel;

public class LoginRequest {
    private String uId;
    private String uPassword;

    public LoginRequest(String uId, String uPassword) {
        this.uId = uId;
        this.uPassword = uPassword;
    }
}
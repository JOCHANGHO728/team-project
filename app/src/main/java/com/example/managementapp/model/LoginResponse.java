package com.example.managementapp.model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}

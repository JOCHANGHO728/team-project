package com.example.managementapp.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("managerId")
    private String id;

    @SerializedName("mPassword")
    private String password;

    public LoginRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }
}

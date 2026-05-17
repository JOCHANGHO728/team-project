package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("login_id")
    private String uId;

    @SerializedName("password")
    private String uPassword;

    public LoginRequest(String uId, String uPassword) {
        this.uId = uId;
        this.uPassword = uPassword;
    }
}

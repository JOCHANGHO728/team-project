package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

public class UserAuthResponse {
    @SerializedName("login_id")
    private String loginId;

    @SerializedName("name")
    private String name;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("expiresAt")
    private long expiresAt;

    public String getLoginId() { return loginId; }
    public String getName() { return name; }
    public String getAccessToken() { return accessToken; }
    public long getExpiresAt() { return expiresAt; }
}

package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

// 회원가입 데이터 클래스
public class SignUpRequest {
    @SerializedName("login_id")
    private String uId;
    @SerializedName("password")
    private String uPassword;
    @SerializedName("name")
    private String uName;
    @SerializedName("p_number")
    private String uNum;

    public SignUpRequest(String uId, String uPassword, String uName, String uNum) {
        this.uId = uId;
        this.uPassword = uPassword;
        this.uName = uName;
        this.uNum = uNum;
    }
}

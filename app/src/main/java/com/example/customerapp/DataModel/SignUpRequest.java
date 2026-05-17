package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

// 회원가입 데이터 클래스
public class SignUpRequest {
    private String uId;
    @SerializedName("rawPassword")
    private String rawPassword;
    @SerializedName("uPassword")
    private String uPassword;
    private String uName;
    private String uNum;

    public SignUpRequest(String uId, String uPassword, String uName, String uNum) {
        this.uId = uId;
        this.rawPassword = uPassword;
        this.uPassword = uPassword;
        this.uName = uName;
        this.uNum = uNum;
    }
}

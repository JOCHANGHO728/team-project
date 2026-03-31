package com.example.customerapp.DataModel;

// 회원가입 데이터 클래스
public class SignUpRequest {
    private String uId;
    private String uPassword;
    private String uName;
    private String uNum;

    public SignUpRequest(String uId, String uPassword, String uName, String uNum) {
        this.uId = uId;
        this.uPassword = uPassword;
        this.uName = uName;
        this.uNum = uNum;
    }
}
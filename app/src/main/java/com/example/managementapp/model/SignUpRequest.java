package com.example.managementapp.model;

// 회원가입 데이터 클래스
public class SignUpRequest {
    private String id;
    private String password;
    private String name;
    private String phoneNumber;

    public SignUpRequest(String id, String password, String name, String phoneNumber) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
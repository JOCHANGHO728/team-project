package com.example.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginDto {
    private String userId;    // 로그인 아이디를 담을 칸
    private String uPassword; // 비밀번호를 담을 칸
}
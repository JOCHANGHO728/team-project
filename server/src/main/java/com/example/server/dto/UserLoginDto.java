package com.example.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginDto {
    private String login_id;
    private String password;
}

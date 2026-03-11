package com.example.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserSignupDto {

    private String login_id;


    private String password;


    private String name;


    private String p_number;
}
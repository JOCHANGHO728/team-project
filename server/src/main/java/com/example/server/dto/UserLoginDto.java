package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginDto {
    @JsonProperty("login_id")
    private String uId;

    @JsonProperty("password")
    private String uPassword;
}
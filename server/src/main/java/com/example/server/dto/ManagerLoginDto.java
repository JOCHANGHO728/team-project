package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ManagerLoginDto {

    @JsonProperty("managerId")
    private String managerId;

    @JsonProperty("mPassword")
    private String mPassword;
}
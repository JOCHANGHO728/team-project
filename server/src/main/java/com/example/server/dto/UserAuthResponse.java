package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAuthResponse {
    @JsonProperty("login_id")
    private String uId;

    @JsonProperty("name")
    private String uName;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("expiresAt")
    private long expiresAt;
}

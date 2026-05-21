package com.example.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ManagerAuthResponse {
    private String managerId;
    private String managerName;
    private String accessToken;
    private long expiresAt;
}

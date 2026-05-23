package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginDto {
    @JsonProperty("login_id")
    @NotBlank(message = "아이디는 필수입니다.")
    private String uId;

    @JsonProperty("password")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String uPassword;
}

package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserSignupDto {
    @JsonProperty("login_id")
    @NotBlank(message = "아이디는 필수입니다.")
    private String uId;

    @JsonProperty("password")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String uPassword;

    @JsonProperty("name")
    @NotBlank(message = "이름은 필수입니다.")
    private String uName;

    @JsonProperty("p_number")
    @NotBlank(message = "전화번호는 필수입니다.")
    private String uNum;
}

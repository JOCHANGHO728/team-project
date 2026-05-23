package com.example.server.controller;

import com.example.server.common.ApiResponse;
import com.example.server.dto.UserAuthResponse;
import com.example.server.dto.UserLoginDto;
import com.example.server.dto.UserSignupDto;
import com.example.server.entity.User;
import com.example.server.security.UserTokenService;
import com.example.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserTokenService userTokenService;

    @PostMapping("/login")
    public ApiResponse<UserAuthResponse> login(@Valid @RequestBody UserLoginDto request) {
        User user = userService.login(request);
        UserTokenService.IssuedToken issuedToken = userTokenService.issueToken(user.getUId());

        UserAuthResponse response = new UserAuthResponse(
                user.getUId(),
                user.getUName(),
                issuedToken.accessToken(),
                issuedToken.expiresAt()
        );
        return ApiResponse.success("로그인 성공", response);
    }

    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody UserSignupDto request) {
        String message = userService.signup(request);
        return ApiResponse.success("회원가입 성공", message);
    }
}

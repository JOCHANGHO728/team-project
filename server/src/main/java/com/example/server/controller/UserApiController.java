package com.example.server.controller;

import com.example.server.common.ApiResponse;
import com.example.server.dto.UserLoginDto;
import com.example.server.dto.UserSignupDto;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody UserLoginDto request) {
        try {
            String message = userService.login(request);
            return ApiResponse.success("로그인 성공", message);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody UserSignupDto request) {
        try {
            String message = userService.signup(request);
            return ApiResponse.success("회원가입 성공", message);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
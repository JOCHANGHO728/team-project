package com.example.server.controller;

import com.example.server.dto.UserLoginDto;
import com.example.server.dto.UserSignupDto; // 추가됨
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // 로그인 API (기존)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto request) {
        try {
            String message = userService.login(request);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원가입 API 추가
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupDto request) {
        try {
            // 서비스의 signup 로직 호출
            String message = userService.signup(request);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            // 아이디 중복 등 예외 발생 시 에러 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
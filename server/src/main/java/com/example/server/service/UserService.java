package com.example.server.service;

import com.example.server.dto.UserLoginDto;
import com.example.server.dto.UserSignupDto;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String login(UserLoginDto request) {
        User user = userRepository.findByUId(request.getUId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 아이디입니다."));

        if (!user.getUPassword().equals(request.getUPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user.getUName() + "님, 환영합니다!";
    }

    @Transactional
    public String signup(UserSignupDto request) {
        userRepository.findByUId(request.getUId())
                .ifPresent(u -> { throw new IllegalArgumentException("이미 존재하는 아이디입니다."); });

        User user = new User();
        user.setUId(request.getUId());
        user.setUPassword(request.getUPassword());
        user.setUName(request.getUName());
        user.setUNum(request.getUNum());
        userRepository.save(user);

        return user.getUName() + "님, 회원가입이 완료되었습니다!";
    }
}
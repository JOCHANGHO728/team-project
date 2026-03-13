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
    //로그인 기능
    public String login(UserLoginDto request) {
        User user = userRepository.findByUId(request.getLogin_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 아이디입니다."));

        if (!user.getUPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user.getUName() + "님, 환영합니다!";
    }

    //회원가입 기능
    @Transactional
    public String signup(UserSignupDto request) {

        //아이디 중복 검사
        userRepository.findByUId(request.getLogin_id())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
                });


        User user = new User();


        user.setUId(request.getLogin_id());
        user.setUPassword(request.getPassword());
        user.setUName(request.getName());
        user.setUNum(request.getP_number());


        userRepository.save(user);

        return user.getUName() + "님, 회원가입이 완료되었습니다!";
    }
}
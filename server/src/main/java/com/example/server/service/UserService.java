package com.example.server.service;

import com.example.server.dto.UserLoginDto;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String login(UserLoginDto request) {
        
        User user = userRepository.findByUId(request.getLogin_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 아이디입니다."));

       
        if (!user.getUPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        
        return user.getUName() + "님, 환영합니다!";
    }
}

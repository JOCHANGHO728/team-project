package com.example.server.service;

import com.example.server.dto.UserLoginDto;
import com.example.server.dto.UserSignupDto; // 추가됨
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
        User user = userRepository.findByuId(request.getLogin_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 아이디입니다."));

        if (!user.getUPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user.getUName() + "님, 환영합니다!";
    }


    @Transactional // 데이터 저장 시 에러가 나면 롤백해주는 안전장치
    public String signup(UserSignupDto request) {

        userRepository.findByuId(request.getLogin_id())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
                });


        User user = new User();

        // 3. DTO의 값을 엔티티의 변수에 하나씩 세팅 (이 부분이 중요!)
        user.setUId(request.getLogin_id());      // DTO(login_id) -> Entity(uId)
        user.setUPassword(request.getPassword()); // DTO(password) -> Entity(uPassword)
        user.setUName(request.getName());        // DTO(name) -> Entity(uName)
        user.setUNum(request.getP_number());     // DTO(p_number) -> Entity(uNum)


        userRepository.save(user);

        return user.getUName() + "님, 회원가입이 완료되었습니다!";
    }
}
package com.example.server.service;

import com.example.server.dto.ManagerLoginDto;
import com.example.server.entity.Manager;
import com.example.server.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Manager login(ManagerLoginDto request) {
        Manager manager = managerRepository.findByManagerId(request.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자 아이디입니다."));

        // 수정: .equals() 대신 BCrypt matches()로 비교
        if (!passwordEncoder.matches(request.getMPassword(), manager.getMPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return manager;
    }
}

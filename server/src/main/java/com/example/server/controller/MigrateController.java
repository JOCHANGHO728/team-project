package com.example.server.controller;

import com.example.server.entity.Manager;
import com.example.server.entity.User;
import com.example.server.repository.ManagerRepository;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MigrateController {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    // ⚠️ 한 번만 호출하고 반드시 삭제할 것
    @GetMapping("/migrate-passwords")
    public String migratePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 유저 비밀번호 변환
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setUPassword(encoder.encode(user.getUPassword()));
            userRepository.save(user);
        }

        // 관리자 비밀번호 변환
        List<Manager> managers = managerRepository.findAll();
        for (Manager manager : managers) {
            manager.setMPassword(encoder.encode(manager.getMPassword()));
            managerRepository.save(manager);
        }

        return "완료! 유저 " + users.size() + "명, 관리자 " + managers.size() + "명 변환됨. 이 API를 즉시 삭제하세요.";
    }
}
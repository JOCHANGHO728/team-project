package com.example.server.repository;

import com.example.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // JPA의 이름 인식 오류를 방지하기 위해 직접 쿼리를 지정합니다.
    @Query("SELECT u FROM User u WHERE u.uId = :uId")
    Optional<User> findByUId(@Param("uId") String uId);

}
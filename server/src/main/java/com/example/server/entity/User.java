package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usersdb")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false)
    private String uId; //여기 값 이용해주세요

    @Column(name = "password", nullable = false)
    private String uPassword;  //여기 값 이용해주세요

    @Column(name = "name", nullable = false)
    private String uName;  //여기 값 이용해주세요

    @Column(name = "p_number")
    private String uNum;  //여기 값 이용해주세요

}




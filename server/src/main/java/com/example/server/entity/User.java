package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "userdb")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false)
    private String uId;

    @Column(name = "password", nullable = false)
    private String uPw;

    @Column(name = "name")
    private String uName;

    @Column(name = "p_number")
    private String uPnum;

}

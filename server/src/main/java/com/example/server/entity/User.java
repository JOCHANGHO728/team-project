package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "u_password", nullable = false)
    private String uPassword;

    @Column(name = "u_name")
    private String uName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_address")
    private String userAddress;
}
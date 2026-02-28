package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "managerdb")
@Getter @Setter
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manager_id")
    private String managerId;

    @Column(name = "m_password")
    private String mPassword;

    @Column(name = "m_name")
    private String mName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "manager_number")
    private String managerNumber;
}
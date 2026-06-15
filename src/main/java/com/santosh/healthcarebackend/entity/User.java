package com.santosh.healthcarebackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import com.santosh.healthcarebackend.entity.Role;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.PATIENT;

    private LocalDateTime createdAt = LocalDateTime.now();


}
package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;

    private String avatar; //file or URL image


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // xoa user -> xoa ca student
    private Student student;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Teacher teacher;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    boolean isActive = true;

}


package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "class")
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;

    private String className;
    private int academicYear;

    @OneToOne
    @JoinColumn(name = "homeroom_teacher_id")
    private Teacher teacher;


}

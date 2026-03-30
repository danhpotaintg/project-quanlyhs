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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "class_id")
    private String id;

    private String className;
    private String academicYear;

    @ManyToOne
    @JoinColumn(name = "homeroom_teacher_id")
    private Teacher teacher;


}

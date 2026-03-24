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
    private String academicYear;

    @OneToOne
    @JoinColumn(name = "teacher_id", unique = true)
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "class_subject",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> subjects;

}

package com.example.Qlyhocsinh.entity;

import com.example.Qlyhocsinh.enums.GradeStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "grade")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "grade_id")
    String id;


    int entryIndex;
    LocalDate gradedAt;
    String note;

    @Column(precision = 4, scale = 2)
    double score;

    @Enumerated(EnumType.STRING)
    GradeStatus status;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne
    @JoinColumn(name = "grade_config_id")
    GradeConfig gradeConfig;
}

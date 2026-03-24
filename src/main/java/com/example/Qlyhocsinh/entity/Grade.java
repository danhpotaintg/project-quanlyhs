package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    int semester;
    String scoreType;
    float score;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    Subject subject;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

}

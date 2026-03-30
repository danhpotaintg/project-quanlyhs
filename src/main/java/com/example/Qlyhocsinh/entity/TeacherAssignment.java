package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "teacher_subject",
        uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_id", "subject_id"})
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @MapsId("teacherId")
    @JoinColumn(name = "teacher_id")
    Teacher teacher;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    Subject subject;

    boolean isPrimary;
    int assignedYear;


}
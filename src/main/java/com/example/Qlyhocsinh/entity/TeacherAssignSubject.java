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
public class TeacherAssignSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    Subject subject;
    


}
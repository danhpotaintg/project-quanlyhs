package com.example.Qlyhocsinh.entity;

import com.example.Qlyhocsinh.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "attendance")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance {

    @Id
    @Column(name = "attendance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime checkInTime;


    @Enumerated(EnumType.STRING)
    AttendanceStatus status;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    Teacher teacher;
}

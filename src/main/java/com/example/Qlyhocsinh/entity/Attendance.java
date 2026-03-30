package com.example.Qlyhocsinh.entity;

import com.example.Qlyhocsinh.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

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

    LocalTime checkInTime;
    String note;

    @Enumerated(EnumType.STRING)
    AttendanceStatus status;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    Teacher teacher;
}

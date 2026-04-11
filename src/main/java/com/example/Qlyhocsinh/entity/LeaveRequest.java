package com.example.Qlyhocsinh.entity;

import com.example.Qlyhocsinh.enums.LeaveRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name = "leave_request")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String reason;
    LocalDate fromDate;
    LocalDate toDate;

    @Enumerated(EnumType.STRING)
    LeaveRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @ManyToOne
    @JoinColumn(name = "approved_by_teacher_id")
    Teacher approvedByTeacher;
}

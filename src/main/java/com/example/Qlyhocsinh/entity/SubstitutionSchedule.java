package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "substitution_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubstitutionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "original_schedule_id", nullable = false)
    Schedule originalSchedule;

    @ManyToOne
    @JoinColumn(name = "absent_teacher_id", nullable = false)
    Teacher absentTeacher;

    @ManyToOne
    @JoinColumn(name = "substitute_teacher_id", nullable = false)
    Teacher substituteTeacher;

    LocalDate substitutionDate;

    // Trạng thái trả tiết (Dùng cho sắp xếp buổi dạy bù )
    @Column(columnDefinition = "boolean default false")
    boolean isReturned;

    LocalDateTime createdAt = LocalDateTime.now();
}

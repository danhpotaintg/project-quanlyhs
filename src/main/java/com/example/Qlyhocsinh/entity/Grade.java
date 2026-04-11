package com.example.Qlyhocsinh.entity;

import com.example.Qlyhocsinh.enums.GradeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "grades",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_grade_student_config_entry",
                columnNames = {"student_id", "grade_config_id", "entry_index"}
        )
)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "grade_config_id", nullable = false)
    private Long gradeConfigId;

    @Column(name = "entry_index", nullable = false)
    private Integer entryIndex;

    @Column(nullable = false, columnDefinition = "DECIMAL(4,2)")
    private Double score;

    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

}

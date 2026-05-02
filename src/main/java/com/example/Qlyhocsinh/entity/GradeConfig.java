package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "grade_config")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_config_id")
    Long id;

    int semester;
    int academicYear;

    String scoreType;
    Double weight; //he so
    Integer maxEntries;//số đầu điểm,  ví dụ kiểm tra 15 phút co 3 lần ktra thì 3 đầu điểm

    @Column(name = "subject_id", nullable = false)
    String subjectId;

}

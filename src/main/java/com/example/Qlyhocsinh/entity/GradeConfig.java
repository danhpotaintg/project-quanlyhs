package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gradeconfig")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "grade_config_id")
    String id;

    int semester;
    String score_type;
    float weight; //he so
    int max_entries;//số đầu điểm,  ví dụ kiểm tra 15 phút co 3 lần ktra thì 3 đầu điểm

    @ManyToOne
    @JoinColumn(name = "subject_id")
    Subject subject;
}

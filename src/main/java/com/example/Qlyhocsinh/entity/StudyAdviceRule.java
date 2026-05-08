package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_advice_rule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyAdviceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    private String subjectId;

    private Double minScore;

    private Double maxScore;

    @Column(columnDefinition = "TEXT")
    private String advice;

    private Integer priority;

    private Boolean isActive;
}

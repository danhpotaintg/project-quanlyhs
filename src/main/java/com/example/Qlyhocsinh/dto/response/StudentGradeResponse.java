package com.example.Qlyhocsinh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentGradeResponse {
    private String subjectId;
    private String subjectName;
    private Integer semester;
    private Double semesterAverage; // diem tb ki
    private List<GradeConfigDetail> gradeConfigs;

    @Data
    @Builder
    public static class GradeConfigDetail{
        private Long gradeConfigId;
        private String scoreType;
        private Double weight;
        private Integer maxEntries;
        private List<Double> scores;
    }
}

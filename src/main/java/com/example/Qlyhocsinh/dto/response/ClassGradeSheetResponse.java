package com.example.Qlyhocsinh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ClassGradeSheetResponse {

    private List<GradeConfigDto> gradeConfigs;
    private List<StudentGradeRow> students;

    @Data
    @Builder
    public static class GradeConfigDto {
        private Long id;
        private String subjectId;
        private Integer semester;
        private String scoreType;
        private Double weight;
        private Integer maxEntries;
    }

    @Data
    @Builder
    public static class StudentGradeRow {
        private String studentId;
        private String studentName;
        private Map<String, Double> scores;
    }

}

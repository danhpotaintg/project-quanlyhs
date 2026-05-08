package com.example.Qlyhocsinh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentAllGradeResponse {
    private String studentId;
    private String studentName;
    private Integer semester;
    private int academicYear;
    private Double semesterGPA;      // Điểm TB học kì (TB của tất cả TBM)
    private String academicRank;     // Học lực: Giỏi, Khá, TB, Yếu, Kém
    private String title;            // Danh hiệu: Học sinh giỏi, Tiên tiến, ...
    private List<SubjectGradeSummary> subjects;

    @Data
    @Builder
    public static class SubjectGradeSummary {
        private String subjectId;
        private String subjectName;
        private List<GradeConfigDetail> gradeConfigs;
        private Double semesterAverage; // TBM từng môn
    }

    @Data
    @Builder
    public static class GradeConfigDetail {
        private Long gradeConfigId;
        private String scoreType;
        private double weight;
        private int maxEntries;
        private List<Double> scores;
    }
}


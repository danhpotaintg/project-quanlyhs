package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class StudentRankingResponse {

    String studentName;
    String className;
    Double semesterAverage; // diem tb ki
    List<GradeConfigDetail> gradeConfigs;

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

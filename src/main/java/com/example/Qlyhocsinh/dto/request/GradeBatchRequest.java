package com.example.Qlyhocsinh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeBatchRequest {

    private List<GradeEntry> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GradeEntry{

        private Long gradeConfigId;

        private Integer entryIndex;

        private Double score;

    }
}

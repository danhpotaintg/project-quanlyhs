package com.example.Qlyhocsinh.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectAdviceResponse {

    private String subjectId;
    private String subjectName;
    private Double semesterAverage;
    private String advice;

}

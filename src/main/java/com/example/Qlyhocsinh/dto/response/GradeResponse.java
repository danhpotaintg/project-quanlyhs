package com.example.Qlyhocsinh.dto.response;

import com.example.Qlyhocsinh.enums.GradeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GradeResponse {

    private String studentId;

    private Long gradeConfigId;

    private Integer entryIndex;

    private Double score;

    private String teacherId;

    private GradeStatus status;

    private String note;
}
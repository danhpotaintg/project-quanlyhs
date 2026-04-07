package com.example.Qlyhocsinh.dto.response;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeConfigBulkResponse {

    private String subjectId;
    private String subjectName;

    private List<GradeConfigResponse> configs;

}

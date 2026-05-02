package com.example.Qlyhocsinh.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeImportResponse {

    private Integer rowIndex;
    private String studentName;
    private boolean success;
    private String message;

}

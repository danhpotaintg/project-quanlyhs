package com.example.Qlyhocsinh.dto.request;

import com.example.Qlyhocsinh.enums.GradeStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRequest {

//    @NotBlank(message = "student_id không được để trống")
//    private String studentId;
//
//    @NotNull(message = "grade_config_id không được để trống")
//    @Positive(message = "grade_config_id phải là số dương")
//    private Long gradeConfigId;

//    @NotNull(message = "entry_index không được để trống")
//    @Min(value = 1, message = "entry_index phải >= 1")
//    private Integer entryIndex;

    @NotNull(message = "score không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Điểm phải >= 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Điểm phải <= 10")
    private Double score;

    // Được set từ JWT token trong Service, không nhận từ client
    //private String teacherId;

//    private GradeStatus status = GradeStatus.final_grade;
//
//    @Size(max = 500, message = "Ghi chú không quá 500 ký tự")
//    private String note;
//
}

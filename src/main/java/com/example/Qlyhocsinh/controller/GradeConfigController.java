package com.example.Qlyhocsinh.controller;


import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.GradeConfigBulkRequest;
import com.example.Qlyhocsinh.dto.request.GradeConfigRequest;
import com.example.Qlyhocsinh.dto.response.GradeConfigBulkResponse;
import com.example.Qlyhocsinh.dto.response.GradeConfigResponse;
import com.example.Qlyhocsinh.service.GradeConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gradeConfigs")
@RequiredArgsConstructor
public class GradeConfigController {

    private final GradeConfigService gradeConfigService;

    @PostMapping("/{subjectId}")
    ApiResponse<GradeConfigResponse> createGradeConfig(@RequestBody GradeConfigRequest request, @PathVariable String subjectId){
        return ApiResponse.<GradeConfigResponse>builder()
                .result(gradeConfigService.createGradeConfig(request, subjectId))
                .build();
    }

    @PutMapping("/{gradeConfigId}")
    ApiResponse<GradeConfigResponse> updateGradeConfig(@RequestBody GradeConfigRequest request,@PathVariable Long gradeConfigId){
        return ApiResponse.<GradeConfigResponse>builder()
                .result(gradeConfigService.updateGradeConfig(request, gradeConfigId))
                .build();
    }

    @DeleteMapping("/{gradeConfigId}")
    ApiResponse<String> deleteGrade(Long gradeConfigId){
        gradeConfigService.deleteGradeConfig(gradeConfigId);
        return ApiResponse.<String>builder()
                .result("GradeConfig has been deleted")
                .build();
    }

    // tạo cùng lúc các đầu điểm cho 1 môn
    @PostMapping("/bulk/{subjectId}")
    public ApiResponse<GradeConfigBulkResponse> createBulkGradeConfig(
            @PathVariable String subjectId,
            @RequestBody @Valid GradeConfigBulkRequest request) {

        return ApiResponse.<GradeConfigBulkResponse>builder()
                .result(gradeConfigService.createBulkGradeConfig(request, subjectId))
                .build();
    }

    // lấy các đầu điểm cho 1 môn
    @GetMapping("/bulk/{subjectId}")
    public ApiResponse<GradeConfigBulkResponse> getGradeConfigFor1Sub(@PathVariable String subjectId){
        return ApiResponse.<GradeConfigBulkResponse>builder()
                .result(gradeConfigService.getGradeConfigFor1Sub(subjectId))
                .build();
    }

    @GetMapping("/bulk-year-semseter/{subjectId}")
    public ApiResponse<GradeConfigBulkResponse> getGradeConfigSubYearSemester(@PathVariable String subjectId,
                                                                              @RequestParam int academicYear,
                                                                              @RequestParam Integer semester){
        return ApiResponse.<GradeConfigBulkResponse>builder()
                .result(gradeConfigService.getGradeConfigBySubjectIdAndAcademicYearAndSemester(subjectId, academicYear, semester))
                .build();
    }
}

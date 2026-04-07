package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.GradeBatchRequest;
import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.ClassGradeSheetResponse;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.entity.Grade;
import com.example.Qlyhocsinh.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;

    /**
     * POST /quanly/grades
     * Nhập điểm 1 học sinh.
     * Chỉ TEACHER mới được nhập điểm.
     */

    @PostMapping("/{studentId}/{gradeConfigId}")
    public ApiResponse<GradeResponse> createGrade(@Valid @RequestBody GradeRequest request,
                                                  @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt,
                                                  @PathVariable String studentId, @PathVariable Long gradeConfigId){
        String teacherId = jwt.getClaim("userId");
        return ApiResponse.<GradeResponse>builder()
                .result(gradeService.saveGrade(request, teacherId, gradeConfigId, studentId))
                .build();
    }

    /**
     * POST /quanly/grades
     * Nhập điểm các đầu điểm cho học sinh
     * Chỉ TEACHER mới được nhập điểm. File Json ví dụ: gửi request
     * {
     *   "entries": [
     *     { "gradeConfigId": 1, "entryIndex": 1, "score": 8.0 },
     *     { "gradeConfigId": 1, "entryIndex": 3, "score": 9.0 },
     *     { "gradeConfigId": 2, "entryIndex": 1, "score": 8.5 }
     *   ]
     * }
     */


    @PostMapping("/{studentId}/batch")
    public ApiResponse<List<GradeResponse>> saveBatch(@PathVariable String studentId,
                                                      @RequestBody GradeBatchRequest request,
                                                      @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt){
        String teacherId = jwt.getClaim("userId");
        return ApiResponse.<List<GradeResponse>>builder()
                .result(gradeService.saveBatch(studentId, request.getEntries(), teacherId))
                .build();
    }

    @GetMapping("/class/{classId}/subject/{subjectId}")
    public  ApiResponse<ClassGradeSheetResponse> getGradeSheet(@PathVariable Long classId,
                                                               @PathVariable String subjectId,
                                                               @RequestParam Integer semester,
                                                               @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt){
        String teacherId = jwt.getClaimAsString("userId");
        return ApiResponse.<ClassGradeSheetResponse>builder()
                .result(gradeService.getGradeSheet(classId, subjectId, semester, teacherId))
                .build();

    }




}
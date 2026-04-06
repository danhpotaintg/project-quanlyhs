package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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


}
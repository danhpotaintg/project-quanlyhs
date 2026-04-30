package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.response.GradeImportResponse;
import com.example.Qlyhocsinh.service.GradeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/excel/grades")
@RequiredArgsConstructor
public class GradeImportController {

    private final GradeImportService gradeImportService;

    @PostMapping("/class/{classId}/subject/{subjectId}/import")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<GradeImportResponse>> importExcel(
            @PathVariable Long classId,
            @PathVariable String subjectId,
            @RequestParam Integer semester,
            @RequestParam int academicYear,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt
    ) {
        String teacherId = jwt.getClaimAsString("userId");
        return ApiResponse.<List<GradeImportResponse>>builder()
                .result(gradeImportService.importFromExcel(classId, subjectId, semester, academicYear, file, teacherId))
                .build();
    }
}

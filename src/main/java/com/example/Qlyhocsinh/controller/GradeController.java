package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    ApiResponse<GradeResponse> createGrade(@RequestBody GradeRequest request) {
        return ApiResponse.<GradeResponse>builder()
                .result(gradeService.createGrade(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<GradeResponse> updateGrade(@PathVariable String id, @RequestBody GradeRequest request) {
        return ApiResponse.<GradeResponse>builder()
                .result(gradeService.updateGrade(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<GradeResponse>> getAll() {
        return ApiResponse.<List<GradeResponse>>builder()
                .result(gradeService.getAll())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
        return ApiResponse.<String>builder()
                .result("Delete grade success")
                .build();
    }

}
package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherUpdateResquest;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    ApiResponse<TeacherResponse> createTeacher(@RequestBody TeacherCreationRequest request) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.createTeacher(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<TeacherResponse> updateTeacher(@PathVariable String id, @RequestBody TeacherUpdateResquest request) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.updateTeacher(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<TeacherResponse>> getAll() {
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.getAll())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteTeacher(@PathVariable String id) {
        teacherService.deleteTea(id);
        return ApiResponse.<String>builder()
                .result("Teacher has been deleted !")
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<TeacherResponse> getTeacher() {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.getTea())
                .build();
    }

}
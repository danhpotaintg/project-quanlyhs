package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.request.StudentUpdateRequest;
import com.example.Qlyhocsinh.dto.response.StudentPreviewCreationResponse;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.service.StudentImportService;
import com.example.Qlyhocsinh.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentImportService studentImportService;
    private final StudentService studentService;

    @PostMapping
    ApiResponse<StudentResponse> createStudent(@RequestBody StudentCreationRequest request) {
        return ApiResponse.<StudentResponse>builder()
                .result(studentService.createStudent(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<StudentResponse> updateStudent(@PathVariable String id, @RequestBody StudentUpdateRequest request) {
        return ApiResponse.<StudentResponse>builder()
                .result(studentService.updateStudent(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<StudentResponse>> getAll() {
        return ApiResponse.<List<StudentResponse>>builder()
                .result(studentService.getAllStudents())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ApiResponse.<String>builder()
                .result("Student has been deleted !")
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<StudentResponse> getStudent() {
        return ApiResponse.<StudentResponse>builder()
                .result(studentService.getStudentInfo())
                .build();
    }

    @PostMapping("/import/preview")
    public ApiResponse<List<StudentPreviewCreationResponse>> getPreview(@RequestParam("file") MultipartFile file) throws Exception {
        return ApiResponse.<List<StudentPreviewCreationResponse>>builder()
                .result(studentImportService.previewData(file))
                .build();
    }

    @PostMapping("/import/confirm")
    public ApiResponse<String> confirmImport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mode") String mode) throws Exception {

        return ApiResponse.<String>builder()
                .result(studentImportService.confirmImport(file, mode))
                .build();
    }
}
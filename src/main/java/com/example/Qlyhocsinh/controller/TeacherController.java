package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherUpdateResquest;
import com.example.Qlyhocsinh.dto.response.SubjectResponse;
import com.example.Qlyhocsinh.dto.response.TeacherPreviewCreationResponse;
import com.example.Qlyhocsinh.dto.response.SubjectResponse;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.service.TeacherImportService;
import com.example.Qlyhocsinh.service.SubjectService;
import com.example.Qlyhocsinh.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final SubjectService subjectService;
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
                .result(teacherService.getTeacherInfo())
                .build();
    }
    private final TeacherImportService teacherImportService; // Tiêm service vào

    // API Xem trước
    @PostMapping("/import/preview")
    public ApiResponse<List<TeacherPreviewCreationResponse>> getPreview(@RequestParam("file") MultipartFile file) throws Exception {
        return ApiResponse.<List<TeacherPreviewCreationResponse>>builder()
                .result(teacherImportService.previewData(file))
                .build();
    }

    // API Xác nhận tạo (Chỉ có 1 nút tạo dữ liệu Xanh nên không cần biến mode nữa)
    @PostMapping("/import/confirm")
    public ApiResponse<String> confirmImport(@RequestParam("file") MultipartFile file) throws Exception {
        return ApiResponse.<String>builder()
                .result(teacherImportService.confirmImport(file))
                .build();
    }

    @GetMapping("/subject")
    ApiResponse<SubjectResponse> getSubjectByTeacherId(){
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.getSubjectByTeacherId())
                .build();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("upload-avatar")
    ApiResponse<TeacherResponse> updateAvatar(@RequestParam("file") MultipartFile file){
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.updateAvatar(file))
                .build();
    }

}
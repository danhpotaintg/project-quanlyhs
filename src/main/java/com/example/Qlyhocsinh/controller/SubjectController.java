package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.SubjectBulkRequest;
import com.example.Qlyhocsinh.dto.request.SubjectRequest;
import com.example.Qlyhocsinh.dto.response.SubjectResponse;
import com.example.Qlyhocsinh.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    ApiResponse<SubjectResponse> createSub(@RequestBody SubjectRequest request) {
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.createSub(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<SubjectResponse> updateSub(@PathVariable String id, @RequestBody SubjectRequest request) {
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.updateSub(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<SubjectResponse>> getAll() {
        return ApiResponse.<List<SubjectResponse>>builder()
                .result(subjectService.getAll())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteSub(@PathVariable String id){
        subjectService.deleteSub(id);
        return ApiResponse.<String>builder()
                .result("Subject has been deleted")
                .build();
    }

    @PostMapping("/bulk")
    ApiResponse<List<SubjectResponse>> createBulkSubjects(@RequestBody @Valid SubjectBulkRequest request){
        return ApiResponse.<List<SubjectResponse>>builder()
                .result(subjectService.createBulk(request))
                .build();
    }


}
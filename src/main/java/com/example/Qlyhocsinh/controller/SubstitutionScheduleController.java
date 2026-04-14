package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.SubstitutionScheduleRequest;
import com.example.Qlyhocsinh.dto.response.SubstitutionScheduleResponse;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.service.SubstitutionScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/substitution")
@RequiredArgsConstructor
public class SubstitutionScheduleController{
    private final SubstitutionScheduleService substitutionScheduleService;

    @PostMapping
    public ApiResponse<SubstitutionScheduleResponse> createSubstitutionSchedule(@RequestBody SubstitutionScheduleRequest request){
        return ApiResponse.<SubstitutionScheduleResponse>builder()
                .result(substitutionScheduleService.assignTeacherSubstitution(request))
                .build();
    }

    @GetMapping("/{scheduleId}")
    public ApiResponse<List<TeacherResponse>> getAllTeachersIsFree
            (@PathVariable String scheduleId, @RequestParam(defaultValue = "true") boolean onlySameSubject){
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(substitutionScheduleService.getALLTeacherIsFree(scheduleId, onlySameSubject))
                .build();
    }
}

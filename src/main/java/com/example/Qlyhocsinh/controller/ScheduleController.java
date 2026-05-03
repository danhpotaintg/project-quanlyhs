package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.ScheduleRequest;
import com.example.Qlyhocsinh.dto.response.ClassResponse;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    ApiResponse<ScheduleResponse> createSche(@RequestBody ScheduleRequest request) {
        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.createSchedule(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ScheduleResponse> updateShe(@PathVariable String id, @RequestBody ScheduleRequest request) {
        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.updateSchedule(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ScheduleResponse>> getAll() {
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAll())
                .build();
    }

    @GetMapping("/teacher/{semester}/{academicYear}")
    ApiResponse<List<ScheduleResponse>> getAllScheduleByTeacher(@PathVariable int semester, @PathVariable int academicYear) {
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAllScheduleByTeacher(semester, academicYear))
                .build();
    }

    @GetMapping("/teacher/{teacherId}/{semester}/{academicYear}")
    ApiResponse<List<ScheduleResponse>> getAllScheduleByTeacherId(@PathVariable String teacherId, @PathVariable int semester, @PathVariable int academicYear) {
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAllScheduleByTeacherID(teacherId, semester, academicYear))
                .build();
    }

    @GetMapping("/student/{semester}/{academicYear}")
    ApiResponse<List<ScheduleResponse>> getAllScheduleByStudent(@PathVariable int semester, @PathVariable int academicYear) {
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAllScheduleByStudent(semester, academicYear))
                .build();
    }

    @GetMapping("/class/{classId}/{semester}/{academicYear}")
    ApiResponse<List<ScheduleResponse>> getAllScheduleByClass(@PathVariable Long classId, @PathVariable int semester, @PathVariable int academicYear) {
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAllScheduleByClass(classId, semester, academicYear))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deledeSche(@PathVariable String id) {
        scheduleService.deleteSchedule(id);
        return ApiResponse.<String>builder()
                .result("Schedule has been deleted")
                .build();
    }

    @GetMapping("/teacher-classes/{academicYear}")
    ApiResponse<List<ClassResponse>> getAllClassByTeacherId(@PathVariable int academicYear){
        return ApiResponse.<List<ClassResponse>>builder()
                .result(scheduleService.getAllClassByTeacher(academicYear))
                .build();
    }

}
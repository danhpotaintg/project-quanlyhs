package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.AttendanceRequest;
import com.example.Qlyhocsinh.dto.response.AttendanceResponse;
import com.example.Qlyhocsinh.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    ApiResponse<AttendanceResponse> creatAttendance(@RequestBody AttendanceRequest request){
        return ApiResponse.<AttendanceResponse>builder()
                .result(attendanceService.createAttendance(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<AttendanceResponse> updateAttendance(@PathVariable Long id, @RequestBody AttendanceRequest request){
        return ApiResponse.<AttendanceResponse>builder()
                .result(attendanceService.updateAttendance(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<AttendanceResponse>> getAll(){

        return ApiResponse.<List<AttendanceResponse>>builder()
                .result(attendanceService.getAll())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteAttendance(@PathVariable Long id){
        attendanceService.deleteAttendance(id);
        return ApiResponse.<String>builder()
                .result("Delete success")
                .build();
    }
}

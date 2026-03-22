package com.example.Qlyhocsinh.controller;

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
    AttendanceResponse creatAttendance(@RequestBody AttendanceRequest request){
        return attendanceService.createAttendance(request);
    }

    @PutMapping("/{id}")
    AttendanceResponse updateAttendance(@PathVariable Long id, @RequestBody AttendanceRequest request){
        return attendanceService.updateAttendance(id, request);
    }

    @GetMapping
    List<AttendanceResponse> getAll(){
        return attendanceService.getAll();
    }

    @DeleteMapping("/{id}")
    String deleteAttendance(@PathVariable Long id){
        attendanceService.deleteAttendance(id);
        return "Delete success";
    }

}

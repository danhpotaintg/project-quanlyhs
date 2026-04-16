package com.example.Qlyhocsinh.controller;


import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.TeacherStatisticRequest;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.dto.response.TeacherStatisticResponse;
import com.example.Qlyhocsinh.service.ScheduleService;
import com.example.Qlyhocsinh.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticService statisticService;
    private final ScheduleService scheduleService;

    @GetMapping("/teachers")
    ApiResponse<List<TeacherStatisticResponse>> getTeacherStatistics(@RequestBody TeacherStatisticRequest request){
        return ApiResponse.<List<TeacherStatisticResponse>>builder()
                .result(statisticService.getTeacherStatistics(request))
                .build();
    }

    @GetMapping("/teachers/{teacherId}/schedule")
    ApiResponse<List<ScheduleResponse>> getTeacherScheduleDetails(@PathVariable String teacherId,@RequestBody TeacherStatisticRequest request){
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAllScheduleByTeacher(teacherId,request))
                .build();
    }

}

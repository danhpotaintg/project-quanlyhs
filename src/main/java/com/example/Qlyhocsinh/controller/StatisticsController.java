package com.example.Qlyhocsinh.controller;


import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.StudentRankingRequest;
import com.example.Qlyhocsinh.dto.request.SemesterAndAcademicYearRequest;
import com.example.Qlyhocsinh.dto.response.*;
import com.example.Qlyhocsinh.service.GradeService;
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
    private final GradeService gradeService;


    //Thong ke giao vien
    @GetMapping("/teachers")
    public ApiResponse<List<TeacherStatisticResponse>> getTeacherStatistics(@ModelAttribute SemesterAndAcademicYearRequest request){
        return ApiResponse.<List<TeacherStatisticResponse>>builder()
                .result(statisticService.getTeacherStatistics(request))
                .build();
    }

    //Xem lich cu the giao vien
    @GetMapping("/teachers/{teacherId}/schedule")
    public ApiResponse<List<ScheduleResponse>> getTeacherScheduleDetails(@PathVariable String teacherId,@ModelAttribute SemesterAndAcademicYearRequest request){
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAllScheduleByTeacher(teacherId,request))
                .build();
    }

    //thong ke diem hoc sinh
    @GetMapping("/class/{classId}/subject/{subjectId}")
    public ApiResponse<ClassGradeSheetResponse> getGradeSheet(@PathVariable Long classId,
                                                              @PathVariable String subjectId,
                                                              @ModelAttribute SemesterAndAcademicYearRequest request) {
        return ApiResponse.<ClassGradeSheetResponse>builder()
                .result(gradeService.getGradeSheet(classId, subjectId, request.getSemester(), request.getAcademicYear()))
                .build();

    }

    @GetMapping("/top-students")
    public ApiResponse<List<StudentRankingResponse>> getTopStudentsBySubject(@ModelAttribute StudentRankingRequest request) {
        return ApiResponse.<List<StudentRankingResponse>>builder()
                .result(statisticService.getStudentsHighestScoreBySubject(request))
                .build();
    }

}

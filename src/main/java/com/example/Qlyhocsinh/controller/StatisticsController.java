package com.example.Qlyhocsinh.controller;


import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.StudentRankingRequest;
import com.example.Qlyhocsinh.dto.request.SemesterAndAcademicYearRequest;
import com.example.Qlyhocsinh.dto.response.ClassGradeSheetResponse;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.dto.response.StudentGradeResponse;
import com.example.Qlyhocsinh.dto.response.TeacherStatisticResponse;
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
    ApiResponse<List<TeacherStatisticResponse>> getTeacherStatistics(@RequestBody SemesterAndAcademicYearRequest request){
        return ApiResponse.<List<TeacherStatisticResponse>>builder()
                .result(statisticService.getTeacherStatistics(request))
                .build();
    }

    //Xem lich cu the giao vien
    @GetMapping("/teachers/{teacherId}/schedule")
    ApiResponse<List<ScheduleResponse>> getTeacherScheduleDetails(@PathVariable String teacherId,@RequestBody SemesterAndAcademicYearRequest request){
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.getAllScheduleByTeacher(teacherId,request))
                .build();
    }

    //thong ke diem hoc sinh
    @GetMapping("/class/{classId}/subject/{subjectId}")
    public ApiResponse<ClassGradeSheetResponse> getGradeSheet(@PathVariable Long classId,
                                                              @PathVariable String subjectId,
                                                              @RequestBody SemesterAndAcademicYearRequest request) {
        return ApiResponse.<ClassGradeSheetResponse>builder()
                .result(gradeService.getGradeSheet(classId, subjectId, request.getSemester(), request.getAcademicYear()))
                .build();

    }

    @GetMapping("/top-students")
    public ApiResponse<List<StudentGradeResponse>> getTopStudentsBySubject(@RequestBody StudentRankingRequest request) {
        return ApiResponse.<List<StudentGradeResponse>>builder()
                .result(statisticService.getStudentsHighestScoreBySubject(request))
                .build();
    }

}

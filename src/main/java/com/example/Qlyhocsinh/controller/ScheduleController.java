package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ScheduleRequest;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.entity.Schedule;
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
    ScheduleResponse createSche(@RequestBody ScheduleRequest request){
        return scheduleService.createSchedule(request);
    }

    @PutMapping("/{id}")
    ScheduleResponse updateShe(@PathVariable String id, @RequestBody ScheduleRequest request){
        return scheduleService.updateSchedule(id, request);
    }

    @GetMapping
    List<ScheduleResponse> getAll(){
        return scheduleService.getAll();
    }

    @DeleteMapping("/{id}")
    String deledeSche(@PathVariable String id){
        scheduleService.deleteSchedule(id);
        return "Schedule has been deleted";
    }



}

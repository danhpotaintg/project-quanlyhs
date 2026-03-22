package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.ScheduleRequest;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.entity.Schedule;
import com.example.Qlyhocsinh.mapper.ScheduleMapper;
import com.example.Qlyhocsinh.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleResponse createSchedule(ScheduleRequest request){
        Schedule schedule = scheduleMapper.toSchedule(request);
        return scheduleMapper.toScheduleResponse(scheduleRepository.save(schedule));
    }

    public ScheduleResponse updateSchedule(String id, ScheduleRequest request){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        scheduleMapper.updateSchedule(schedule, request);

        return scheduleMapper.toScheduleResponse(scheduleRepository.save(schedule));
    }

    public List<ScheduleResponse> getAll(){
        return scheduleMapper.toScheduleResponseList(scheduleRepository.findAll());
    }

    public ScheduleResponse getById(String id){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        return scheduleMapper.toScheduleResponse(schedule);
    }

    public void deleteSchedule(String id){
        scheduleRepository.deleteById(id);
    }
}

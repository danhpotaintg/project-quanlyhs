package com.example.Qlyhocsinh.service;


import com.example.Qlyhocsinh.dto.request.TeacherStatisticRequest;
import com.example.Qlyhocsinh.dto.response.TeacherStatisticResponse;
import com.example.Qlyhocsinh.repository.StatisticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {

    private final StatisticRepository statisticRepository;

    public List<TeacherStatisticResponse> getTeacherStatistics(TeacherStatisticRequest request){
        return statisticRepository.getTeacherStatistics(request.getSemester(),request.getAcademicYear());
    }


}

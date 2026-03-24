package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.AttendanceRequest;
import com.example.Qlyhocsinh.dto.response.AttendanceResponse;
import com.example.Qlyhocsinh.entity.Attendance;
import com.example.Qlyhocsinh.mapper.AttendanceMapper;
import com.example.Qlyhocsinh.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final AttendanceRepository attendanceRepository;

    public AttendanceResponse createAttendance(AttendanceRequest request){

        Attendance attendance = attendanceMapper.toAttendance(request);

        return attendanceMapper.toAttendanceResponse(attendanceRepository.save(attendance));

    }

    public AttendanceResponse updateAttendance(Long id, AttendanceRequest request){
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Attendance not found"));

        attendanceMapper.updateAttendance(attendance, request);

        return attendanceMapper.toAttendanceResponse(attendanceRepository.save(attendance));
    }

    public List<AttendanceResponse> getAll(){
        return attendanceMapper.toAttendanceResponseList(attendanceRepository.findAll());
    }

    public void deleteAttendance(Long id){
        attendanceRepository.deleteById(id);
    }

}

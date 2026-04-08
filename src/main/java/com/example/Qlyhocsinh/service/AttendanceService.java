package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.AttendanceRequest;
import com.example.Qlyhocsinh.dto.response.AttendanceResponse;
import com.example.Qlyhocsinh.entity.Attendance;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.AttendanceMapper;
import com.example.Qlyhocsinh.repository.AttendanceRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public AttendanceResponse createAttendance(AttendanceRequest request) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));


        Attendance attendance = attendanceMapper.toAttendance(request);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStudent(student);
        attendance.setTeacher(teacher);

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

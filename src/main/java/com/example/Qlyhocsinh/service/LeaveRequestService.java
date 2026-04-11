package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.ApproveRequest;
import com.example.Qlyhocsinh.dto.request.LeaveRequestRequest;
import com.example.Qlyhocsinh.dto.response.LeaveRequestResponse;
import com.example.Qlyhocsinh.entity.LeaveRequest;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.enums.LeaveRequestStatus;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.LeaveRequestMapper;
import com.example.Qlyhocsinh.repository.LeaveRequestRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public LeaveRequestResponse createLeaveRequest(LeaveRequestRequest request) {
        var context = SecurityContextHolder.getContext();
        String studentName = context.getAuthentication().getName();

        Student student = studentRepository.findByUserUsername(studentName)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        LeaveRequest leaveRequest = leaveRequestMapper.toLeaveRequest(request);
        leaveRequest.setStudent(student);
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        leaveRequest.setApprovedByTeacher(null);

        return leaveRequestMapper.toLeaveRequestResponse(leaveRequestRepository.save(leaveRequest));
    }

    public List<LeaveRequestResponse> getAllLeaveRequestByHomeroomTeacher(){
        var context = SecurityContextHolder.getContext();
        String teacherName = context.getAuthentication().getName();

        Teacher teacher = teacherRepository.findByUserUsername(teacherName)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        List<LeaveRequest> listLeave = leaveRequestRepository.findRequestsByHomeroomTeacher(teacher.getId());

        return leaveRequestMapper.toLeaveRequestResponseList(listLeave);


    }

    public LeaveRequestResponse approvedLeaveRequest(Long id, ApproveRequest request){
        var context = SecurityContextHolder.getContext();
        String teacherName = context.getAuthentication().getName();

        Teacher teacher = teacherRepository.findByUserUsername(teacherName)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.LEAVE_REQUEST_NOT_FOUND));

        leaveRequest.setStatus(request.getStatus());
        leaveRequest.setApprovedByTeacher(teacher);
        return leaveRequestMapper.toLeaveRequestResponse(leaveRequestRepository.save(leaveRequest));
    }

    public List<LeaveRequestResponse> getStudentLeaveHistory() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Student student = studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        List<LeaveRequest> list = leaveRequestRepository.findByStudentId(student.getId());

        return leaveRequestMapper.toLeaveRequestResponseList(list);
    }
}

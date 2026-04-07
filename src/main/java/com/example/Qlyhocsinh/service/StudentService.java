package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.request.StudentUpdateRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.StudentMapper;
import com.example.Qlyhocsinh.mapper.UserMapper;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final IdGeneratorService idGeneratorService;


    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse createStudent(StudentCreationRequest request) {
        // 1. Sinh ID (K22ST00001)
        String studentId = idGeneratorService.generateId("STUDENT", String.valueOf(request.getAcademicYear()), 0);

        // 2. Sinh Username và Password
        String username = accountService.generateUsername(request.getFullName(), studentId);
        String password = accountService.generateDefaultPassword(studentId);

        User user = userMapper.toUser(request);
        user.setId(studentId);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("STUDENT");

        Student student = studentMapper.toStudent(request);
        student.setUser(user);

        studentRepository.save(student);
        return studentMapper.toStudentResponse(student);
    }

    public StudentResponse updateStudent(String id, StudentUpdateRequest request){
        Student student = studentRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        studentMapper.updateStudent(student, request);

        return studentMapper.toStudentResponse(studentRepository.save(student));
    }

    public void deleteStudent(String id){
        studentRepository.deleteById(id);
    }

    public List<StudentResponse> getAllStudents(){
        return studentMapper.toStudentResponseList(studentRepository.findAll());
    }

    public StudentResponse getStudentInfo(){
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Student student = studentRepository.findByUserUsername(userId)
                .orElseThrow(()->new AppException(ErrorCode.STUDENT_NOT_FOUND));
        return studentMapper.toStudentResponse(student);
    }

}

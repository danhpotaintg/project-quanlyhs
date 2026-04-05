package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.request.StudentUpdateRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.StudentMapper;
import com.example.Qlyhocsinh.mapper.UserMapper;
import com.example.Qlyhocsinh.repository.ClassRepository;
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
    private final ClassRepository classRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse createStudent(StudentCreationRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        ClassRoom classRoom = classRepository.findByClassName(request.getClassName())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("STUDENT");
        userRepository.save(user);

        Student student = studentMapper.toStudent(request);
        student.setUser(user);
        student.setClassRoom(classRoom);

        return studentMapper.toStudentResponse(studentRepository.save(student));

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

    public StudentResponse getStudent(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Student student = studentRepository.findByUserUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.STUDENT_NOT_FOUND));
        return studentMapper.toStudentResponse(student);
    }

}

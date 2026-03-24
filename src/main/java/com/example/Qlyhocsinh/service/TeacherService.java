package com.example.Qlyhocsinh.service;


import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherUpdateResquest;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.ClassMapper;
import com.example.Qlyhocsinh.mapper.TeacherMapper;
import com.example.Qlyhocsinh.mapper.UserMapper;
import com.example.Qlyhocsinh.repository.ClassRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import com.example.Qlyhocsinh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ClassRepository classRepository;
    private final ClassMapper classMapper;

    private final PasswordEncoder passwordEncoder;

    public TeacherResponse createTeacher(TeacherCreationRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("TEACHER");
        userRepository.save(user);

        Teacher teacher = teacherMapper.toTeacher(request);

        teacher.setUser(user);
        teacherRepository.save(teacher);

        return teacherMapper.toTeacherResponse(teacher);

    }

    public TeacherResponse updateTeacher(String id,TeacherUpdateResquest request){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacherMapper.toUpdateTeacher(teacher, request);
        return teacherMapper.toTeacherResponse(teacherRepository.save(teacher));
    }

    public List<TeacherResponse> getAll(){
        var tea = teacherRepository.findAll();
        return tea.stream().map(teacherMapper::toTeacherResponse).toList();
    }

    public TeacherResponse getTea(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Teacher teacher = teacherRepository.findByUserUsername(name)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        return teacherMapper.toTeacherResponse(teacher);
    }

    public void deleteTea(String id){
        teacherRepository.deleteById(id);
    }


}

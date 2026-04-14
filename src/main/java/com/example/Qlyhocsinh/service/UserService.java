package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.UserCreationRequest;
import com.example.Qlyhocsinh.dto.request.UserUpdateByIDRequest;
import com.example.Qlyhocsinh.dto.request.UserUpdateRequest;
import com.example.Qlyhocsinh.dto.response.UserResponse;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.enums.Role;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.UserMapper;
import com.example.Qlyhocsinh.repository.ClassRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import com.example.Qlyhocsinh.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class    UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    TeacherRepository teacherRepository;
    ClassRepository classRepository;
    StudentRepository studentRepository;

    PasswordEncoder passwordEncoder;

//    public UserResponse createUser(UserCreationRequest request){
//        if(userRepository.findByUsername(request.getUsername()).isPresent()){
//            throw new AppException(ErrorCode.USERNAME_EXISTED);
//        }
//
//        User user = userMapper.toUser(request);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        user.setRole(Role.USER.name());
//
//        return userMapper.toUserResponse(userRepository.save(user));
//    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(){
        log.info("O trong method getUsers");
        return userRepository.findAllByIsActiveTrue().stream()
                .map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserByID(String userId, UserUpdateByIDRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUserByID(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }


    public UserResponse updateUser(UserUpdateRequest request) {

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isPasswordMatch = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }


        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getRole().equals("TEACHER")) {
            Teacher teacher = teacherRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

            classRepository.findByTeacherUserId(userId).ifPresent(classRoom -> {
                classRoom.setTeacher(null);
                classRepository.save(classRoom);
            });


        } else if (user.getRole().equals("STUDENT")) {
            Student student = studentRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

            student.setClassRoom(null);
            studentRepository.save(student);
        }

        user.setActive(false);
        userRepository.save(user);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id){
        log.info("O trong method getUser by ID");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }


    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info("o trong getMyInfo");
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_DISABLED);
        }

        return userMapper.toUserResponse(user);
    }
}

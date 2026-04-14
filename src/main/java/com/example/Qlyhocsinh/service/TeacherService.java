package com.example.Qlyhocsinh.service;


import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherUpdateResquest;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.entity.*;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.ClassMapper;
import com.example.Qlyhocsinh.mapper.TeacherMapper;
import com.example.Qlyhocsinh.mapper.UserMapper;
import com.example.Qlyhocsinh.repository.ClassRepository;
import com.example.Qlyhocsinh.repository.SubjectRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import com.example.Qlyhocsinh.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final IdGeneratorService idGeneratorService;

    @PreAuthorize("hasRole('ADMIN')")
    public TeacherResponse createTeacher(TeacherCreationRequest request){
        String teacherId = idGeneratorService.generateId("TEACHER", "ALL", request.getDob().getYear());

        // 2. Sinh Username và Password
        String username = accountService.generateUsername(request.getFullName(), teacherId);
        String password = accountService.generateDefaultPassword(teacherId);

        Subject subject = subjectRepository.findBySubjectName(request.getSubjectName())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        User user = userMapper.toUser(request);
        user.setId(teacherId);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("TEACHER");

        Teacher teacher = teacherMapper.toTeacher(request);
        teacher.setUser(user);
        teacher.setSubject(subject);

        teacherRepository.save(teacher);
        return teacherMapper.toTeacherResponse(teacher);
    }

    public TeacherResponse updateTeacher(String id,TeacherUpdateResquest request){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        teacherMapper.toUpdateTeacher(teacher, request);
        return teacherMapper.toTeacherResponse(teacherRepository.save(teacher));
    }

    public List<TeacherResponse> getAll(){
        var tea = teacherRepository.findAllActiveTeacher();
        return tea.stream().map(teacherMapper::toTeacherResponse).toList();
    }

    public TeacherResponse getTeacherInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Teacher teacher = teacherRepository.findByUserUsername(name)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        return teacherMapper.toTeacherResponse(teacher);
    }

    public void deleteTea(String id){
        teacherRepository.deleteById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherResponse updateAvatar(MultipartFile file){

        // Lấy username từ token
        var context = SecurityContextHolder.getContext();
        String currentUsername = context.getAuthentication().getName();

        //  Tìm teacher theo username
        Teacher teacher = teacherRepository.findByUserUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Validate file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ cho phép file ảnh (jpg, png...)");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new RuntimeException("File phải nhỏ hơn 2MB");
        }

        // Lưu file
        User user = teacher.getUser();

        String fileName = fileStorageService.save(file);

        // Xóa avatar cũ nếu có
        if (user.getAvatar() != null) {
            fileStorageService.delete(user.getAvatar());
        }

        user.setAvatar(fileName);
        userRepository.save(user);

        return teacherMapper.toTeacherResponse(teacher);
    }

}

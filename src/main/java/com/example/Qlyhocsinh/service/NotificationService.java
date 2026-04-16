package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.SendNotificationRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.StudentMapper;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import com.example.Qlyhocsinh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final JavaMailSender mailSender;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('TEACHER')")
    public List<StudentResponse> getStudentsOfHomeroomTeacher(String username) {
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        if (teacher.getClassRoom() == null) {
            throw new AppException(ErrorCode.TEACHER_NOT_HOMEROOM);
        }

        Long classId = teacher.getClassRoom().getId();
        List<Student> students = studentRepository.findByClassRoomId(classId);

        return studentMapper.toStudentResponseList(students);
    }

    public void sendEmailToUser(SendNotificationRequest request) {
        if (request.getUserId() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        User userSelected = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if ("STUDENT".equals(userSelected.getRole())) {
            Student student = studentRepository.findById(userSelected.getId())
                    .orElseThrow();
            String headText = "Kính gửi phụ huynh em ";
            String email = student.getParentGmail();
            if (isValidEmail(email)) {
                sendSimpleEmail(
                        email,
                        request.getSubject(),
                        request.getContent(),
                        headText,
                        student.getFullName()
                );
            }

        }else if ("TEACHER".equals(userSelected.getRole())) {
            Teacher teacher = teacherRepository.findById(userSelected.getId())
                    .orElseThrow();
            String headText = "Kính gửi thầy/cô ";
            String email = teacher.getEmail();
            if (isValidEmail(email)) {
                sendSimpleEmail(
                        email,
                        request.getSubject(),
                        request.getContent(),
                        headText,
                        teacher.getFullName()
                );
            }
        }
    }

    public void sendNewAccountToUser(String username,String password, String email, String fullName){
        String subject = "Tài khoản mới trong hệ thống của bạn";
        String headText = "Xin chào ";
        String content = "Đây là tài khoản và mật khẩu của bạn:\n"+"Tài khoản: " + username + "\n" + "Mật khẩu: " + password + "\n"+"Vui lòng đổi mật khẩu sớm!";
        if(isValidEmail(email)){
            sendSimpleEmail(email,subject,content,headText,fullName);
        }
    }

    private void sendSimpleEmail(String to, String subject, String content,String headText, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);

            message.setText(headText + fullName + ",\n\n" + content);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi gửi email đến {}", to, e);
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;

        email = email.trim();
        if (email.isEmpty()) return false;

        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
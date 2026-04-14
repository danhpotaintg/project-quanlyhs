package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.SendNotificationRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.StudentMapper;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
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

    public void sendEmailToParents(SendNotificationRequest request) {
        if (request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_STUDENT_LIST);
        }

        List<Student> selectedStudents = studentRepository.findAllById(request.getStudentIds());

        for (Student student : selectedStudents) {
            String parentEmail = student.getParentGmail();
            if (parentEmail != null && !parentEmail.trim().isEmpty()) {
                sendSimpleEmail(parentEmail, request.getSubject(), request.getContent(), student.getFullName());
            }
        }
    }

    private void sendSimpleEmail(String to, String subject, String content, String studentName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText("Kính gửi phụ huynh em " + studentName + ",\n\n" + content);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email đến " + to + ": " + e.getMessage());
        }
    }
}
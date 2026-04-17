package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.AdminNotificationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherNotificationRequest;
import com.example.Qlyhocsinh.dto.response.NotificationResponse;
import com.example.Qlyhocsinh.entity.*;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.NotificationMapper;
import com.example.Qlyhocsinh.repository.NotificationRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebNotificationService {

    NotificationRepository notificationRepository;
    StudentRepository studentRepository;
    TeacherRepository teacherRepository;

    NotificationMapper notificationMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public void createAdminNotification(String senderUsername, AdminNotificationRequest request) {

        AdminNotification notif = notificationMapper.toAdminNotification(request);

        notif.setSenderUsername(senderUsername);
        notif.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notif);
    }

    @PreAuthorize("hasRole('TEACHER')")
    public void createTeacherNotification(TeacherNotificationRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        if (teacher.getClassRoom() == null) {
            throw new AppException(ErrorCode.TEACHER_NOT_HOMEROOM);
        }

        Long classId = teacher.getClassRoom().getId();

        TeacherNotification notif = notificationMapper.toTeacherNotification(request);

        notif.setClassId(classId);
        notif.setSenderUsername(teacher.getFullName());
        notif.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notif);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public List<NotificationResponse> getNotificationsForStudent(String username) {
        Student student = studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Long classId = student.getClassRoom() != null ? student.getClassRoom().getId() : null;

        List<Notification> notifications = notificationRepository.findForStudent(classId);


        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('TEACHER')")
    public List<NotificationResponse> getNotificationsForTeacher(String teacherUsername) {
        Teacher teacher = teacherRepository.findByUserUsername(teacherUsername)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        List<Notification> notifications = notificationRepository.findForTeacher(teacher.getId());

        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }
}
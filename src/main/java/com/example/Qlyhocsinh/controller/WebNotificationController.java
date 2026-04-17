package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.AdminNotificationRequest;
import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.TeacherNotificationRequest;
import com.example.Qlyhocsinh.dto.response.NotificationResponse;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.service.WebNotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebNotificationController {

    WebNotificationService webNotificationService;

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> sendAdminNotification(@RequestBody AdminNotificationRequest request) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        webNotificationService.createAdminNotification(username, request);

        return ApiResponse.<String>builder()
                .result("SUCCESS")
                .message("Đã gửi thông báo!")
                .build();
    }

    @PostMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<String> sendTeacherNotification(@RequestBody TeacherNotificationRequest request) {

        webNotificationService.createTeacherNotification(request);

        return ApiResponse.<String>builder()
                .result("SUCCESS")
                .message("Đã gửi thông báo cho lớp học!")
                .build();
    }

    @GetMapping("/my-notifications")
    ApiResponse<List<NotificationResponse>> getMyNotifications() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        List<NotificationResponse> notifications = isStudent
                ? webNotificationService.getNotificationsForStudent(authentication.getName())
                : webNotificationService.getNotificationsForTeacher(authentication.getName());

        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notifications)
                .build();
    }
}
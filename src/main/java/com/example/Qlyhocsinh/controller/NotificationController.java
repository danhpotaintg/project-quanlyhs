package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.SendNotificationRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my-class-students")
    public ApiResponse<List<StudentResponse>> getMyClassStudents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<StudentResponse> students = notificationService.getStudentsOfHomeroomTeacher(username);

        return ApiResponse.<List<StudentResponse>>builder()
                .result(students)
                .message("Lấy danh sách thành công")
                .build();
    }

    // Nhận request gửi mail
    @PostMapping("/send")
    public ApiResponse<String> sendNotification(@RequestBody SendNotificationRequest request) {

        notificationService.sendEmailToUser(request);

        return ApiResponse.<String>builder()
                .result("SUCCESS")
                .message("Gửi thông báo thành công!")
                .build();
    }
}
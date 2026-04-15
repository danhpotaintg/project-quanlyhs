package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.AdminNotificationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherNotificationRequest;
import com.example.Qlyhocsinh.dto.response.NotificationResponse;
import com.example.Qlyhocsinh.entity.AdminNotification;
import com.example.Qlyhocsinh.entity.Notification;
import com.example.Qlyhocsinh.entity.TeacherNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderUsername", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AdminNotification toAdminNotification(AdminNotificationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderUsername", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "classId", ignore = true)
    TeacherNotification toTeacherNotification(TeacherNotificationRequest request);

    NotificationResponse toNotificationResponse(Notification notification);

}
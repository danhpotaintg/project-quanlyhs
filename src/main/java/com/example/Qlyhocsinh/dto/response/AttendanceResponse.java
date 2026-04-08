package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class AttendanceResponse {

    Long id;
    LocalDateTime checkInTime;
    String status;
    String studentName;
    String teacherName;

}

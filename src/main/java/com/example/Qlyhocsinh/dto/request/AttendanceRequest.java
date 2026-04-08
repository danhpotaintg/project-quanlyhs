package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class AttendanceRequest {

    String studentId;
    String status;

}

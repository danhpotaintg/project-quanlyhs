package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class AttendanceResponse {

    String id;
    LocalTime checkInTime;
    String status;

}

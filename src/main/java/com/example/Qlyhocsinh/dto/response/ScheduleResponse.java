package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class ScheduleResponse {
    String id;
    private LocalTime startTime;;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
}

package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class ScheduleRequest{
    Long classId;
    String subjectId;
    String teacherId;
    private LocalTime startTime;;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    int semester;
    int academicYear;
}

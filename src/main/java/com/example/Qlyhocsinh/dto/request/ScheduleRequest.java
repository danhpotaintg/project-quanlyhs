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
    String subjectName;
    String teacherId;
    private int startLesson;
    private int endLesson;
    private int dayOfWeek;
    int semester;
    int academicYear;
}

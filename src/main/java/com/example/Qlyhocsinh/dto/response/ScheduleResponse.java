package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class ScheduleResponse {
    String id;

    Long classId;
    String className;

    String subjectId;
    String subjectName;

    String teacherId;
    String teacherName;
    private int startLesson;
    private int endLesson;
    private int dayOfWeek;

    int semester;
    int academicYear;
}

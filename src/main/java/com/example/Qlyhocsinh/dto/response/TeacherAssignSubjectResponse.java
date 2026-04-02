package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherAssignSubjectResponse {

    String teacherId;
    String teacherName;


    String subjectId;
    String subjectName;


    int assignedYear;
}

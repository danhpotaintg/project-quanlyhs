package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherAssignmentResponse {

    String teacherId;
    String teacherName;


    String subjectId;
    String subjectName;

    boolean isPrimary;
    int assignedYear;
}

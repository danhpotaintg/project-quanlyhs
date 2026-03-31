package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherAssignmentRequest {
    String teacherId;
    String subjectId;
    boolean isPrimary;
    int assignedYear;
}

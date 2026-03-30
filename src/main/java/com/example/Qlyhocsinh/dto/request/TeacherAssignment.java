package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherAssignment {
    String teacherId;
    String subjectId;
    boolean isPrimary;
    int assignedYear;
}

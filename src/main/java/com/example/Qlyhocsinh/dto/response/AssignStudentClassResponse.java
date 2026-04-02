package com.example.Qlyhocsinh.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignStudentClassResponse {
    String className;
    int academicYear;
    List<String> assignedStudentIds;
}

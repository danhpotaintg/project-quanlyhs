package com.example.Qlyhocsinh.dto.response;

import com.example.Qlyhocsinh.entity.Teacher;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class ClassResponse {
    Long id;
    String className;
    String academicYear;

    String teacherName;
}

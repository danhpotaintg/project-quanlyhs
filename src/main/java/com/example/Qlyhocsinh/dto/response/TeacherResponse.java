package com.example.Qlyhocsinh.dto.response;

import com.example.Qlyhocsinh.entity.TeacherAssignSubject;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherResponse {

    String id;
    String fullName;;
    LocalDate dob;
    String gender;
    UserResponse user;

    String className;

    String subjectName;

    private String avatarUrl;

}

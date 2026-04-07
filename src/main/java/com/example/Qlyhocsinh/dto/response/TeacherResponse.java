package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String email;
    String phoneNumber;
    UserResponse user;

    String className;

    String subjectName;

    private String avatarUrl;

}

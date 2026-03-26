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
    UserResponse user;

    String className;

    private String avatarUrl;

}

package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherCreationRequest {
    //user
    private String username;
    private String password;

    //teacher
    private String fullName;
    private LocalDate dob;
    private String gender;

    private String subjectName;

}

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

    private String fullName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phoneNumber;

    private String subjectName;

}

package com.example.Qlyhocsinh.dto.response;

import com.example.Qlyhocsinh.entity.Subject;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class TeacherPreviewCreationResponse {

    String id;

    String fullName;
    LocalDate dob;
    String gender;
    String email;
    String phoneNumber;
    String subjectName;

    boolean valid;
    String errorNote;

}

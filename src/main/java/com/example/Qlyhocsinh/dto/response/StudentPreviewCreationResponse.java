package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class StudentPreviewCreationResponse {
    String id;

    String fullName;
    LocalDate dob;
    String parentGmail;
    String parentPhonenumber;
    String gender;
    int academicYear;
    String className;

    boolean valid;
    String errorNote;

    boolean hasWarning;
    String warningNote;
}

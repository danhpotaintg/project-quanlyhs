package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentCreationRequest {

    String fullName;
    LocalDate dob;
    String parentGmail;
    String parentPhonenumber;
    String gender;
    int academicYear;

}
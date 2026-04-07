package com.example.Qlyhocsinh.dto.response;

import com.example.Qlyhocsinh.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentResponse {

    String id;

    String fullName;
    LocalDate dob;
    String parentGmail;
    String parentPhonenumber;
    String gender;
    int academicYear;

    // user info
    UserResponse user;

    // class info;
    ClassResponse classRoom;
}
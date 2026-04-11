package com.example.Qlyhocsinh.dto.response;

import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.enums.LeaveRequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class LeaveRequestResponse {
    Long id;

    String reason;

    LocalDate fromDate;

    LocalDate toDate;

    LeaveRequestStatus status;

    String studentName;
    String className;
    String approvedByTeacherName;
}

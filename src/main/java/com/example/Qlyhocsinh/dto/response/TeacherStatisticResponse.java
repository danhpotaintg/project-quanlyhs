package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherStatisticResponse {
    String teacherId;
    String fullName;
    String gender;
    String email;
    Long schedulePerWeek;
    Long countClassTeaching;
}

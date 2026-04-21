package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeConfigRequest {

    Integer semester;
    int academicYear;
    String scoreType;
    Double weight;
    Integer maxEntries;
}

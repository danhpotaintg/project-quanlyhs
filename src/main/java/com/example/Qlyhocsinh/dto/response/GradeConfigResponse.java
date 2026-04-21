package com.example.Qlyhocsinh.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeConfigResponse {
    String id;

    String subjectId;
    String subjectName;

    Integer semester;
    int academicYear;
    String scoreType;
    Double weight;
    Integer maxEntries;
}

package com.example.Qlyhocsinh.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeConfigResponse {
    String id;

    String subjectId;
    String subjectName;

    int semester;
    String scoreType;
    float weight;
    int maxEntries;
}

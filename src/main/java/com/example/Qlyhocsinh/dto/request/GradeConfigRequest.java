package com.example.Qlyhocsinh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeConfigRequest {
    String id;
    int semester;
    String score_type;
    float weight; //he so
    int max_entries;
}

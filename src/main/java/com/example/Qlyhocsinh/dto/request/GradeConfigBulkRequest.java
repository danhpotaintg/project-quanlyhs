package com.example.Qlyhocsinh.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradeConfigBulkRequest {

    @NotEmpty
    private List<@Valid GradeConfigRequest> configs;

}

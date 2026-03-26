package com.example.Qlyhocsinh.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateByIDRequest {

    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;
}

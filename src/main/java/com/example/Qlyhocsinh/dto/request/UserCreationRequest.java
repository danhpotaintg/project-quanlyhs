package com.example.Qlyhocsinh.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserCreationRequest {
    @Size(min = 3, message = "INVALID_USERNAME")
    private String username;
    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;

}

package com.example.Qlyhocsinh.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdSequenceId implements Serializable {
    private String type;
    private String period;
}
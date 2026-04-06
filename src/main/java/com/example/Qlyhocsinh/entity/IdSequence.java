package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "id_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(IdSequenceId.class) // Kết nối với class IdSequenceId
public class IdSequence {

    @Id
    @Column(name = "seq_type")
    private String type;   // 'STUDENT' hoặc 'TEACHER'

    @Id
    @Column(name = "seq_period")
    private String period; // '2021', '2022', hoặc 'ALL'

    @Column(name = "current_value", nullable = false)
    private int currentValue;
}
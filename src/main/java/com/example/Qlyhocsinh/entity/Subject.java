package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "subject")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subject {

    @Id
    @Column(name = "subject_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String subjectName;

}

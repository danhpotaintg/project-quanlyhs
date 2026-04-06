package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teacher")
public class Teacher {

    @Id
    @Column(name = "teacher_id")
    private String userId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "teacher_id")
    private User user;

    private String fullName;
    private LocalDate dob;
    private String gender;

    @OneToOne(mappedBy = "teacher")
    private ClassRoom classRoom;

    private String subjectId;

}

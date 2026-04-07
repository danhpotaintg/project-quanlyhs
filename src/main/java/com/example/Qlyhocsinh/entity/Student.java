package com.example.Qlyhocsinh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {

    @Id
    @Column(name = "student_id")
    String id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "student_id")
    private User user;

    String fullName;
    LocalDate dob;
    String parentGmail;
    String parentPhonenumber;
    String gender;
    int academicYear;

    @ManyToOne
    @JoinColumn(name = "class_id")
    ClassRoom classRoom;

}

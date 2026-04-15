package com.example.Qlyhocsinh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teacher_notification")
public class TeacherNotification extends Notification{

    @Column(name = "class_id")
    Long classId;
}

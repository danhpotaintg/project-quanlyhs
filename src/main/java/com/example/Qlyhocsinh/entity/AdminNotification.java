package com.example.Qlyhocsinh.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_notification")
public class AdminNotification extends Notification{
    boolean toStudents;
    boolean toTeachers;

    Long targetClassId;
    String targetTeacherId;
}

package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT n FROM Notification n WHERE " +
            "(TYPE(n) = AdminNotification AND " +
            "(treat(n as AdminNotification).toStudents = true AND treat(n as AdminNotification).targetClassId IS NULL) OR" +
            "(treat(n as AdminNotification).targetClassId = :classId) OR " +
            "(TYPE(n) = TeacherNotification AND treat(n as TeacherNotification).classId = :classId) " +
            ") ORDER BY n.createdAt DESC")
    List<Notification> findForStudent(@Param("classId") Long classId);

    @Query("SELECT n FROM Notification n WHERE " +
            "TYPE(n) = AdminNotification AND (" +
            "(treat(n as AdminNotification).toTeachers = true AND treat(n as AdminNotification).targetTeacherId IS NULL) OR " +
            "(treat(n as AdminNotification).targetTeacherId = :teacherId)" +
            ") ORDER BY n.createdAt DESC")
    List<Notification> findForTeacher(@Param("teacherId") String teacherId);
}
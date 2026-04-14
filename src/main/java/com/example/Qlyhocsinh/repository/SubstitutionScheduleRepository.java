package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.SubstitutionSchedule;
import com.example.Qlyhocsinh.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubstitutionScheduleRepository extends JpaRepository<SubstitutionSchedule, Long> {
    //lấy danh sách gv rảnh
    @Query("SELECT t FROM Teacher t " +
            "WHERE (:subjectName IS NULL OR t.subject.subjectName = :subjectName) " +
            "AND t.id NOT IN (" +
            "  SELECT s.teacher.id FROM Schedule s " +
            "  WHERE s.dayOfWeek = :day " +
            "  AND (s.startLesson <= :endLesson AND s.endLesson >= :startLesson)" +
            ")")
    List<Teacher> findAvailableSubstitutes(String subjectName, int day, int startLesson, int endLesson);
}

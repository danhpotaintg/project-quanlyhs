package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.dto.response.TeacherStatisticResponse;
import com.example.Qlyhocsinh.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatisticRepository extends JpaRepository<Teacher, String> {

    @Query("SELECT new com.example.Qlyhocsinh.dto.response.TeacherStatisticResponse(" +
            "t.id, t.fullName, t.gender, t.email, " +
            "COALESCE(SUM(s.endLesson - s.startLesson + 1), 0L), " +
            "COUNT(DISTINCT s.classRoom.id)) " +
            "FROM Teacher t " +
            "LEFT JOIN Schedule s ON s.teacher.id = t.id " +
            "AND s.semester = :semester AND s.academicYear = :academicYear " +
            "GROUP BY t.id, t.fullName, t.gender, t.email")
    List<TeacherStatisticResponse> getTeacherStatistics(@Param("semester") int semester,
                                                        @Param("academicYear") int academicYear);
}
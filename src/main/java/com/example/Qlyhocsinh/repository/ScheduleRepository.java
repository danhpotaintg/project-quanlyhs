package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    @Query("SELECT DISTINCT s.classRoom FROM Schedule s WHERE s.teacher.id = :teacherId")
    List<ClassRoom> findDistinctClassByTeacherId(@Param("teacherId") String teacherId);
}

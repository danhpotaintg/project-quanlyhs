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
    //lấy danh sách lớp giáo viên dang dạy
    @Query("SELECT DISTINCT s.classRoom FROM Schedule s WHERE s.teacher.id = :teacherId " +
    "AND s.academicYear = :academicYear")
    List<ClassRoom> findDistinctClassByTeacherId(@Param("teacherId") String teacherId, @Param("academicYear") int academicYear);

    List<Schedule> findByTeacherId(String teacherId);
    List<Schedule> findByTeacherIdAndSemesterAndAcademicYear(String teacherId, int semester, int academicYear);

    List<Schedule> findByClassRoomId(Long classId);
    List<Schedule> findByClassRoomIdAndSemesterAndAcademicYear(long classId, int semester, int academicYear);
    //kiểm tra trùng lịch
    @Query("SELECT s FROM Schedule s WHERE s.academicYear = :year " +
            "AND s.semester = :semester " +
            "AND s.dayOfWeek = :day " +
            "AND ((s.startLesson <= :end AND s.endLesson >= :start))")
    List<Schedule> findScheduleConflicts(
            int year,
            int semester,
            int day,
            int start,
            int end
    );
}

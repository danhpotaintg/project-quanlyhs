package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByClassRoomId(Long classId);
    Optional<Student> findByUserUsername(String name);
    Optional<Student> findByUserIdAndClassRoomId(String id, Long classId);

    @Query("SELECT s FROM Student s WHERE s.classRoom IS NULL AND s.user.isActive = true")
    List<Student> findAllWithoutClass();

    @Query("SELECT s FROM Student s WHERE s.classRoom.id = :classId AND s.user.isActive = true")
    List<Student> findAllActiveByClass(Long classId);


    @Query("SELECT s FROM Student s " +
            "JOIN Grade g ON s.id = g.studentId " +
            "JOIN GradeConfig gc ON g.gradeConfigId = gc.id " +
            "WHERE gc.subjectId = :subjectId " +
            "AND gc.semester = :semester " +
            "AND gc.academicYear = :academicYear " +
            "AND s.academicYear = :studentCohort " + // s.academicYear đại diện cho khóa học của học sinh
            "GROUP BY s " +
            "ORDER BY (SUM(g.score * gc.weight) / SUM(gc.weight)) DESC")
    List<Student> findTopStudentsBySubjectAndSemester(
            @Param("subjectId") String subjectId,
            @Param("semester") int semester,
            @Param("academicYear") int academicYear,
            @Param("studentCohort") int studentCohort,
            Pageable pageable);
}

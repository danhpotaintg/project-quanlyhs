package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {
    Optional<Subject> findBySubjectName(String subjectName);
    List<Subject> findBySubjectNameIn(List<String> subjectName);

    @Query(value = """
        SELECT DISTINCT sub.*
        FROM subject sub
        JOIN schedule sc ON sc.subject_id = sub.subject_id
        WHERE sc.class_id = :classId
        AND sc.semester = :semester
    """, nativeQuery = true)
    List<Subject> findByClassIdAndSemester(
            @Param("classId") Long classId,
            @Param("semester") Integer semester
    );
}


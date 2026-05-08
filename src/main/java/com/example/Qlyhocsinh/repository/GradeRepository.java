package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.dto.projection.GradeRawRow;
import com.example.Qlyhocsinh.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {


    @Query(value = """
    SELECT s.student_id AS studentId,
           s.full_name  AS studentName,
           gc.grade_config_id AS gradeConfigId,
           gc.score_type      AS gradeConfigName,
           gc.max_entries     AS maxEntries,
           gc.weight          AS weight,
           gc.semester        AS semester,
           gc.academic_year   AS academicYear,
           gc.subject_id      AS subjectId,
           gen.entry_index    AS entryIndex,
           g.score            AS score
    FROM student s
    JOIN class c ON c.class_id = s.class_id
    CROSS JOIN grade_config gc
    JOIN (
        SELECT 1 AS entry_index UNION ALL
        SELECT 2 UNION ALL
        SELECT 3 UNION ALL
        SELECT 4 UNION ALL
        SELECT 5
    ) gen ON gen.entry_index <= gc.max_entries
    LEFT JOIN grades g ON g.student_id = s.student_id
        AND g.grade_config_id = gc.grade_config_id
        AND g.entry_index = gen.entry_index
    WHERE s.class_id = :classId
        AND gc.subject_id = :subjectId
        AND gc.semester = :semester
        AND gc.academic_year = :academicYear
    ORDER BY s.full_name, gc.semester, gc.score_type, gen.entry_index
""", nativeQuery = true)
    List<GradeRawRow> findGradeSheet(
            @Param("classId") Long classId,
            @Param("subjectId") String subjectId,
            @Param("semester") Integer semester,
            @Param("academicYear") int academicYear
    );

    @Query(value = """
    SELECT gc.grade_config_id AS gradeConfigId,
           gc.score_type      AS gradeConfigName,
           gc.weight          AS weight,
           gc.max_entries     AS maxEntries,
           gc.subject_id      AS subjectId,
           gc.semester        AS semester,
           gc.academic_year   AS academicYear,
           gen.entry_index    AS entryIndex,
           g.score            AS score
    FROM grade_config gc
    JOIN (
        SELECT 1 AS entry_index UNION ALL
        SELECT 2 UNION ALL
        SELECT 3 UNION ALL
        SELECT 4 UNION ALL
        SELECT 5
    ) gen ON gen.entry_index <= gc.max_entries
    LEFT JOIN grades g ON g.grade_config_id = gc.grade_config_id
        AND g.student_id = :studentId
        AND g.entry_index = gen.entry_index
    WHERE gc.subject_id = :subjectId
        AND gc.semester = :semester
        AND gc.academic_year = :academicYear
    ORDER BY gc.score_type, gen.entry_index
""", nativeQuery = true)
    List<GradeRawRow> findStudentGradeBySubject(
            @Param("studentId") String studentId,
            @Param("subjectId") String subjectId,
            @Param("semester") Integer semester,
            @Param("academicYear") int academicYear
    );

    @Query(value = """
    SELECT s.student_id         AS studentId,
           s.full_name          AS studentName,
           sub.subject_id       AS subjectId,
           sub.subject_name     AS subjectName,
           gc.grade_config_id   AS gradeConfigId,
           gc.score_type        AS gradeConfigName,
           gc.max_entries       AS maxEntries,
           gc.weight            AS weight,
           gc.semester          AS semester,
           gc.academic_year     AS academicYear,
           gen.entry_index      AS entryIndex,
           g.score              AS score
    FROM grade_config gc
    JOIN subject sub ON sub.subject_id = gc.subject_id
    JOIN student s ON s.student_id = :studentId
    JOIN (
        SELECT 1 AS entry_index UNION ALL
        SELECT 2 UNION ALL
        SELECT 3 UNION ALL
        SELECT 4 UNION ALL
        SELECT 5
    ) gen ON gen.entry_index <= gc.max_entries
    LEFT JOIN grades g ON g.student_id = s.student_id
        AND g.grade_config_id = gc.grade_config_id
        AND g.entry_index = gen.entry_index
    WHERE gc.semester = :semester
        AND gc.academic_year = :academicYear
    ORDER BY sub.subject_name, gc.grade_config_id, gen.entry_index
""", nativeQuery = true)
    List<GradeRawRow> findAllSubjectsGradeByStudent(
            @Param("studentId") String studentId,
            @Param("semester") Integer semester,
            @Param("academicYear") int academicYear
    );

    Optional<Grade> findByStudentIdAndGradeConfigIdAndEntryIndex(String studentId, Long gradeConfigId, Integer entryIndex);

    List<Grade> findByStudentIdAndGradeConfigIdIn(String studentId, List<Long> gradeConfigIds);

}

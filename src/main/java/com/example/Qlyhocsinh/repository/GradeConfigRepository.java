package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.GradeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeConfigRepository extends JpaRepository<GradeConfig, Long> {

    List<GradeConfig> findBySubjectId(String subjectId);

    List<GradeConfig> findBySubjectIdAndAcademicYearAndSemester(String subjectId, int academicYear, Integer semester);

    List<GradeConfig> findBySubjectIdAndSemesterAndAcademicYear(String subjectId, int semester,int academicYear);
}

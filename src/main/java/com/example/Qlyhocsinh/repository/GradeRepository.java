package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Kiểm tra trùng (student + loại điểm + lần nhập)
    boolean existsByStudentIdAndGradeConfigIdAndEntryIndex(String studentId, Long gradeConfigId, Integer entryIndex);

    // Đếm số lần nhập điểm của 1 học sinh trong 1 gradeConfig
    long countByStudentIdAndGradeConfigId(String studentId, Long gradeConfigId);

    Optional<Grade> findByStudentIdAndGradeConfigIdAndEntryIndex(String studentId, Long gradeConfigId, Integer entryIndex);



}

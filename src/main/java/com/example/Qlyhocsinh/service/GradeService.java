package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.entity.Grade;
import com.example.Qlyhocsinh.entity.GradeConfig;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.GradeMapper;
import com.example.Qlyhocsinh.repository.GradeConfigRepository;
import com.example.Qlyhocsinh.repository.GradeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;
    private final GradeConfigRepository gradeConfigRepository;

    //  nhập điểm cho 1 học sinh
    @PreAuthorize("hasRole('TEACHER')")
    @Transactional
    public GradeResponse saveGrade(GradeRequest request, String teacherId, Long gradeConfigId, String studentId){
        GradeConfig gradeConfig = gradeConfigRepository.findById(gradeConfigId)
                .orElseThrow(() -> new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND));

        validateEntryIndex(request.getEntryIndex(), gradeConfig.getMaxEntries());

        Grade grade = gradeRepository.findByStudentIdAndGradeConfigIdAndEntryIndex(studentId, gradeConfigId, request.getEntryIndex())
                .orElse(new Grade());

        if(grade.getId() == null){
            grade.setStudentId(studentId);
            grade.setTeacherId(teacherId);
            grade.setGradeConfigId(gradeConfigId);
        }

        grade.setScore(request.getScore());
        grade.setEntryIndex(request.getEntryIndex());

        return gradeMapper.toResponse(gradeRepository.save(grade));
    }


    private void validateEntryIndex(Integer entryIndex, Integer maxEntries) {
        if (entryIndex < 1 || entryIndex > maxEntries) {
            throw new AppException(ErrorCode.GRADE_ENTRY_INDEX_INVALID);
        }
    }

}

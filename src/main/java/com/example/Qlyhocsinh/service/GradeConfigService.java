package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.GradeConfigBulkRequest;
import com.example.Qlyhocsinh.dto.request.GradeConfigRequest;
import com.example.Qlyhocsinh.dto.response.GradeConfigBulkResponse;
import com.example.Qlyhocsinh.dto.response.GradeConfigResponse;
import com.example.Qlyhocsinh.entity.GradeConfig;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.GradeConfigMapper;
import com.example.Qlyhocsinh.repository.GradeConfigRepository;
import com.example.Qlyhocsinh.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeConfigService {

    private final GradeConfigRepository gradeConfigRepository;
    private final SubjectRepository subjectRepository;
    private final GradeConfigMapper gradeConfigMapper;

    // create 1 gradeConfig
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public GradeConfigResponse createGradeConfig(GradeConfigRequest request, String subjectId){
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        GradeConfig gradeConfig = gradeConfigMapper.toGradeConfig(request);
        gradeConfig.setSubjectId(subjectId);

        GradeConfig saved = gradeConfigRepository.save(gradeConfig);

        GradeConfigResponse response = gradeConfigMapper.toGradeConfigResponse(saved);
        response.setSubjectName(subject.getSubjectName());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public GradeConfigResponse updateGradeConfig(GradeConfigRequest request, Long gradeConfigId){
        GradeConfig gradeConfig = gradeConfigRepository.findById(gradeConfigId)
                .orElseThrow(()->new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND));
        Subject subject = subjectRepository.findById(gradeConfig.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        gradeConfigMapper.updateGradeConfig(request, gradeConfig);

        GradeConfig saved = gradeConfigRepository.save(gradeConfig);

        GradeConfigResponse response = gradeConfigMapper.toGradeConfigResponse(saved);
        response.setSubjectName(subject.getSubjectName());
        return response;

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGradeConfig(Long gradeConfigId){
        gradeConfigRepository.deleteById(gradeConfigId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public GradeConfigBulkResponse createBulkGradeConfig(GradeConfigBulkRequest bulkRequest, String subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject Not found"));

        // Kiểm tra trùng trong DB
        List<GradeConfig> existing = gradeConfigRepository.findBySubjectId(subjectId);
        Set<String> existingKeys = existing.stream()
                .map(gc -> gc.getSemester() + "_" + gc.getScoreType() + "_" + gc.getAcademicYear())
                .collect(Collectors.toSet());

        // Kiểm tra trùng trong chính request (ví dụ gửi 2 thuong_xuyen kỳ 1)
        Set<String> requestKeys = new HashSet<>();
        List<String> duplicatesInRequest = bulkRequest.getConfigs().stream()
                .map(r -> r.getSemester() + "_" + r.getScoreType() + "_" + r.getAcademicYear())
                .filter(key -> !requestKeys.add(key))
                .toList();

        if (!duplicatesInRequest.isEmpty()) {
            throw new RuntimeException("Trùng trong request: " + String.join(", ", duplicatesInRequest));
        }

        // Kiểm tra trùng với DB
        List<String> duplicatesWithDb = bulkRequest.getConfigs().stream()
                .map(r -> r.getSemester() + "_" + r.getScoreType() + "_" + r.getAcademicYear())
                .filter(existingKeys::contains)
                .toList();

        if (!duplicatesWithDb.isEmpty()) {
            throw new RuntimeException("Đã tồn tại trong DB: " + String.join(", ", duplicatesWithDb));
        }

        List<GradeConfig> configs = bulkRequest.getConfigs().stream()
                .map(r -> {
                    GradeConfig gc = gradeConfigMapper.toGradeConfig(r);
                    gc.setSubjectId(subjectId);
                    return gc;
                })
                .toList();

        return GradeConfigBulkResponse.builder()
                .subjectId(subjectId)
                .subjectName(subject.getSubjectName())
                .configs(gradeConfigRepository.saveAll(configs).stream()
                        .map(gradeConfigMapper::toGradeConfigResponse)
                        .toList())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Transactional
    public GradeConfigBulkResponse getGradeConfigFor1Sub(String subjectId){
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        List<GradeConfig> configs = gradeConfigRepository.findBySubjectId(subjectId);

        return GradeConfigBulkResponse.builder()
                .subjectId(subjectId)
                .subjectName(subject.getSubjectName())
                .configs(configs.stream().map(gradeConfigMapper::toGradeConfigResponse).toList())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public GradeConfigBulkResponse getGradeConfigBySubjectIdAndAcademicYearAndSemester(String subjectId, int academicYear, Integer semester){
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        List<GradeConfig> configs = gradeConfigRepository.findBySubjectIdAndAcademicYearAndSemester(subjectId, academicYear, semester);

        return GradeConfigBulkResponse.builder()
                .subjectId(subjectId)
                .subjectName(subject.getSubjectName())
                .configs(configs.stream().map(gradeConfigMapper::toGradeConfigResponse).toList())
                .build();
    }
}

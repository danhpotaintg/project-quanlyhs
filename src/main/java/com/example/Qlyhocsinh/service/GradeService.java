package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.projection.GradeRawRow;
import com.example.Qlyhocsinh.dto.request.GradeBatchRequest;
import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.ClassGradeSheetResponse;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.dto.response.StudentGradeResponse;
import com.example.Qlyhocsinh.entity.Grade;
import com.example.Qlyhocsinh.entity.GradeConfig;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.GradeMapper;
import com.example.Qlyhocsinh.repository.GradeConfigRepository;
import com.example.Qlyhocsinh.repository.GradeRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;
    private final GradeConfigRepository gradeConfigRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    //  nhập 1 điểm cho 1 học sinh (tạo và sửa điểm cho 1)
    @PreAuthorize("hasRole('TEACHER')")
    @Transactional
    public GradeResponse saveGrade(GradeRequest request,Integer entryIndex, String teacherId, Long gradeConfigId, String studentId){
        GradeConfig gradeConfig = gradeConfigRepository.findById(gradeConfigId)
                .orElseThrow(() -> new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND));

        validateEntryIndex(entryIndex, gradeConfig.getMaxEntries());

        Grade grade = gradeRepository.findByStudentIdAndGradeConfigIdAndEntryIndex(studentId, gradeConfigId, entryIndex)
                .orElse(new Grade());

        if(grade.getId() == null){
            grade.setStudentId(studentId);
            grade.setTeacherId(teacherId);
            grade.setGradeConfigId(gradeConfigId);
        }

        grade.setScore(request.getScore());
        grade.setEntryIndex(entryIndex);

        return gradeMapper.toResponse(gradeRepository.save(grade));
    }

    // nhập nhiều đầu điểm cho 1 student
    @PreAuthorize("hasRole('TEACHER')")
    @Transactional
    public List<GradeResponse> saveBatch(String studentId, List<GradeBatchRequest.GradeEntry> entries, String teacherId){

        List<GradeResponse> result = new ArrayList<>();

        for(GradeBatchRequest.GradeEntry entry : entries ){

            GradeConfig gradeConfig = gradeConfigRepository.findById(entry.getGradeConfigId())
                    .orElseThrow(()-> new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND));

            validateEntryIndex(entry.getEntryIndex(), gradeConfig.getMaxEntries());

            Grade grade = gradeRepository.findByStudentIdAndGradeConfigIdAndEntryIndex(studentId, gradeConfig.getId(), entry.getEntryIndex())
                    .orElseGet(() -> Grade.builder()
                            .studentId(studentId)
                            .teacherId(teacherId)
                            .gradeConfigId(gradeConfig.getId())
                            .entryIndex(entry.getEntryIndex())
                            .build());

            grade.setScore(entry.getScore());

            GradeResponse gradeResponse = gradeMapper.toResponse(gradeRepository.save(grade));

            result.add(gradeResponse);
        }
        return result;
    }

    // lấy điểm học sinh 1 lớp theo môn và kì học
    @PreAuthorize("hasRole('TEACHER')")
    public ClassGradeSheetResponse getGradeSheet(Long classId, String subjectId,
                                                 Integer semester, String teacherId) {

        List<GradeRawRow> rawRows = gradeRepository.findGradeSheet(classId, subjectId, semester);

        if (rawRows.isEmpty()) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        // 2. Build danh sách GradeConfig (không trùng)

        Map<Long, ClassGradeSheetResponse.GradeConfigDto> configMap = new LinkedHashMap<>();

        for (GradeRawRow row : rawRows) {
            Long configId = row.getGradeConfigId();

            if (!configMap.containsKey(configId)) {
                ClassGradeSheetResponse.GradeConfigDto dto =
                        ClassGradeSheetResponse.GradeConfigDto.builder()
                                .id(row.getGradeConfigId())
                                .scoreType(row.getGradeConfigName())
                                .maxEntries(row.getMaxEntries())
                                .weight(row.getWeight())
                                .subjectId(subjectId)
                                .semester(row.getSemester())
                                .build();

                configMap.put(configId, dto);
            }
        }

        List<ClassGradeSheetResponse.GradeConfigDto> configDtos =
                new ArrayList<>(configMap.values());

        // 3. Group dữ liệu theo student

        Map<String, List<GradeRawRow>> groupedByStudent = new LinkedHashMap<>();

        for (GradeRawRow row : rawRows) {
            String studentId = row.getStudentId();

            if (!groupedByStudent.containsKey(studentId)) {
                groupedByStudent.put(studentId, new ArrayList<>());
            }

            groupedByStudent.get(studentId).add(row);
        }

        // 4. Build danh sách student rows

        List<ClassGradeSheetResponse.StudentGradeRow> studentRows = new ArrayList<>();

        for (Map.Entry<String, List<GradeRawRow>> entry : groupedByStudent.entrySet()) {

            String studentId = entry.getKey();
            List<GradeRawRow> studentData = entry.getValue();

            // Map điểm: key = configId_entryIndex
            Map<String, Double> scores = new HashMap<>();

            for (GradeRawRow row : studentData) {
                if (row.getScore() != null) {
                    String key = row.getGradeConfigId() + "_" + row.getEntryIndex();
                    scores.put(key, row.getScore());
                }
            }

            ClassGradeSheetResponse.StudentGradeRow studentRow =
                    ClassGradeSheetResponse.StudentGradeRow.builder()
                            .studentId(studentId)
                            .studentName(studentData.get(0).getStudentName())
                            .scores(scores)
                            .build();

            studentRows.add(studentRow);
        }

        return ClassGradeSheetResponse.builder()
                .gradeConfigs(configDtos)
                .students(studentRows)
                .build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public StudentGradeResponse getGradesBySubject(String studentId, String subjectId, Integer semester){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        List<GradeRawRow> grades = gradeRepository.findStudentGradeBySubject(studentId, subjectId, semester);

        if(grades.isEmpty()) throw new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND);

        Map<Long, List<GradeRawRow>> grouped = new LinkedHashMap<>();

        for (GradeRawRow row : grades){
            Long configId = row.getGradeConfigId();

            if (!grouped.containsKey(configId)) {
                grouped.put(configId, new ArrayList<>());
            }
            grouped.get(configId).add(row);
        }

        List<StudentGradeResponse.GradeConfigDetail> gradeConfigs = new ArrayList<>();

        for (Map.Entry<Long, List<GradeRawRow>> entry : grouped.entrySet()) {

            Long gradeConfigId = entry.getKey();
            List<GradeRawRow> rows = entry.getValue();

            // Sắp xếp theo entryIndex (lần nhập điểm)
            rows.sort(Comparator.comparing(GradeRawRow::getEntryIndex));

            // Lấy danh sách điểm
            List<Double> scores = new ArrayList<>();
            for (GradeRawRow row : rows) {
                scores.add(row.getScore()); // có thể null
            }

            // Lấy thông tin config từ row đầu tiên
            GradeRawRow firstRow = rows.get(0);

            StudentGradeResponse.GradeConfigDetail detail =
                    StudentGradeResponse.GradeConfigDetail.builder()
                            .gradeConfigId(gradeConfigId)
                            .scoreType(firstRow.getGradeConfigName())
                            .weight(firstRow.getWeight())
                            .maxEntries(firstRow.getMaxEntries())
                            .scores(scores)
                            .build();

            gradeConfigs.add(detail);
        }

        return StudentGradeResponse.builder()
                .subjectId(subjectId)
                .subjectName(subject.getSubjectName())
                .semester(semester)
                .gradeConfigs(gradeConfigs)
                .build();
    }


    private void validateEntryIndex(Integer entryIndex, Integer maxEntries) {
        if (entryIndex < 1 || entryIndex > maxEntries) {
            throw new AppException(ErrorCode.GRADE_ENTRY_INDEX_INVALID);
        }
    }

}

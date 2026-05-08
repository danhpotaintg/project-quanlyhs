package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.projection.GradeRawRow;
import com.example.Qlyhocsinh.dto.request.GradeBatchRequest;
import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.ClassGradeSheetResponse;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.dto.response.StudentAllGradeResponse;
import com.example.Qlyhocsinh.dto.response.StudentGradeResponse;
import com.example.Qlyhocsinh.entity.Grade;
import com.example.Qlyhocsinh.entity.GradeConfig;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.GradeMapper;
import com.example.Qlyhocsinh.repository.*;
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
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClassRepository classRepository;

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

        List<Long> configIds = entries.stream()
                .map(GradeBatchRequest.GradeEntry::getGradeConfigId)
                .distinct()
                .toList();

        //Lấy GradeConfig 1 lần
        Map<Long, GradeConfig> configMap = gradeConfigRepository.findAllById(configIds)
                .stream()
                .collect(Collectors.toMap(GradeConfig::getId, gc -> gc));

        //Lấy toàn bộ Grade hiện có của student
        List<Grade> existingGrades = gradeRepository.findByStudentIdAndGradeConfigIdIn(studentId, configIds);

        // Map key = (configId + entryIndex)
        Map<String, Grade> gradeMap = existingGrades.stream()
                .collect(Collectors.toMap(
                        g -> g.getGradeConfigId() + "_" + g.getEntryIndex(),
                        g -> g
                ));

        List<Grade> toSave = new ArrayList<>();

        for (GradeBatchRequest.GradeEntry entry : entries) {

            GradeConfig gradeConfig = configMap.get(entry.getGradeConfigId());
            if (gradeConfig == null) {
                throw new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND);
            }

            validateEntryIndex(entry.getEntryIndex(), gradeConfig.getMaxEntries());

            String key = entry.getGradeConfigId() + "_" + entry.getEntryIndex();

            Grade grade = gradeMap.getOrDefault(key,
                    Grade.builder()
                            .studentId(studentId)
                            .teacherId(teacherId)
                            .gradeConfigId(entry.getGradeConfigId())
                            .entryIndex(entry.getEntryIndex())
                            .build()
            );

            grade.setScore(entry.getScore());
            toSave.add(grade);
        }

        return gradeRepository.saveAll(toSave).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    // lấy điểm học sinh 1 lớp theo môn, năm học và kì học
    //@PreAuthorize("hasRole('TEACHER')")
    public ClassGradeSheetResponse getGradeSheet(Long classId, String subjectId,
                                                 Integer semester, int academicYear) {

        List<GradeRawRow> rawRows = gradeRepository.findGradeSheet(classId, subjectId, semester, academicYear);

        if (rawRows.isEmpty()) throw new AppException(ErrorCode.STUDENT_NOT_FOUND);

        // Build gradeConfigs
        Map<Long, ClassGradeSheetResponse.GradeConfigDto> configMap = new LinkedHashMap<>();
        for (GradeRawRow row : rawRows) {
            Long configId = row.getGradeConfigId();
            if (!configMap.containsKey(configId)) {
                configMap.put(configId, ClassGradeSheetResponse.GradeConfigDto.builder()
                        .id(row.getGradeConfigId())
                        .scoreType(row.getGradeConfigName())
                        .maxEntries(row.getMaxEntries())
                        .weight(row.getWeight())
                        .subjectId(subjectId)
                        .semester(row.getSemester())
                        .build());
            }
        }
        List<ClassGradeSheetResponse.GradeConfigDto> configDtos = new ArrayList<>(configMap.values());

        // Group theo student
        Map<String, List<GradeRawRow>> groupedByStudent = new LinkedHashMap<>();
        for (GradeRawRow row : rawRows) {
            String studentId = row.getStudentId();
            if (!groupedByStudent.containsKey(studentId)) {
                groupedByStudent.put(studentId, new ArrayList<>());
            }
            groupedByStudent.get(studentId).add(row);
        }

        // Build student rows
        List<ClassGradeSheetResponse.StudentGradeRow> studentRows = new ArrayList<>();

        for (Map.Entry<String, List<GradeRawRow>> entry : groupedByStudent.entrySet()) {
            String studentId = entry.getKey();
            List<GradeRawRow> studentData = entry.getValue();

            // Build scores map
            Map<String, Double> scores = new HashMap<>();
            for (GradeRawRow row : studentData) {
                if (row.getScore() != null) {
                    String key = row.getGradeConfigId() + "_" + row.getEntryIndex();
                    scores.put(key, row.getScore());
                }
            }

            // Tính TB kì - group theo scoreType
            Map<String, List<GradeRawRow>> groupedByType = new LinkedHashMap<>();
            for (GradeRawRow row : studentData) {
                String scoreType = row.getGradeConfigName();
                if (!groupedByType.containsKey(scoreType)) {
                    groupedByType.put(scoreType, new ArrayList<>());
                }
                groupedByType.get(scoreType).add(row);
            }

            double totalWeightedScore = 0;
            double totalWeight = 0;
            boolean canCalculate = true;

            for (Map.Entry<String, List<GradeRawRow>> typeEntry : groupedByType.entrySet()) {
                List<GradeRawRow> typeRows = typeEntry.getValue();

                List<Double> validScores = new ArrayList<>();
                for (GradeRawRow row : typeRows) {
                    if (row.getScore() != null) validScores.add(row.getScore());
                }

                if (validScores.isEmpty()) {
                    canCalculate = false;
                    break;
                }

                double avg = 0;
                for (Double s : validScores) avg += s;
                avg = avg / validScores.size();

                double weight = typeRows.get(0).getWeight();
                totalWeightedScore += avg * weight;
                totalWeight += weight;
            }

            Double semesterAverage = null;
            if (canCalculate && totalWeight > 0) {
                semesterAverage = Math.round((totalWeightedScore / totalWeight) * 100.0) / 100.0;
            }

            studentRows.add(ClassGradeSheetResponse.StudentGradeRow.builder()
                    .studentId(studentId)
                    .studentName(studentData.get(0).getStudentName())
                    .scores(scores)
                    .semesterAverage(semesterAverage)
                    .build());
        }

        return ClassGradeSheetResponse.builder()
                .gradeConfigs(configDtos)
                .className(classRepository.findById(classId).get().getClassName())
                .subjectName(subjectRepository.findById(subjectId).get().getSubjectName())
                //.teacherName(teacherRepository.findById(teac))
                .semester(semester)
                .academicYear(academicYear)
                .students(studentRows)
                .build();
    }

    //học sinh xem điểm môn học của mình theo môn, năm học và học kì
    @PreAuthorize("hasRole('STUDENT')")
    public StudentGradeResponse getGradesBySubject(String studentId, String subjectId, Integer semester, int academicYear) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        List<GradeRawRow> grades = gradeRepository.findStudentGradeBySubject(studentId, subjectId, semester, academicYear);

        if (grades.isEmpty()) throw new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND);

        Map<Long, List<GradeRawRow>> grouped = new LinkedHashMap<>();
        for (GradeRawRow row : grades) {
            Long configId = row.getGradeConfigId();
            if (!grouped.containsKey(configId)) {
                grouped.put(configId, new ArrayList<>());
            }
            grouped.get(configId).add(row);
        }

        List<StudentGradeResponse.GradeConfigDetail> gradeConfigs = new ArrayList<>();

        double totalWeightedScore = 0;
        double totalWeight = 0;
        boolean canCalculate = true;

        for (Map.Entry<Long, List<GradeRawRow>> entry : grouped.entrySet()) {
            Long gradeConfigId = entry.getKey();
            List<GradeRawRow> rows = entry.getValue();

            rows.sort(Comparator.comparing(GradeRawRow::getEntryIndex));

            List<Double> scores = new ArrayList<>();
            for (GradeRawRow row : rows) {
                scores.add(row.getScore());
            }

            GradeRawRow firstRow = rows.get(0);

            gradeConfigs.add(StudentGradeResponse.GradeConfigDetail.builder()
                    .gradeConfigId(gradeConfigId)
                    .scoreType(firstRow.getGradeConfigName())
                    .weight(firstRow.getWeight())
                    .maxEntries(firstRow.getMaxEntries())
                    .scores(scores)
                    .build());

            // Tính TB theo từng scoreType
            List<Double> validScores = new ArrayList<>();
            for (Double s : scores) {
                if (s != null) validScores.add(s);
            }

            if (validScores.isEmpty()) {
                // Còn thiếu điểm → không tính được TB
                canCalculate = false;
            } else {
                double avg = 0;
                for (Double s : validScores) avg += s;
                avg = avg / validScores.size();

                totalWeightedScore += avg * firstRow.getWeight();
                totalWeight += firstRow.getWeight();
            }
        }

        Double semesterAverage = null;
        if (canCalculate && totalWeight > 0) {
            semesterAverage = Math.round((totalWeightedScore / totalWeight) * 100.0) / 100.0;
        }

        return StudentGradeResponse.builder()
                .subjectId(subjectId)
                .subjectName(subject.getSubjectName())
                .semester(semester)
                .academicYear(academicYear)
                .semesterAverage(semesterAverage)
                .gradeConfigs(gradeConfigs)
                .build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    public StudentAllGradeResponse getAllSubjectsGrade(
            String studentId, Integer semester, int academicYear) {

        studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        List<GradeRawRow> rows = gradeRepository
                .findAllSubjectsGradeByStudent(studentId, semester, academicYear);

        if (rows.isEmpty()) throw new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND);

        String studentName = rows.get(0).getStudentName();

        // Group theo subjectId
        Map<String, List<GradeRawRow>> bySubject = new LinkedHashMap<>();
        for (GradeRawRow row : rows) {
            bySubject.computeIfAbsent(row.getSubjectId(), k -> new ArrayList<>()).add(row);
        }

        List<StudentAllGradeResponse.SubjectGradeSummary> subjects = new ArrayList<>();
        List<Double> allSubjectAverages = new ArrayList<>(); // để tính GPA

        for (Map.Entry<String, List<GradeRawRow>> subjectEntry : bySubject.entrySet()) {
            List<GradeRawRow> subjectRows = subjectEntry.getValue();
            GradeRawRow firstSubjectRow = subjectRows.get(0);

            // Group theo gradeConfigId
            Map<Long, List<GradeRawRow>> byConfig = new LinkedHashMap<>();
            for (GradeRawRow row : subjectRows) {
                byConfig.computeIfAbsent(row.getGradeConfigId(), k -> new ArrayList<>()).add(row);
            }

            List<StudentAllGradeResponse.GradeConfigDetail> configDetails = new ArrayList<>();
            double totalWeightedScore = 0;
            double totalWeight = 0;
            boolean canCalculate = true;

            for (Map.Entry<Long, List<GradeRawRow>> configEntry : byConfig.entrySet()) {
                List<GradeRawRow> configRows = configEntry.getValue();
                configRows.sort(Comparator.comparing(GradeRawRow::getEntryIndex));

                GradeRawRow firstConfigRow = configRows.get(0);

                List<Double> scores = configRows.stream()
                        .map(GradeRawRow::getScore)
                        .collect(Collectors.toList());

                configDetails.add(StudentAllGradeResponse.GradeConfigDetail.builder()
                        .gradeConfigId(configEntry.getKey())
                        .scoreType(firstConfigRow.getGradeConfigName())
                        .weight(firstConfigRow.getWeight())
                        .maxEntries(firstConfigRow.getMaxEntries())
                        .scores(scores)
                        .build());

                List<Double> validScores = scores.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (validScores.isEmpty()) {
                    canCalculate = false;
                } else {
                    double avg = validScores.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0);
                    totalWeightedScore += avg * firstConfigRow.getWeight();
                    totalWeight += firstConfigRow.getWeight();
                }
            }

            Double semesterAverage = null;
            if (canCalculate && totalWeight > 0) {
                semesterAverage = Math.round((totalWeightedScore / totalWeight) * 100.0) / 100.0;
                allSubjectAverages.add(semesterAverage);
            }

            subjects.add(StudentAllGradeResponse.SubjectGradeSummary.builder()
                    .subjectId(subjectEntry.getKey())
                    .subjectName(firstSubjectRow.getSubjectName())
                    .gradeConfigs(configDetails)
                    .semesterAverage(semesterAverage)
                    .build());
        }

        // Tính GPA (TB tất cả môn)
        Double semesterGPA = null;
        String academicRank = null;
        String title = null;

        if (allSubjectAverages.size() == bySubject.size()) {
            // Chỉ tính khi tất cả môn đều có TBM
            semesterGPA = Math.round(
                    allSubjectAverages.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0) * 100.0) / 100.0;

            academicRank = calculateAcademicRank(semesterGPA);
            title = calculateTitle(semesterGPA, academicRank);
        }

        return StudentAllGradeResponse.builder()
                .studentId(studentId)
                .studentName(studentName)
                .semester(semester)
                .academicYear(academicYear)
                .semesterGPA(semesterGPA)
                .academicRank(academicRank)
                .title(title)
                .subjects(subjects)
                .build();
    }

    private String calculateAcademicRank(Double gpa) {
        if (gpa == null) return null;
        if (gpa >= 8.0) return "Giỏi";
        if (gpa >= 6.5) return "Khá";
        if (gpa >= 5.0) return "Trung bình";
        if (gpa >= 3.5) return "Yếu";
        return "Kém";
    }

    private String calculateTitle(Double gpa, String academicRank) {
        if (gpa == null || academicRank == null) return null;
        if (gpa >= 9.0 && "Giỏi".equals(academicRank)) return "Học sinh xuất sắc";
        if (gpa >= 8.0 && "Giỏi".equals(academicRank)) return "Học sinh giỏi";
        if ("Khá".equals(academicRank)) return "Học sinh tiên tiến";
        return null; // TB, Yếu, Kém không có danh hiệu
    }

    private void validateEntryIndex(Integer entryIndex, Integer maxEntries) {
        if (entryIndex < 1 || entryIndex > maxEntries) {
            throw new AppException(ErrorCode.GRADE_ENTRY_INDEX_INVALID);
        }
    }

}

package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.projection.GradeRawRow;
import com.example.Qlyhocsinh.dto.response.SubjectAdviceResponse;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.repository.GradeRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.StudyAdviceRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyAdviceService {

    private final StudyAdviceRuleRepository studyAdviceRuleRepository;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;

    @PreAuthorize("hasRole('STUDENT')")
    public List<SubjectAdviceResponse> getAdvicesForStudent(
            String studentId, Integer semester, int academicYear) {

        studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        List<GradeRawRow> rows = gradeRepository
                .findAllSubjectsGradeByStudent(studentId, semester, academicYear);

        if (rows.isEmpty()) throw new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND);

        // Group theo subjectId
        Map<String, List<GradeRawRow>> bySubject = new LinkedHashMap<>();
        for (GradeRawRow row : rows) {
            bySubject.computeIfAbsent(row.getSubjectId(), k -> new ArrayList<>()).add(row);
        }

        List<SubjectAdviceResponse> result = new ArrayList<>();

        for (Map.Entry<String, List<GradeRawRow>> entry : bySubject.entrySet()) {
            String subjectId = entry.getKey();
            List<GradeRawRow> subjectRows = entry.getValue();
            GradeRawRow firstRow = subjectRows.get(0);

            Double tbm = calculateTBM(subjectRows);

            result.add(SubjectAdviceResponse.builder()
                    .subjectId(subjectId)
                    .subjectName(firstRow.getSubjectName())
                    .semesterAverage(tbm)
                    .advice(tbm != null
                            ? resolveAdvice(subjectId, firstRow.getSubjectName(), tbm)
                            : null)
                    .build());
        }

        return result;
    }

    public Double calculateTBM(List<GradeRawRow> subjectRows) {
        Map<Long, List<GradeRawRow>> byConfig = new LinkedHashMap<>();
        for (GradeRawRow row : subjectRows) {
            byConfig.computeIfAbsent(row.getGradeConfigId(), k -> new ArrayList<>()).add(row);
        }

        double totalWeightedScore = 0;
        double totalWeight = 0;
        boolean canCalculate = true;

        for (List<GradeRawRow> configRows : byConfig.values()) {
            List<Double> validScores = configRows.stream()
                    .map(GradeRawRow::getScore)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (validScores.isEmpty()) {
                canCalculate = false;
                break;
            }

            double avg = validScores.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);

            totalWeightedScore += avg * configRows.get(0).getWeight();
            totalWeight += configRows.get(0).getWeight();
        }

        if (!canCalculate || totalWeight == 0) return null;
        return Math.round((totalWeightedScore / totalWeight) * 100.0) / 100.0;
    }

    private String resolveAdvice(String subjectId, String subjectName, Double score) {
        return studyAdviceRuleRepository
                .findBestMatchRule(subjectId, score)
                .map(rule -> rule.getAdvice()
                        .replace("{subject}", subjectName)
                        .replace("{score}", String.valueOf(score)))
                .orElse(null);
    }
}

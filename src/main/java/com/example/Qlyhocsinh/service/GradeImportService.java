package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.response.GradeImportResponse;
import com.example.Qlyhocsinh.entity.Grade;
import com.example.Qlyhocsinh.entity.GradeConfig;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.repository.GradeConfigRepository;
import com.example.Qlyhocsinh.repository.GradeRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GradeImportService {

    private final GradeRepository gradeRepository;
    private final GradeConfigRepository gradeConfigRepository;
    private final StudentRepository studentRepository;

    @Transactional
    @PreAuthorize("hasRole('TEACHER')")
    public List<GradeImportResponse> importFromExcel(Long classId, String subjectId,
                                                     Integer semester, int academicYear,
                                                     MultipartFile file, String teacherId) {
        // 1. Validate file
        if (file.isEmpty()) throw new AppException(ErrorCode.FILE_EMPTY);
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }

        // 2. Load gradeConfig 1 lần
        List<GradeConfig> gradeConfigs = gradeConfigRepository
                .findBySubjectIdAndAcademicYearAndSemester(subjectId, academicYear, semester);
        if (gradeConfigs.isEmpty()) throw new AppException(ErrorCode.GRADE_CONFIG_NOT_FOUND);

        Map<String, GradeConfig> configByType = new LinkedHashMap<>();
        for (GradeConfig gc : gradeConfigs) {
            configByType.put(gc.getScoreType(), gc);
        }

        List<GradeImportResponse> results = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 3. Parse header bắt đầu từ cột 2 (cột 0: studentId, cột 1: tên)
            Row headerRow = sheet.getRow(0);
            Map<Integer, long[]> columnMap = parseHeader(headerRow, configByType);

            // 4. Đọc từng dòng học sinh bắt đầu từ row 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Cột 0: studentId
                Cell idCell = row.getCell(0);
                if (idCell == null || idCell.getStringCellValue().isBlank()) continue;

                String studentId = idCell.getStringCellValue().trim();

                // Cột 1: tên - chỉ dùng để hiển thị trong response
                String studentName = (row.getCell(1) != null && !row.getCell(1).getStringCellValue().isBlank())
                        ? row.getCell(1).getStringCellValue().trim()
                        : studentId;

                GradeImportResponse result = processRow(
                        row, studentId, studentName, columnMap, classId, teacherId, i);
                results.add(result);
            }

        } catch (IOException e) {
            log.error("Lỗi đọc file Excel", e);
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }

        return results;
    }

    private Map<Integer, long[]> parseHeader(Row headerRow, Map<String, GradeConfig> configByType) {
        Map<Integer, long[]> columnMap = new LinkedHashMap<>();
        Map<String, Integer> entryCounter = new HashMap<>();

        // Bắt đầu từ cột 2 vì cột 0 là studentId, cột 1 là tên
        for (int col = 2; col < headerRow.getLastCellNum(); col++) {
            Cell cell = headerRow.getCell(col);
            if (cell == null) continue;

            String header = cell.getStringCellValue().trim().toLowerCase();
            String scoreType = mapHeaderToScoreType(header);
            if (scoreType == null) continue;

            GradeConfig gc = configByType.get(scoreType);
            if (gc == null) continue;

            int entryIndex = entryCounter.getOrDefault(scoreType, 0) + 1;
            entryCounter.put(scoreType, entryIndex);

            // [gradeConfigId, entryIndex]
            columnMap.put(col, new long[]{gc.getId(), entryIndex});
        }

        return columnMap;
    }

    private GradeImportResponse processRow(Row row, String studentId, String studentName,
                                           Map<Integer, long[]> columnMap,
                                           Long classId, String teacherId, int rowIndex) {
        try {
            // Validate student có thuộc lớp không
            Student student = studentRepository.findByUserIdAndClassRoomId(studentId, classId)
                    .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

            // Load existing grades của student 1 lần
            List<Long> configIds = new ArrayList<>();
            for (long[] val : columnMap.values()) {
                configIds.add(val[0]);
            }

            List<Grade> existingGrades = gradeRepository
                    .findByStudentIdAndGradeConfigIdIn(student.getId(), configIds);

            Map<String, Grade> gradeMap = new LinkedHashMap<>();
            for (Grade g : existingGrades) {
                gradeMap.put(g.getGradeConfigId() + "_" + g.getEntryIndex(), g);
            }

            List<Grade> toSave = new ArrayList<>();

            for (Map.Entry<Integer, long[]> entry : columnMap.entrySet()) {
                int colIndex = entry.getKey();
                long gradeConfigId = entry.getValue()[0];
                int entryIndex = (int) entry.getValue()[1];

                Cell cell = row.getCell(colIndex);
                if (cell == null || cell.getCellType() == CellType.BLANK) continue;

                double score = cell.getNumericCellValue();
                if (score < 0 || score > 10) {
                    log.warn("Row {}: điểm {} không hợp lệ, bỏ qua", rowIndex, score);
                    continue;
                }

                String key = gradeConfigId + "_" + entryIndex;
                Grade grade = gradeMap.getOrDefault(key,
                        Grade.builder()
                                .studentId(student.getId())
                                .teacherId(teacherId)
                                .gradeConfigId(gradeConfigId)
                                .entryIndex(entryIndex)
                                .build()
                );

                grade.setScore(score);
                toSave.add(grade);
            }

            if (!toSave.isEmpty()) {
                gradeRepository.saveAll(toSave);
            }

            return GradeImportResponse.builder()
                    .rowIndex(rowIndex)
                    .studentName(studentName)
                    .success(true)
                    .message("Nhập điểm thành công")
                    .build();

        } catch (Exception e) {
            return GradeImportResponse.builder()
                    .rowIndex(rowIndex)
                    .studentName(studentName)
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    private String mapHeaderToScoreType(String header) {
        if (header.contains("15p") || header.contains("thường xuyên")) return "thuong_xuyen";
        if (header.contains("giữa kì") || header.contains("giữa kỳ"))  return "giua_ky";
        if (header.contains("cuối kì") || header.contains("cuối kỳ"))  return "cuoi_ky";
        return null;
    }
}
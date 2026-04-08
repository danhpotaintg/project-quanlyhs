package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.response.TeacherPreviewCreationResponse;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.repository.SubjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherImportService {

    TeacherService teacherService;
    SubjectRepository subjectRepository;

    public List<TeacherPreviewCreationResponse> previewData(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new AppException(ErrorCode.FILE_EMPTY);

        List<TeacherPreviewCreationResponse> previewList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua tiêu đề

                String fullName = getStringValue(row.getCell(0));
                LocalDate dob = getDateValue(row.getCell(1));
                String gender = getStringValue(row.getCell(2));
                String email = getStringValue(row.getCell(3));
                String phoneNumber = getStringValue(row.getCell(4));
                String subjectName = getStringValue(row.getCell(5));

                // Bỏ qua dòng trống
                if (fullName.isEmpty() && dob == null && email.isEmpty()) continue;

                boolean isValid = true;
                StringBuilder errorMsg = new StringBuilder();

                // Kiểm tra TẤT CẢ các trường (Thiếu là Đỏ)
                if (fullName.isEmpty()) { isValid = false; errorMsg.append("Thiếu Họ Tên. "); }
                if (dob == null) { isValid = false; errorMsg.append("Thiếu Ngày sinh. "); }
                if (gender.isEmpty()) { isValid = false; errorMsg.append("Thiếu Giới tính. "); }
                if (email.isEmpty()) { isValid = false; errorMsg.append("Thiếu Email. "); }
                if (phoneNumber.isEmpty()) { isValid = false; errorMsg.append("Thiếu SĐT. "); }

                // Kiểm tra Môn học có tồn tại trong DB không
                if (subjectName.isEmpty()) {
                    isValid = false;
                    errorMsg.append("Thiếu Môn học. ");
                } else {
                    Optional<Subject> subjectOpt = subjectRepository.findBySubjectName(subjectName);
                    if (subjectOpt.isEmpty()) {
                        isValid = false;
                        errorMsg.append("Môn '").append(subjectName).append("' không tồn tại. ");
                    }
                }

                TeacherPreviewCreationResponse previewResponse = TeacherPreviewCreationResponse.builder()
                        .fullName(fullName)
                        .dob(dob)
                        .gender(gender)
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .subjectName(subjectName)
                        .valid(isValid)
                        .errorNote(errorMsg.toString().trim())
                        .build();

                previewList.add(previewResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }

        return previewList;
    }

    public String confirmImport(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String fullName = getStringValue(row.getCell(0));
                LocalDate dob = getDateValue(row.getCell(1));
                String gender = getStringValue(row.getCell(2));
                String email = getStringValue(row.getCell(3));
                String phoneNumber = getStringValue(row.getCell(4));
                String subjectName = getStringValue(row.getCell(5));

                if (fullName.isEmpty() && dob == null && email.isEmpty()) continue;

                // Lọc điều kiện: Phải đầy đủ thông tin và Môn học phải tồn tại mới được tạo
                boolean isValid = !fullName.isEmpty() && dob != null && !gender.isEmpty()
                        && !email.isEmpty() && !phoneNumber.isEmpty() && !subjectName.isEmpty();

                if (isValid && subjectRepository.findBySubjectName(subjectName).isPresent()) {

                    teacherService.createTeacher(TeacherCreationRequest.builder()
                            .fullName(fullName)
                            .dob(dob)
                            .gender(gender)
                            .email(email)
                            .phoneNumber(phoneNumber)
                            .subjectName(subjectName)
                            .build());
                }
            }
        }
        return "Import dữ liệu Giáo viên thành công!";
    }

    // --- Các hàm Helper đọc Cell (Copy y hệt bên Student) ---
    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.toString().trim();
    }

    private LocalDate getDateValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern("[dd/MM/yyyy]").appendPattern("[d/M/yyyy]")
                        .appendPattern("[dd-MM-yyyy]").appendPattern("[yyyy-MM-dd]").toFormatter();
                return LocalDate.parse(dateStr, formatter);
            }
        } catch (Exception e) { return null; }
        return null;
    }
}
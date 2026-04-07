package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.response.StudentPreviewCreationResponse;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentImportService {
    private final StudentService studentService;
    public List<StudentPreviewCreationResponse> previewData(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        List<StudentPreviewCreationResponse> previewList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                String fullName = getStringValue(row.getCell(0));
                LocalDate dob = getDateValue(row.getCell(1));
                String parentGmail = getStringValue(row.getCell(2));
                String parentPhonenumber = getStringValue(row.getCell(3));
                String gender = getStringValue(row.getCell(4));
                int academicYear = getIntValue(row.getCell(5));

                // Bỏ qua nếu dòng trống hoàn toàn
                if (fullName.isEmpty() && dob == null && academicYear == 0) {
                    continue;
                }

                boolean isValid = true;
                boolean hasWarning = false;
                StringBuilder errorMsg = new StringBuilder();
                StringBuilder warningMsg = new StringBuilder();

                // 1. Kiểm tra MÀU ĐỎ (Lỗi thiếu thông tin bắt buộc)
                if (fullName.isEmpty()) { isValid = false; errorMsg.append("Thiếu Họ Tên. "); }
                if (dob == null) { isValid = false; errorMsg.append("Thiếu Ngày sinh. "); }
                if (gender.isEmpty()) { isValid = false; errorMsg.append("Thiếu Giới tính. "); }
                if (academicYear == 0) { isValid = false; errorMsg.append("Thiếu Niên khóa. "); }

                // 2. Kiểm tra MÀU VÀNG (Cảnh báo thiếu thông tin phụ huynh)
                if (isValid) {
                    if (parentGmail.isEmpty()) {
                        hasWarning = true;
                        warningMsg.append("Chưa nhập Email phụ huynh. ");
                    }
                    if (parentPhonenumber.isEmpty()) {
                        hasWarning = true;
                        warningMsg.append("Chưa nhập SĐT phụ huynh. ");
                    }
                }

                StudentPreviewCreationResponse previewResponse = StudentPreviewCreationResponse.builder()
                        .fullName(fullName)
                        .dob(dob)
                        .parentGmail(parentGmail)
                        .parentPhonenumber(parentPhonenumber)
                        .gender(gender)
                        .academicYear(academicYear)
                        .valid(isValid)
                        .errorNote(errorMsg.toString().trim())
                        .hasWarning(hasWarning)
                        .warningNote(warningMsg.toString().trim())
                        .build();

                previewList.add(previewResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }

        return previewList;
    }



    public String confirmImport(MultipartFile file, String mode) throws Exception {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String fullName = getStringValue(row.getCell(0));
                LocalDate dob = getDateValue(row.getCell(1));
                String parentGmail = getStringValue(row.getCell(2));
                String parentPhonenumber = getStringValue(row.getCell(3));
                String gender = getStringValue(row.getCell(4));
                int academicYear = getIntValue(row.getCell(5));

                if (fullName.isEmpty() && dob == null && academicYear == 0) continue;

                boolean isRed = fullName.isEmpty() || dob == null || gender.isEmpty() || academicYear == 0;
                boolean isYellow = !isRed && (parentGmail.isEmpty() || parentPhonenumber.isEmpty());

                if (isRed) continue;
                if (mode.equals("STRICT") && isYellow) continue;

                // Gọi StudentService để lưu vào Database
                studentService.createStudent(StudentCreationRequest.builder()
                        .fullName(fullName)
                        .dob(dob)
                        .parentGmail(parentGmail)
                        .parentPhonenumber(parentPhonenumber)
                        .gender(gender)
                        .academicYear(academicYear)
                        .build());
            }
        }
        return "Import dữ liệu thành công!";
    }

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
            // Trường hợp 1: Excel nhận diện đúng là kiểu Ngày tháng (Date)
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }

            // Trường hợp 2: Excel lưu dưới dạng Chữ (Text) do copy/paste hoặc sai Locale
            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();

                // Tạo một bộ đọc ngày tháng linh hoạt (Hỗ trợ cả 15/08/2008 và 5/1/2008)
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern("[dd/MM/yyyy]")
                        .appendPattern("[d/M/yyyy]")
                        .appendPattern("[dd-MM-yyyy]")
                        .appendPattern("[yyyy-MM-dd]")
                        .toFormatter();

                return LocalDate.parse(dateStr, formatter);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private int getIntValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return 0;
        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            return 0;
        }
    }
}
package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.response.StudentPreviewCreationResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.repository.ClassRepository;
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
public class StudentImportService {

    StudentService studentService;
    ClassRepository classRepository;

    public List<StudentPreviewCreationResponse> previewData(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        List<StudentPreviewCreationResponse> previewList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua tiêu đề

                String fullName = getStringValue(row.getCell(0));
                LocalDate dob = getDateValue(row.getCell(1));
                String parentGmail = getStringValue(row.getCell(2));
                String parentPhonenumber = getStringValue(row.getCell(3));
                String gender = getStringValue(row.getCell(4));
                int academicYear = getIntValue(row.getCell(5));
                String className = getStringValue(row.getCell(6));

                if (fullName.isEmpty() && dob == null && academicYear == 0) continue;

                boolean isValid = true;
                boolean hasWarning = false;
                StringBuilder errorMsg = new StringBuilder();
                StringBuilder warningMsg = new StringBuilder();

                // Kiem tra dư lieu trong
                if (fullName.isEmpty()) { isValid = false; errorMsg.append("Thiếu Họ Tên. "); }
                if (dob == null) { isValid = false; errorMsg.append("Thiếu Ngày sinh. "); }
                if (gender.isEmpty()) { isValid = false; errorMsg.append("Thiếu Giới tính. "); }
                if (academicYear == 0) { isValid = false; errorMsg.append("Thiếu Niên khóa. "); }
                if (parentGmail.isEmpty()) { isValid = false; errorMsg.append("Thiếu Email PH. "); }
                if (parentPhonenumber.isEmpty()) { isValid = false; errorMsg.append("Thiếu SĐT PH. "); }

                // Kiểm tra logic LỚP HỌC
                if (!className.isEmpty()) {
                    Optional<ClassRoom> classRoomOpt = classRepository.findByClassName(className);
                    if (classRoomOpt.isEmpty()) {
                        isValid = false; //lop ko ton tai
                        errorMsg.append("Lớp '").append(className).append("' không tồn tại. ");
                    }
                } else {
                    hasWarning = true; // trong cot lop
                    warningMsg.append("Chưa xếp Lớp. ");
                }

                StudentPreviewCreationResponse previewResponse = StudentPreviewCreationResponse.builder()
                        .fullName(fullName)
                        .dob(dob)
                        .parentGmail(parentGmail)
                        .parentPhonenumber(parentPhonenumber)
                        .gender(gender)
                        .academicYear(academicYear)
                        .className(className)
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
                String className = getStringValue(row.getCell(6));

                if (fullName.isEmpty() && dob == null && academicYear == 0) continue;

                // Validate lại logic Đỏ (thêm parentGmail và parentPhonenumber vào danh sách bắt buộc)
                boolean isRed = fullName.isEmpty() || dob == null || gender.isEmpty() || academicYear == 0
                        || parentGmail.isEmpty() || parentPhonenumber.isEmpty();

                if (!className.isEmpty() && classRepository.findByClassName(className).isEmpty()) {
                    isRed = true; //lop ko ton tai
                }

                boolean isYellow = !isRed && className.isEmpty();

                if (isRed) continue;
                if (mode.equals("STRICT") && isYellow) continue;

                // Truyền tên lớp sang request để StudentService xử lý
                studentService.createStudent(StudentCreationRequest.builder()
                        .fullName(fullName)
                        .dob(dob)
                        .parentGmail(parentGmail)
                        .parentPhonenumber(parentPhonenumber)
                        .gender(gender)
                        .academicYear(academicYear)
                        .className(className)
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
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
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
        try { return (int) cell.getNumericCellValue(); } catch (Exception e) { return 0; }
    }
}
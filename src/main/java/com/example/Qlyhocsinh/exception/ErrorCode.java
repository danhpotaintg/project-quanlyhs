package com.example.Qlyhocsinh.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_EXISTED(1002, "Username đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User khong ton tai", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Ban khong co quyen truy cap", HttpStatus.FORBIDDEN),
    WRONG_ACCOUNT(1008, "Sai tai khoan hoac mat khau", HttpStatus.BAD_REQUEST),
    ClASS_EXISTED(1009, "Lop hoc da ton tai", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1009, "Mật khẩu hiện tại không chính xác", HttpStatus.BAD_REQUEST),
    CLASS_NOT_FOUND(1010, "Lớp học không tồn tại", HttpStatus.NOT_FOUND),
    STUDENT_NOT_FOUND(1011, "Học sinh không tồn tại", HttpStatus.NOT_FOUND),
    TEACHER_NOT_FOUND(1012, "Giáo viên không tồn tại", HttpStatus.NOT_FOUND),
    SUBJECT_NOT_FOUND(1015, "Môn học không tồn tại", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_FOUND(1016, "Lịch học không tồn tại", HttpStatus.NOT_FOUND),
    GRADE_CONFIG_NOT_FOUND(1017, "Đầu điểm không tồn tại", HttpStatus.NOT_FOUND),
    CLASS_HAS_TEACHER(1017, "Lớp học đã có GVCN", HttpStatus.NOT_FOUND),
    SCHEDULE_FOR_TEACHER_EXISTED(1018, "Giáo viên trùng lịch dạy", HttpStatus.NOT_FOUND),
    CLASS_SCHEDULE_EXISTED(1019, "Lớp học bị trùng lịch học", HttpStatus.NOT_FOUND),
    GRADE_NOT_FOUND(1050, "Không tìm thấy bản ghi điểm.", HttpStatus.NOT_FOUND),
    GRADE_ALREADY_EXISTS(1051, "Điểm đã tồn tại. Dùng PUT để cập nhật.", HttpStatus.CONFLICT),
    GRADE_ENTRY_INDEX_INVALID(1053, "entry_index vượt quá số lần nhập cho phép.", HttpStatus.BAD_REQUEST),
    GRADE_SCORE_INVALID(1054, "Điểm phải từ 0 đến 10.", HttpStatus.BAD_REQUEST),
    GRADE_FORBIDDEN(1055, "Bạn không có quyền thao tác với bản ghi điểm này.", HttpStatus.FORBIDDEN),
    SUBJECT_ALREADY_EXISTS(1060, "Môn đã tồn tại", HttpStatus.CONFLICT)

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}

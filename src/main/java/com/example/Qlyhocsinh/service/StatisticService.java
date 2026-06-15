package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.StudentRankingRequest;
import com.example.Qlyhocsinh.dto.request.SemesterAndAcademicYearRequest;
import com.example.Qlyhocsinh.dto.response.StudentGradeResponse;
import com.example.Qlyhocsinh.dto.response.StudentRankingResponse;
import com.example.Qlyhocsinh.dto.response.TeacherStatisticResponse;
import com.example.Qlyhocsinh.entity.Grade;
import com.example.Qlyhocsinh.entity.GradeConfig;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final GradeConfigRepository gradeConfigRepository;
    private final SubjectRepository subjectRepository;

    public List<TeacherStatisticResponse> getTeacherStatistics(SemesterAndAcademicYearRequest request){
        return teacherRepository.getTeacherStatistics(request.getSemester(), request.getAcademicYear());
    }

    public List<StudentRankingResponse> getStudentsHighestScoreBySubject(StudentRankingRequest request) {

        // Bước 1: Lấy danh sách học sinh từ DB (chưa sort, chỉ lọc đúng môn/kỳ/năm/khóa)
        List<Student> topStudents = studentRepository.findTopStudentsBySubjectAndSemester(
                request.getSubjectId(),
                request.getSemester(),
                request.getAcademicYear(),
                request.getStudentCohort(),
                PageRequest.of(0, request.getRankingQuantity())
        );

        // Bước 2: Lấy danh sách cấu hình điểm (GradeConfig) của môn học trong học kỳ và năm học
        List<GradeConfig> gradeConfigs = gradeConfigRepository.findBySubjectIdAndSemesterAndAcademicYear(
                request.getSubjectId(),
                request.getSemester(),
                request.getAcademicYear()
        );

        // Bước 3: Trích xuất danh sách ID của các cấu hình điểm để dùng khi truy vấn điểm
        List<Long> configIds = gradeConfigs.stream()
                .map(GradeConfig::getId)
                .collect(Collectors.toList());

        // Bước 4: Khởi tạo danh sách rỗng chứa kết quả trả về
        List<StudentRankingResponse> responseList = new ArrayList<>();

        // Bước 5: Lặp qua từng học sinh để tính điểm trung bình học kỳ
        for (Student student : topStudents) {

            // Lấy toàn bộ điểm của học sinh này theo các cấu hình điểm
            List<Grade> studentGrades = gradeRepository.findByStudentIdAndGradeConfigIdIn(
                    student.getId(),
                    configIds
            );

            // Tử số: tổng (điểm × hệ số)
            double totalWeightedScore = 0.0;
            // Mẫu số: tổng (số đầu điểm × hệ số)
            double totalExpectedWeight = 0.0;

            List<StudentRankingResponse.GradeConfigDetail> configDetails = new ArrayList<>();

            // Bước 6: Duyệt qua từng cấu hình điểm để tính đóng góp vào ĐTB
            for (GradeConfig config : gradeConfigs) {

                // Lọc ra các điểm của học sinh thuộc cấu hình hiện tại
                List<Double> scores = new ArrayList<>();
                for (Grade g : studentGrades) {
                    if (g.getGradeConfigId().equals(config.getId())) {
                        scores.add(g.getScore());
                    }
                }

                // Công thức đúng: cộng thẳng từng điểm × hệ số vào tử số
                // Ví dụ thuong_xuyen [8, 9, 9] weight=1 → 8×1 + 9×1 + 9×1 = 26
                for (Double score : scores) {
                    totalWeightedScore += score * config.getWeight();
                }

                // Mẫu số cộng thêm: số đầu điểm quy định × hệ số
                // Ví dụ thuong_xuyen maxEntries=3, weight=1 → 3×1 = 3
                totalExpectedWeight += config.getWeight() * config.getMaxEntries();

                // Đóng gói chi tiết điểm theo từng cấu hình vào DTO
                StudentRankingResponse.GradeConfigDetail detail = StudentRankingResponse.GradeConfigDetail.builder()
                        .gradeConfigId(config.getId())
                        .scoreType(config.getScoreType())
                        .weight(config.getWeight())
                        .maxEntries(config.getMaxEntries())
                        .scores(scores)
                        .build();

                configDetails.add(detail);
            }

            // Bước 7: Tính ĐTB học kỳ = tổng(điểm × hệ số) / tổng(số đầu điểm × hệ số)
            // Ví dụ: (8+9+9 + 8×2 + 9.5×5) / (3 + 2 + 5) = 89.5 / 10 = 8.95
            double semesterAvg = (totalExpectedWeight > 0) ? (totalWeightedScore / totalExpectedWeight) : 0.0;
            // Làm tròn 2 chữ số thập phân
            semesterAvg = Math.round(semesterAvg * 100.0) / 100.0;

            String className = student.getClassRoom() != null ? student.getClassRoom().getClassName() : "Chưa có lớp";

            // Bước 8: Đóng gói kết quả của học sinh vào DTO response
            StudentRankingResponse studentResponse = StudentRankingResponse.builder()
                    .studentName(student.getFullName())
                    .className(className)
                    .semesterAverage(semesterAvg)
                    .gradeConfigs(configDetails)
                    .build();

            responseList.add(studentResponse);
        }

        // Bước 9: Sort giảm dần theo ĐTB học kỳ trước khi trả về
        return responseList.stream()
                .sorted((a, b) -> Double.compare(b.getSemesterAverage(), a.getSemesterAverage()))
                .collect(Collectors.toList());
    }
}
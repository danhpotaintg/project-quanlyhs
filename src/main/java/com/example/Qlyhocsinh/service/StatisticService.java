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
        return teacherRepository.getTeacherStatistics(request.getSemester(),request.getAcademicYear());
    }

    public List<StudentRankingResponse> getStudentsHighestScoreBySubject(StudentRankingRequest request) {

        // 1. Lấy danh sách các học sinh có điểm cao nhất từ database
        List<Student> topStudents = studentRepository.findTopStudentsBySubjectAndSemester(
                request.getSubjectId(),
                request.getSemester(),
                request.getAcademicYear(),
                request.getStudentCohort(),
                PageRequest.of(0, request.getRankingQuantity())
        );

        // 2. Lấy danh sách cấu hình điểm (GradeConfig) của môn học trong học kỳ và năm học tương ứng
        List<GradeConfig> gradeConfigs = gradeConfigRepository.findBySubjectIdAndSemesterAndAcademicYear(
                request.getSubjectId(),
                request.getSemester(),
                request.getAcademicYear()
        );

        // Trích xuất danh sách ID của các cấu hình điểm
        List<Long> configIds = gradeConfigs.stream()
                .map(GradeConfig::getId)
                .collect(Collectors.toList());

        // 3. Khởi tạo danh sách rỗng chứa kết quả DTO mới
        List<StudentRankingResponse> responseList = new ArrayList<>();

        // 4. Lặp qua từng học sinh trong danh sách top để tính toán
        for (Student student : topStudents) {

            // Truy vấn toàn bộ các điểm số mà học sinh này đạt được
            List<Grade> studentGrades = gradeRepository.findByStudentIdAndGradeConfigIdIn(
                    student.getId(),
                    configIds
            );

            double totalWeightedScore = 0.0;
            double totalExpectedWeight = 0.0;
            List<StudentRankingResponse.GradeConfigDetail> configDetails = new ArrayList<>();

            // 5. Duyệt qua từng cấu hình điểm
            for (GradeConfig config : gradeConfigs) {

                // Lọc ra điểm của học sinh khớp với cấu hình hiện tại
                List<Double> scores = new ArrayList<>();
                for (Grade g : studentGrades) {
                    if (g.getGradeConfigId().equals(config.getId())) {
                        scores.add(g.getScore());
                    }
                }

                // Tính tổng các điểm của cấu hình này
                double sumForThisConfig = 0.0;
                for (Double score : scores) {
                    sumForThisConfig += score;
                }

                // Tính điểm trung bình của cấu hình điểm này (chia cho số đầu điểm quy định)
                double avgScoreForThisConfig = 0.0;
                if (config.getMaxEntries() != null && config.getMaxEntries() > 0) {
                    avgScoreForThisConfig = sumForThisConfig / config.getMaxEntries();
                }

                // Cộng dồn điểm và hệ số vào tổng chung
                totalWeightedScore += (avgScoreForThisConfig * config.getWeight());
                totalExpectedWeight += config.getWeight();

                // Đóng gói chi tiết điểm theo DTO nội bộ (Inner Class) mới
                StudentRankingResponse.GradeConfigDetail detail = StudentRankingResponse.GradeConfigDetail.builder()
                        .gradeConfigId(config.getId())

                        .scoreType(config.getScoreType())
                        .weight(config.getWeight())
                        .maxEntries(config.getMaxEntries())
                        .scores(scores)
                        .build();

                configDetails.add(detail);
            }

            // 6. Tính điểm trung bình tổng kết học kỳ và làm tròn 2 chữ số thập phân
            double semesterAvg = (totalExpectedWeight > 0) ? (totalWeightedScore / totalExpectedWeight) : 0.0;
            semesterAvg = Math.round(semesterAvg * 100.0) / 100.0;

            String className = student.getClassRoom() != null ? student.getClassRoom().getClassName() : "Chưa có lớp";

            // 7. Khởi tạo đối tượng Response MỚI (StudentRankingResponse)
            StudentRankingResponse studentResponse = StudentRankingResponse.builder()
                    .studentName(student.getFullName())
                    .className(className)// Lấy tên trực tiếp từ Entity Student
                    .semesterAverage(semesterAvg)
                    .gradeConfigs(configDetails)
                    .build();

            // 8. Thêm học sinh vào danh sách kết quả
            responseList.add(studentResponse);
        }

        // 9. Trả về danh sách xếp hạng
        return responseList;
    }
}

package com.example.Qlyhocsinh.service;


import com.example.Qlyhocsinh.dto.request.StudentRankingRequest;
import com.example.Qlyhocsinh.dto.request.SemesterAndAcademicYearRequest;
import com.example.Qlyhocsinh.dto.response.StudentGradeResponse;
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

    public List<StudentGradeResponse> getStudentsHighestScoreBySubject(StudentRankingRequest request) {

        // 1. Lấy danh sách các học sinh có điểm cao nhất từ database dựa trên điều kiện tìm kiếm và phân trang
        List<Student> topStudents = studentRepository.findTopStudentsBySubjectAndSemester(
                request.getSubjectId(),
                request.getSemester(),
                request.getAcademicYear(),
                request.getStudentCohort(),
                PageRequest.of(0, request.getRankingQuantity())
        );

        // 2. Lấy danh sách cấu hình điểm (GradeConfig) của môn học trong học kỳ và năm học tương ứng
        // Ví dụ: Cấu hình điểm miệng (hệ số 1, tối đa 1 cột), điểm 15 phút (hệ số 1, tối đa 3 cột), v.v.
        List<GradeConfig> gradeConfigs = gradeConfigRepository.findBySubjectIdAndSemesterAndAcademicYear(
                request.getSubjectId(),
                request.getSemester(),
                request.getAcademicYear()
        );

        // Trích xuất danh sách ID của các cấu hình điểm để phục vụ cho việc truy vấn điểm thực tế
        List<Long> configIds = gradeConfigs.stream()
                .map(GradeConfig::getId)
                .collect(Collectors.toList());

        // 3. Khởi tạo danh sách rỗng để chứa kết quả trả về cho client
        List<StudentGradeResponse> responseList = new ArrayList<>();

        // 4. Lặp qua từng học sinh trong danh sách top để tính toán điểm và ánh xạ dữ liệu
        for (Student student : topStudents) {

            // Truy vấn toàn bộ các điểm số mà học sinh này đã đạt được thuộc về các cấu hình điểm ở trên
            List<Grade> studentGrades = gradeRepository.findByStudentIdAndGradeConfigIdIn(
                    student.getId(),
                    configIds
            );

            double totalWeightedScore = 0.0;  // Biến lưu tổng điểm sau khi đã nhân hệ số
            double totalExpectedWeight = 0.0; // Biến lưu tổng hệ số quy định của toàn bộ môn học
            List<StudentGradeResponse.GradeConfigDetail> configDetails = new ArrayList<>();

            // 5. Duyệt qua từng cấu hình điểm để tính toán chi tiết
            for (GradeConfig config : gradeConfigs) {

                // Lọc ra các con điểm thực tế của học sinh khớp với cấu hình điểm đang xét
                List<Double> scores = new ArrayList<>();
                for (Grade g : studentGrades) {
                    if (g.getGradeConfigId().equals(config.getId())) {
                        scores.add(g.getScore());
                    }
                }

                // Tính tổng các con điểm đã được giáo viên nhập cho loại cấu hình này
                double sumForThisConfig = 0.0;
                for (Double score : scores) {
                    sumForThisConfig += score;
                }

                // Tính điểm trung bình của loại cấu hình này
                // Chia cho số cột điểm tối đa quy định (maxEntries) để đánh giá đúng tiến độ và điểm số thực tế
                double avgScoreForThisConfig = 0.0;
                if (config.getMaxEntries() != null && config.getMaxEntries() > 0) {
                    avgScoreForThisConfig = sumForThisConfig / config.getMaxEntries();
                }

                // Cộng dồn điểm thành phần (đã nhân trọng số) vào tổng điểm môn học
                totalWeightedScore += (avgScoreForThisConfig * config.getWeight());

                // Cộng dồn trọng số của cấu hình điểm này vào tổng trọng số quy định
                totalExpectedWeight += config.getWeight();

                // Đóng gói thông tin chi tiết của loại cấu hình điểm này vào DTO để hiển thị
                StudentGradeResponse.GradeConfigDetail detail = StudentGradeResponse.GradeConfigDetail.builder()
                        .gradeConfigId(config.getId())
                        .scoreType(config.getScoreType())
                        .weight(config.getWeight())
                        .maxEntries(config.getMaxEntries())
                        .scores(scores) // Truyền danh sách các con điểm đã nhập để frontend có thể render
                        .build();

                // Thêm chi tiết điểm vào danh sách cấu hình của học sinh
                configDetails.add(detail);
            }

            // 6. Tính điểm trung bình tổng kết học kỳ của môn học
            // Công thức: Tổng (Điểm trung bình thành phần * Hệ số) / Tổng hệ số quy định
            double semesterAvg = (totalExpectedWeight > 0) ? (totalWeightedScore / totalExpectedWeight) : 0.0;

            // Làm tròn điểm trung bình đến 2 chữ số thập phân (ví dụ: 8.666... sẽ thành 8.67)
            semesterAvg = Math.round(semesterAvg * 100.0) / 100.0;

            // Truy vấn tên môn học từ database dựa trên subjectId
            String subjectName = subjectRepository.findById(request.getSubjectId())
                    .get()
                    .getSubjectName();

            // 7. Khởi tạo đối tượng Response chứa toàn bộ thông tin kết quả học tập của học sinh
            StudentGradeResponse studentResponse = StudentGradeResponse.builder()
                    .subjectId(request.getSubjectId())
                    .subjectName(subjectName)
                    .semester(request.getSemester())
                    .academicYear(request.getAcademicYear())
                    .semesterAverage(semesterAvg)
                    .gradeConfigs(configDetails)
                    .build();

            // 8. Thêm học sinh vừa xử lý xong vào danh sách kết quả tổng
            responseList.add(studentResponse);
        }

        // 9. Trả về danh sách xếp hạng đã được tính toán và ánh xạ hoàn chỉnh
        return responseList;
    }
}

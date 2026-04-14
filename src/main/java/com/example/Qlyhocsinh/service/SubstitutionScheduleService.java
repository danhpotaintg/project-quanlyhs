package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.SubstitutionScheduleRequest;
import com.example.Qlyhocsinh.dto.response.SubstitutionScheduleResponse;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.entity.Schedule;
import com.example.Qlyhocsinh.entity.SubstitutionSchedule;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.SubstitutionScheduleMapper;
import com.example.Qlyhocsinh.mapper.TeacherMapper;
import com.example.Qlyhocsinh.repository.ScheduleRepository;
import com.example.Qlyhocsinh.repository.SubstitutionScheduleRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubstitutionScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TeacherRepository teacherRepository;
    private final SubstitutionScheduleMapper substitutionScheduleMapper;
    private final SubstitutionScheduleRepository substitutionScheduleRepository;
    private final TeacherMapper teacherMapper;

    public SubstitutionScheduleResponse assignTeacherSubstitution(SubstitutionScheduleRequest request){
        Schedule originalSchedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        Teacher teacherSubstitution = teacherRepository.findById(request.getTeacherSubstitutionId())
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        SubstitutionSchedule substitutionSchedule = substitutionScheduleMapper.toSubstitutionSchedule(request);

        substitutionSchedule.setOriginalSchedule(originalSchedule);
        substitutionSchedule.setSubstituteTeacher(teacherSubstitution);
        substitutionSchedule.setAbsentTeacher(originalSchedule.getTeacher());

        return substitutionScheduleMapper
                .toSubstitutionScheduleResponse(substitutionScheduleRepository.save(substitutionSchedule));
    }

    public List<TeacherResponse> getALLTeacherIsFree(String scheduleId, boolean onlySameSubject) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        //chọn cùng môn hay khác môn
        String subjectToFilter = onlySameSubject ? schedule.getSubject().getSubjectName() : null;

        List<Teacher> teachers = substitutionScheduleRepository.findAvailableSubstitutes(
                subjectToFilter,
                schedule.getDayOfWeek(),
                schedule.getStartLesson(),
                schedule.getEndLesson()
        );

        return teachers.stream()
                .map(teacherMapper::toTeacherResponse)
                .toList();
    }
}

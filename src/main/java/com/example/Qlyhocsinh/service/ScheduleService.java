package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.ScheduleRequest;
import com.example.Qlyhocsinh.dto.response.ClassResponse;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.entity.Schedule;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.ClassMapper;
import com.example.Qlyhocsinh.mapper.ScheduleMapper;
import com.example.Qlyhocsinh.repository.ClassRepository;
import com.example.Qlyhocsinh.repository.ScheduleRepository;
import com.example.Qlyhocsinh.repository.SubjectRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final ClassMapper classMapper;

    public ScheduleResponse createSchedule(ScheduleRequest request){
        ClassRoom classRoom = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        Schedule schedule = scheduleMapper.toSchedule(request);
        schedule.setClassRoom(classRoom);
        schedule.setTeacher(teacher);
        schedule.setSubject(subject);

        return scheduleMapper.toScheduleResponse(scheduleRepository.save(schedule));
    }

    public ScheduleResponse updateSchedule(String id, ScheduleRequest request){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        scheduleMapper.updateSchedule(schedule, request);

        return scheduleMapper.toScheduleResponse(scheduleRepository.save(schedule));
    }

    public List<ScheduleResponse> getAll(){
        return scheduleMapper.toScheduleResponseList(scheduleRepository.findAll());
    }

    public ScheduleResponse getById(String id){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        return scheduleMapper.toScheduleResponse(schedule);
    }

    public List<ClassResponse> getAllClassByTeacher(){
        var context = SecurityContextHolder.getContext();
        String teacherName = context.getAuthentication().getName();

        Teacher teacher = teacherRepository.findByUserUsername(teacherName)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        List<ClassRoom> classes = scheduleRepository.findDistinctClassByTeacherId(teacher.getUserId());

        return classMapper.toClassResponseList(classes);
    }

    public void deleteSchedule(String id){
        scheduleRepository.deleteById(id);
    }
}

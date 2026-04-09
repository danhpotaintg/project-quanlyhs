package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.ScheduleRequest;
import com.example.Qlyhocsinh.dto.response.ClassResponse;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.entity.*;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.ClassMapper;
import com.example.Qlyhocsinh.mapper.ScheduleMapper;
import com.example.Qlyhocsinh.mapper.StudentMapper;
import com.example.Qlyhocsinh.repository.*;
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
    private final StudentRepository studentRepository;


    public ScheduleResponse createSchedule(ScheduleRequest request){
        ClassRoom classRoom = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        Subject subject = subjectRepository.findBySubjectName(request.getSubjectName())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        if(!teacher.getSubject().getId().equals(subject.getId()))
            throw new AppException(ErrorCode.TEACHER_DOESNT_HAVE_SUBJECT);



        List<Schedule> scheduleExisted = scheduleRepository.findScheduleConflicts(
                request.getAcademicYear(),
                request.getSemester(),
                request.getDayOfWeek(),
                request.getStartLesson(),
                request.getEndLesson()
        );

        for(Schedule s : scheduleExisted){
            if(s.getTeacher().getId().equals(request.getTeacherId())){
                throw new AppException(ErrorCode.SCHEDULE_FOR_TEACHER_EXISTED);
            }
            if(s.getClassRoom().getId().equals(request.getClassId())){
                throw new AppException(ErrorCode.CLASS_SCHEDULE_EXISTED);
            }
        }

        Schedule schedule = scheduleMapper.toSchedule(request);
        schedule.setClassRoom(classRoom);
        schedule.setTeacher(teacher);
        schedule.setSubject(subject);

        return scheduleMapper.toScheduleResponse(scheduleRepository.save(schedule));
    }

    public ScheduleResponse updateSchedule(String id, ScheduleRequest request){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        scheduleMapper.updateSchedule(schedule, request);

        return scheduleMapper.toScheduleResponse(scheduleRepository.save(schedule));
    }

    public List<ScheduleResponse> getAll(){
        return scheduleMapper.toScheduleResponseList(scheduleRepository.findAll());
    }

    public ScheduleResponse getById(String id){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        return scheduleMapper.toScheduleResponse(schedule);
    }

    public List<ClassResponse> getAllClassByTeacher(){
        var context = SecurityContextHolder.getContext();
        String teacherName = context.getAuthentication().getName();

        Teacher teacher = teacherRepository.findByUserUsername(teacherName)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        List<ClassRoom> classes = scheduleRepository.findDistinctClassByTeacherId(teacher.getId());

        return classMapper.toClassResponseList(classes);
    }

    public List<ScheduleResponse> getAllScheduleByTeacher(){
        var context = SecurityContextHolder.getContext();
        String teacherName = context.getAuthentication().getName();

        Teacher teacher = teacherRepository.findByUserUsername(teacherName)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findByTeacherId(teacher.getId());

        return scheduleMapper.toScheduleResponseList(schedules);
    }

    public List<ScheduleResponse> getAllScheduleByStudent(){
        var context = SecurityContextHolder.getContext();
        String studentName = context.getAuthentication().getName();

        Student student = studentRepository.findByUserUsername(studentName)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findByClassRoomId(student.getClassRoom().getId());

        return scheduleMapper.toScheduleResponseList(schedules);
    }

    public List<ScheduleResponse> getAllScheduleByClass(Long classId){
        List<Schedule> schedules = scheduleRepository.findByClassRoomId(classId);

        return scheduleMapper.toScheduleResponseList(schedules);

    }

    public void deleteSchedule(String id){
        scheduleRepository.deleteById(id);
    }
}

package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.SubstitutionScheduleRequest;
import com.example.Qlyhocsinh.dto.response.SubstitutionScheduleResponse;
import com.example.Qlyhocsinh.entity.SubstitutionSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubstitutionScheduleMapper {
    SubstitutionSchedule toSubstitutionSchedule(SubstitutionScheduleRequest request);

    @Mapping(source = "originalSchedule.classRoom.id", target = "classId")
    @Mapping(source = "originalSchedule.classRoom.className", target = "className")
    @Mapping(source = "originalSchedule.subject.id", target = "subjectId")
    @Mapping(source = "originalSchedule.subject.subjectName", target = "subjectName")
    @Mapping(source = "substituteTeacher.id", target = "teacherSubstitutionId")
    @Mapping(source = "substituteTeacher.fullName", target = "teacherSubstitutionName")
    @Mapping(source = "originalSchedule.startLesson", target = "startLesson")
    @Mapping(source = "originalSchedule.endLesson", target = "endLesson")
    @Mapping(source = "originalSchedule.semester", target = "semester")
    @Mapping(source = "originalSchedule.dayOfWeek", target = "dayOfWeek")
    @Mapping(source = "originalSchedule.academicYear", target = "academicYear")
    SubstitutionScheduleResponse toSubstitutionScheduleResponse(SubstitutionSchedule substitutionSchedule);
}

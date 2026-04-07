package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.ScheduleRequest;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.entity.Schedule;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    Schedule toSchedule(ScheduleRequest request);

    @Mapping(source = "classRoom.id", target = "classId")
    @Mapping(source = "classRoom.className", target = "className")
    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.subjectName", target = "subjectName")
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.fullName", target = "teacherName")
    ScheduleResponse toScheduleResponse(Schedule schedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSchedule(@MappingTarget Schedule schedule, ScheduleRequest request);

    List<ScheduleResponse> toScheduleResponseList(List<Schedule> schedules);
}

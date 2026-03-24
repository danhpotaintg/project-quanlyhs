package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.ScheduleRequest;
import com.example.Qlyhocsinh.dto.response.ScheduleResponse;
import com.example.Qlyhocsinh.entity.Schedule;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    Schedule toSchedule(ScheduleRequest request);

    ScheduleResponse toScheduleResponse(Schedule schedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSchedule(@MappingTarget Schedule schedule, ScheduleRequest request);

    List<ScheduleResponse> toScheduleResponseList(List<Schedule> schedules);
}

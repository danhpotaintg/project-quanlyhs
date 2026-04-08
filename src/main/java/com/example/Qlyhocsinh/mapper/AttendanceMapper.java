package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.AttendanceRequest;
import com.example.Qlyhocsinh.dto.response.AttendanceResponse;
import com.example.Qlyhocsinh.entity.Attendance;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    Attendance toAttendance(AttendanceRequest request);

    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "teacher.fullName", target = "teacherName")
    AttendanceResponse toAttendanceResponse(Attendance attendance);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAttendance(@MappingTarget Attendance attendance, AttendanceRequest request);

    List<AttendanceResponse> toAttendanceResponseList(List<Attendance> attendances);

}

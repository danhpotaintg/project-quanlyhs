package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.LeaveRequestRequest;
import com.example.Qlyhocsinh.dto.response.LeaveRequestResponse;
import com.example.Qlyhocsinh.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {
    LeaveRequest toLeaveRequest(LeaveRequestRequest request);

    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "student.classRoom.className", target = "className")
    @Mapping(source = "approvedByTeacher.fullName", target = "approvedByTeacherName")
    LeaveRequestResponse toLeaveRequestResponse(LeaveRequest request);


    List<LeaveRequestResponse> toLeaveRequestResponseList(List<LeaveRequest> leaveRequests);
}

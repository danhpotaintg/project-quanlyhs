package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.AssignStudentClassRequest;
import com.example.Qlyhocsinh.dto.response.AssignStudentClassResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssignMapper {
    @Mapping(source = "classRoom.className", target = "className")
    @Mapping(source = "academicYear", target = "academicYear")
    @Mapping(source = "students", target = "assignedStudentIds")
    AssignStudentClassResponse toAssignStudentClassResponse(ClassRoom classRoom, int academicYear, List<Student> students);

    default String mapStudentToId(Student student) {
        return student != null ? student.getUserId() : null;
    }
}

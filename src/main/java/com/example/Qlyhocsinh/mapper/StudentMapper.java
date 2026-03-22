package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.request.StudentUpdateRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.entity.Student;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ClassMapper.class})
public interface StudentMapper {

    @Mapping(target = "user", ignore = true)
    Student toStudent(StudentCreationRequest request);


    @Mapping(source = "user.id", target = "id")
    //@Mapping(source = "user", target = "user")
    StudentResponse toStudentResponse(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStudent(@MappingTarget Student student, StudentUpdateRequest request);

    List<StudentResponse> toStudentResponseList(List<Student> students);

}

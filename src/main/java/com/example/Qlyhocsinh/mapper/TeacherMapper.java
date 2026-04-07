package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherUpdateResquest;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.entity.Teacher;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "user", ignore = true)
    Teacher toTeacher(TeacherCreationRequest request);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "classRoom.className", target = "className")
    @Mapping(source = "subject.subjectName", target = "subjectName")
    TeacherResponse toTeacherResponse(Teacher teacher);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // not update null
    void toUpdateTeacher(@MappingTarget Teacher teacher, TeacherUpdateResquest request);

}

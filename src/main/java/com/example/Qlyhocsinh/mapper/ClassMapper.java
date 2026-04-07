package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.ClassRequest;
import com.example.Qlyhocsinh.dto.response.ClassResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = ClassMapper.class)
public interface ClassMapper {

    ClassRoom toClassRoom(ClassRequest request);

    @Mapping(source = "teacher.fullName", target = "teacherName")
    ClassResponse toClassResponse(ClassRoom classRoom);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClass(@MappingTarget ClassRoom classRoom, ClassRequest request);

    List<ClassResponse> toClassResponseList(List<ClassRoom> classRooms);
}

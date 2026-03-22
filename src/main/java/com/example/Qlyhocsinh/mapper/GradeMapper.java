package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.entity.Grade;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeMapper {

    Grade toGrade(GradeRequest request);

    GradeResponse toGradeResponse(Grade grade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGrade(@MappingTarget Grade grade, GradeRequest request);

    List<GradeResponse> toGradeResponseList(List<Grade> grades);
}

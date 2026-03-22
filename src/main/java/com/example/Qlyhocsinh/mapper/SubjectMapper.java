package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.SubjectRequest;
import com.example.Qlyhocsinh.dto.response.SubjectResponse;
import com.example.Qlyhocsinh.entity.Subject;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    Subject tosubject(SubjectRequest request);

    SubjectResponse toSubjectResponse(Subject subject);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSubject(@MappingTarget Subject subject, SubjectRequest request);

    List<SubjectResponse> toSubjectResponseList(List<Subject> subjects);
}

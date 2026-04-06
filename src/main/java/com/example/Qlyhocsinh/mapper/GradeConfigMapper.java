package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.GradeConfigRequest;
import com.example.Qlyhocsinh.dto.response.GradeConfigResponse;
import com.example.Qlyhocsinh.entity.GradeConfig;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GradeConfigMapper {

    GradeConfig toGradeConfig(GradeConfigRequest request);

    @Mapping(target = "subjectName", ignore = true)
    GradeConfigResponse toGradeConfigResponse(GradeConfig gradeConfig);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGradeConfig(GradeConfigRequest request, @MappingTarget GradeConfig gradeConfig);
}

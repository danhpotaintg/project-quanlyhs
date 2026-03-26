package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.*;
import com.example.Qlyhocsinh.dto.response.UserResponse;
import com.example.Qlyhocsinh.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(StudentCreationRequest request);

    User toUser(TeacherCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    void updateUserByID(@MappingTarget User user, UserUpdateByIDRequest request);
}
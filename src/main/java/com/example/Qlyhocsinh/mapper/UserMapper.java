package com.example.Qlyhocsinh.mapper;

import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.request.UserCreationRequest;
import com.example.Qlyhocsinh.dto.request.UserUpdateRequest;
import com.example.Qlyhocsinh.dto.response.UserResponse;
import com.example.Qlyhocsinh.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(StudentCreationRequest request);

    User toUser(TeacherCreationRequest request);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}

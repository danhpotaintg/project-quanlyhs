package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.UserUpdateRequest;
import com.example.Qlyhocsinh.dto.response.UserResponse;
import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.UserMapper;
import com.example.Qlyhocsinh.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    PasswordEncoder passwordEncoder;
    

//    public UserResponse createUser(UserCreationRequest request){
//        if(userRepository.findByUsername(request.getUsername()).isPresent()){
//            throw new AppException(ErrorCode.USERNAME_EXISTED);
//        }
//
//        User user = userMapper.toUser(request);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        user.setRole(Role.USER.name());
//
//        return userMapper.toUserResponse(userRepository.save(user));
//    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(){
        log.info("O in method getUsers");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }



    public UserResponse updateUser(UserUpdateRequest request) {

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isPasswordMatch = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }


        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }


    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id){
        log.info("O in method getUser by ID");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }


    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

         User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

         return userMapper.toUserResponse(user);
    }



}

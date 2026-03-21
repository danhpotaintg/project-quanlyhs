package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.UserCreationRequest;
import com.example.Qlyhocsinh.dto.request.UserUpdateRequest;
import com.example.Qlyhocsinh.dto.response.UserResponse;
import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.enums.Role;
import com.example.Qlyhocsinh.mapper.UserMapper;
import com.example.Qlyhocsinh.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request){
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        HashSet<String> roles = new HashSet<>();
        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            request.getRoles().forEach(role -> {
                try {
                    Role.valueOf(role);
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Role không hợp lệ: " + role);
                }
            });
        }
        else {
            roles.add(Role.USER.name());
        }

        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(){
        log.info("O trong method getUsers");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }


    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(String id, UserUpdateRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }


    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id){
        log.info("O trong method getUser by ID");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }


    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

         User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User khong ton tai"));

         return userMapper.toUserResponse(user);
    }
}

package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.UserCreationRequest;
import com.example.Qlyhocsinh.dto.request.UserUpdateRequest;
import com.example.Qlyhocsinh.dto.response.UserResponse;
import com.example.Qlyhocsinh.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    private UserService userService;

//    @PostMapping
//    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
//        return ApiResponse.<UserResponse>builder()
//                .result(userService.createUser(request))
//                .build();
//    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities()
                .forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUsers(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User da duoc xoa")
                .build();
    }

    @PutMapping
    ApiResponse<UserResponse> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(request))
                .build();
    }
}

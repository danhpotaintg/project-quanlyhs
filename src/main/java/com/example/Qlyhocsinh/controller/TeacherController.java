package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.TeacherCreationRequest;
import com.example.Qlyhocsinh.dto.request.TeacherUpdateResquest;
import com.example.Qlyhocsinh.dto.response.TeacherResponse;
import com.example.Qlyhocsinh.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")

public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    TeacherResponse createTeacher(@RequestBody TeacherCreationRequest request){
        return teacherService.createTeacher(request);
    }

    @PutMapping("/{id}")
    TeacherResponse updateTeacher(@PathVariable String id, TeacherUpdateResquest request){
        return teacherService.updateTeacher(id, request);
    }

    @GetMapping
    List<TeacherResponse> getAll(){
        return teacherService.getAll();
    }

    @DeleteMapping("/{id}")
    String deleteTeacher(@PathVariable String id){
        teacherService.deleteTea(id);
        return "Teacher has been deleted !";
    }

    @GetMapping("/{id}")
    TeacherResponse getTeacher(@PathVariable String id){
        return teacherService.getTea(id);
    }


}

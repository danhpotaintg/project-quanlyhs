package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.StudentCreationRequest;
import com.example.Qlyhocsinh.dto.request.StudentUpdateRequest;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    StudentResponse createStudent(@RequestBody StudentCreationRequest request){
        return studentService.creatStudent(request);
    }

    @PutMapping("/{id}")
    StudentResponse updateStudent(@PathVariable String id, @RequestBody StudentUpdateRequest request){
        return studentService.updateStudent(id,request);
    }

    @GetMapping
    List<StudentResponse> getAll(){
        return studentService.getAllStudents();
    }

    @DeleteMapping("/{id}")
    String deleteStudent(@PathVariable String id){
        studentService.deleteStudent(id);
        return "Student has been deleted !";
    }

    @GetMapping("/{id}")
    StudentResponse getStudent(@PathVariable String id){
        return studentService.getStudent(id);
    }

}

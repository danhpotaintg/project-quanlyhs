package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.AssignStudentClassRequest;
import com.example.Qlyhocsinh.dto.request.ClassRequest;
import com.example.Qlyhocsinh.dto.response.AssignStudentClassResponse;
import com.example.Qlyhocsinh.dto.response.ClassResponse;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @PostMapping
    ApiResponse<ClassResponse> createClass(@RequestBody ClassRequest request){

        return ApiResponse.<ClassResponse>builder()
                .result(classService.createClass(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ClassResponse> updateClass(@PathVariable Long id, @RequestBody ClassRequest request) {
        return ApiResponse.<ClassResponse>builder()
                .result(classService.updateClass(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ClassResponse>> getAll() {
        return ApiResponse.<List<ClassResponse>>builder()
                .result(classService.getAllClass())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteClass(@PathVariable Long id) {
        classService.deletedClass(id);
        return ApiResponse.<String>builder()
                .result("Class has been deleted !")
                .build();
    }

    @PostMapping("/{clsId}/addstudent/{stuId}")
    ApiResponse<StudentResponse> addStuToClass(@PathVariable Long clsId, @PathVariable String stuId) {
        return ApiResponse.<StudentResponse>builder()
                .result(classService.addStudentToClass(stuId, clsId))
                .build();
    }

    @PostMapping("/{classId}/addstudent")
    ApiResponse<AssignStudentClassResponse> addStudentsToClass(@PathVariable Long classId ,@RequestBody AssignStudentClassRequest request){
        return ApiResponse.<AssignStudentClassResponse>builder()
                .result(classService.addStudentsToClass(classId, request))
                .build();
    }

    @GetMapping("{clsId}/students")
    ApiResponse<List<StudentResponse>> getStuInClass(@PathVariable Long clsId) {
        return ApiResponse.<List<StudentResponse>>builder()
                .result(classService.getStuInClass(clsId))
                .build();
    }

    @DeleteMapping("/{clsId}/removestudent/{stuId}")
    ApiResponse<String> removeStudentInClass(@PathVariable String stuId, @PathVariable Long clsId) {
        classService.removeStudentFromClass(stuId, clsId);
        return ApiResponse.<String>builder()
                .result("Student has been deleted from class")
                .build();
    }

    @PostMapping("/{clsId}/tranferstudent/{stuId}")
    ApiResponse<String> transferStudent(@PathVariable String stuId, @PathVariable Long clsId) {
        classService.transferStudent(stuId, clsId);
        return ApiResponse.<String>builder()
                .result("Students successfully transferred classes")
                .build();
    }

    @PostMapping("/{classId}/assign-teacher/{teacherId}")
    ApiResponse<ClassResponse> assignTeacher(@PathVariable Long classId, @PathVariable String teacherId) {

        return ApiResponse.<ClassResponse>builder()
                .result(classService.addClassToTeacher(teacherId, classId))
                .build();
    }

    @PostMapping("/{classId}/remove-teacher/{teacherId}")
    ApiResponse<String> removeTeacher(@PathVariable Long classId, @PathVariable String teacherId) {
        classService.removeTeacherFromClass(teacherId, classId);
        return ApiResponse.<String>builder()
                .result("Remove teacher from class success")
                .build();
    }
}


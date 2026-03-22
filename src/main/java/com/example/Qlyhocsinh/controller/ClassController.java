package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ClassRequest;
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
    ClassResponse createClass(@RequestBody ClassRequest request){
        return classService.creatClass(request);
    }

    @PutMapping("/{id}")
    ClassResponse updateClass(@PathVariable Long id,@RequestBody ClassRequest request){
        return classService.updateClass(id, request);
    }

    @GetMapping
    List<ClassResponse> getAll(){
        return classService.getAllClass();
    }

    @DeleteMapping("/{id}")
    String deleteClass(@PathVariable Long id){
        classService.deletedClass(id);
        return "Class has been deleted !";
    }

    @PostMapping("/{clsId}/addstudent/{stuId}")
    StudentResponse addStuToClass(@PathVariable Long clsId,@PathVariable String stuId){
        return classService.addStudentToClass(stuId, clsId);
    }

    @GetMapping("{clsId}/students")
    List<StudentResponse> getStuInClass(@PathVariable Long clsId){
        return classService.getStuInClass(clsId);
    }

    @DeleteMapping("/{clsId}/removestudent/{stuId}")
    String removeStudentInClass(@PathVariable String stuId,@PathVariable Long clsId){
        classService.removeStudentFromClass(stuId, clsId);
        return "Student has been deleted from class";
    }

    @PostMapping("/{clsId}/tranferstudent/{stuId}")
    String transferStudent(@PathVariable String stuId,@PathVariable Long clsId){
        classService.transferStudent(stuId, clsId);
        return "Students successfully transferred classes";
    }

    @PostMapping("/{classId}/assign-teacher/{teacherId}")
    public String assignTeacher(@PathVariable Long classId, @PathVariable String teacherId){
        classService.addClassToTeacher(teacherId, classId);
        return "Teacher added in to class successfully";
    }

    @PostMapping("/{classId}/remove-teacher/{teacherId}")
    public String removeTeacher(@PathVariable Long classId, @PathVariable String teacherId){
        classService.removeTeacherFormClass(teacherId, classId);
        return "Remove teacher from class success";
    }

}


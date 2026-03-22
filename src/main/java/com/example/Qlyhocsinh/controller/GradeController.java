package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    GradeResponse createGrade(@RequestBody GradeRequest request){
        return gradeService.createGrade(request);
    }

    @PutMapping("/{id}")
    GradeResponse updateGrade(@PathVariable String id, @RequestBody GradeRequest request){
        return gradeService.updateGrade(id, request);
    }

    @GetMapping
    List<GradeResponse> getAll(){
        return gradeService.getAll();
    }

    @DeleteMapping("/{id}")
    String deleteGrade(@PathVariable String id){
        gradeService.deleteGrade(id);
        return "Delete grade success";
    }

}

package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.SubjectRequest;
import com.example.Qlyhocsinh.dto.response.SubjectResponse;
import com.example.Qlyhocsinh.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    SubjectResponse createSub(@RequestBody SubjectRequest request){
        return subjectService.createSub(request);
    }

    @PutMapping("/{id}")
    SubjectResponse updateSub(@PathVariable String id, @RequestBody SubjectRequest request){
        return subjectService.updateSub(id, request);
    }

    @GetMapping
    List<SubjectResponse> getAll(){
        return subjectService.getAll();
    }

    @DeleteMapping("/{id}")
    String deleteSub(@PathVariable String id){
        subjectService.deleteSub(id);
        return "Subject has been deleted";
    }


}

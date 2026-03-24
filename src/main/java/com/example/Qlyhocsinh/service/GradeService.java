package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.GradeRequest;
import com.example.Qlyhocsinh.dto.response.GradeResponse;
import com.example.Qlyhocsinh.entity.Grade;
import com.example.Qlyhocsinh.mapper.GradeMapper;
import com.example.Qlyhocsinh.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;

    public GradeResponse createGrade(GradeRequest request){
        Grade grade = gradeMapper.toGrade(request);
        return gradeMapper.toGradeResponse(gradeRepository.save(grade));
    }

    public GradeResponse updateGrade(String id, GradeRequest request){
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        gradeMapper.updateGrade(grade, request);
        return  gradeMapper.toGradeResponse(gradeRepository.save(grade));
    }

    public void deleteGrade(String id){
        gradeRepository.deleteById(id);
    }

    public List<GradeResponse> getAll(){
        return gradeMapper.toGradeResponseList(gradeRepository.findAll());
    }

}

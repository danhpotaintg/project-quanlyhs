package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.SubjectRequest;
import com.example.Qlyhocsinh.dto.response.SubjectResponse;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.mapper.SubjectMapper;
import com.example.Qlyhocsinh.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public SubjectResponse createSub(SubjectRequest request){
        Subject subject = subjectMapper.tosubject(request);
        subjectRepository.save(subject);
        return subjectMapper.toSubjectResponse(subject);
    }

    public SubjectResponse updateSub(String id, SubjectRequest request){
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Subject not found"));
        subjectMapper.updateSubject(subject, request);
        return subjectMapper.toSubjectResponse(subjectRepository.save(subject));
    }

    public List<SubjectResponse> getAll(){
        return subjectMapper.toSubjectResponseList(subjectRepository.findAll());
    }

    public void deleteSub(String id){
        subjectRepository.deleteById(id);
    }


}

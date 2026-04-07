package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.SubjectBulkRequest;
import com.example.Qlyhocsinh.dto.request.SubjectRequest;
import com.example.Qlyhocsinh.dto.response.SubjectResponse;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Subject;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.SubjectMapper;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.SubjectRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final StudentRepository studentRepository;

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


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public List<SubjectResponse> createBulk(SubjectBulkRequest request){
        List<String> existing = subjectRepository.findBySubjectNameIn(request.getSubjectName())
                .stream().map(Subject::getSubjectName).toList();

        List<String> duplicates = request.getSubjectName().stream()
                .filter(existing::contains)
                .toList();

//        if(!duplicates.isEmpty()){
//            throw new AppException(ErrorCode.SUBJECT_ALREADY_EXISTS);
//        }
//
        // tự động bỏ qua những môn trùng lặp
        List<Subject> subjects = request.getSubjectName().stream()
                .filter(name -> !existing.contains(name))
                .map(name -> {
                    Subject s = new Subject();
                    s.setSubjectName(name);
                    return s;
                })
                .toList();

        return subjectMapper.toSubjectResponseList(subjectRepository.saveAll(subjects));
    }

    // Danh sách các môn học trong 1 học kì của 1 học sinh
    public List<SubjectResponse> getSubjectsInSemester(String studentId, Integer semester){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        List<SubjectResponse> responses = new ArrayList<>();

        List<Subject> subjects = subjectRepository.findByClassIdAndSemester(student.getClassRoom().getId(), semester);
        for(Subject subject : subjects){
            SubjectResponse subjectResponse = SubjectResponse.builder()
                    .id(subject.getId())
                    .subjectName(subject.getSubjectName())
                    .build();
            responses.add(subjectResponse);
        }
        return responses;
    }



}

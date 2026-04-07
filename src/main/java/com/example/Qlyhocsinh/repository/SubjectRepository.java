package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {
    Optional<Subject> findBySubjectName(String subjectName);
    List<Subject> findBySubjectNameIn(List<String> subjectName);

}

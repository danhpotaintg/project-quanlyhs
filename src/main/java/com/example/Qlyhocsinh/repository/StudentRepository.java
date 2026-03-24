package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByClassRoomId(Long classId);
    Optional<Student> findByUserUsername(String name);
    Optional<Student> findByUserIdAndClassRoomId(String userId, Long classId);
}

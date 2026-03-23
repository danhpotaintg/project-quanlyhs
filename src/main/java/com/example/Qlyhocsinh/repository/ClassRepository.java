package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassRoom, Long> {
    boolean existsByTeacherUserId(String teacherId);
    Optional<ClassRoom> findByClassName(String className);
}

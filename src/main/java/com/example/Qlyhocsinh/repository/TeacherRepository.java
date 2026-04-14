package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    Optional<Teacher> findByUserUsername(String name);

    @Query("SELECT t FROM Teacher t WHERE t.user.isActive = true")
    List<Teacher> findAllActiveTeacher();
}

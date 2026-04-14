package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByClassRoomId(Long classId);
    Optional<Student> findByUserUsername(String name);
    Optional<Student> findByUserIdAndClassRoomId(String id, Long classId);

    @Query("SELECT s FROM Student s WHERE s.classRoom IS NULL AND s.user.isActive = true")
    List<Student> findAllWithoutClass();

    @Query("SELECT s FROM Student s WHERE s.classRoom.id = :classId AND s.user.isActive = true")
    List<Student> findAllActiveByClass(Long classId);
}

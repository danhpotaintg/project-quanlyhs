package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByStudentId(String studentId);

    @Query("SELECT lr FROM LeaveRequest lr " +
            "JOIN lr.student s " +
            "JOIN s.classRoom c " +
            "WHERE c.teacher.id = :teacherId")
    List<LeaveRequest> findRequestsByHomeroomTeacher(String teacherId);

}

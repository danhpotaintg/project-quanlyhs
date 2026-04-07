package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.IdSequence;
import com.example.Qlyhocsinh.entity.IdSequenceId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdSequenceRepository extends JpaRepository<IdSequence, IdSequenceId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<IdSequence> findByTypeAndPeriod(String type, String period);
}
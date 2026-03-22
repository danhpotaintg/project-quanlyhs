package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

}

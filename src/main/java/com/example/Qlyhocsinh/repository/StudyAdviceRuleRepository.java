package com.example.Qlyhocsinh.repository;

import com.example.Qlyhocsinh.entity.StudyAdviceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyAdviceRuleRepository extends JpaRepository<StudyAdviceRule, Long> {

    @Query(value = """
        SELECT * FROM study_advice_rule
        WHERE is_active = true
          AND min_score <= :score
          AND max_score >= :score
          AND (subject_id = :subjectId OR subject_id IS NULL)
        ORDER BY priority DESC
        LIMIT 1
    """, nativeQuery = true)
    Optional<StudyAdviceRule> findBestMatchRule(
            @Param("subjectId") String subjectId,
            @Param("score") Double score
    );
}
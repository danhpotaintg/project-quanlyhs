package com.example.Qlyhocsinh.dto.projection;

public interface GradeRawRow {
    String getStudentId();
    String getStudentName();
    Long getGradeConfigId();
    String getGradeConfigName(); // score_type: thuong_xuyen | giua_ky | cuoi_ky
    Integer getMaxEntries();
    Double getWeight();
    Integer getSemester();
    Integer getEntryIndex();
    Double getScore();
    String getSubjectId();
}
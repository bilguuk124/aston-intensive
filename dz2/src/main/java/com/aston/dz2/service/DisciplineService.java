package com.aston.dz2.service;

import com.aston.dz2.entity.Discipline;
import com.aston.dz2.entity.StudentDisciplineScore;
import com.aston.dz2.entity.dto.DisciplineDto;
import java.util.List;

public interface DisciplineService {

    void addDiscipline(DisciplineDto dto) throws Exception;
    void updateDiscipline(Long id, DisciplineDto dto) throws Exception;

    void deleteDiscipline(Long id) throws Exception;
    Discipline getById(Long id) throws Exception;
    List<Discipline> getAll() throws Exception;

    boolean existsById(Long id) throws Exception;
    StudentDisciplineScore getStudentScoreInDiscipline(Long studentId, Long disciplineId) throws Exception;
    List<StudentDisciplineScore> getScoresInDiscipline(Long disciplineId) throws Exception;
    List<StudentDisciplineScore> getStudentScores(Long studentId) throws Exception;
    void updateStudentScoreInDiscipline(Long studentId, Long disciplineId, int newScore) throws Exception;
    void addGroupToDiscipline(Long disciplineId, Long groupId) throws Exception;

    void deleteGroupFromDiscipline(Long disciplineId, Long groupId) throws Exception;
}

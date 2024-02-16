package com.aston.dz2.repository;

import com.aston.dz2.entity.StudentDisciplineScore;

import java.util.List;

public interface CrudBonusRepository <Discipline,Long> extends CrudRepository<Discipline, Long>{
    StudentDisciplineScore getDisciplineScoreByStudentId(Long disciplineId, Long studentId) throws Exception;
    List<StudentDisciplineScore> getAllScoresOfDiscipline(Long disciplineId) throws Exception;
    List<StudentDisciplineScore> getAllScoresOfStudent(Long studentId) throws Exception;
    void associateStudentWithDiscipline(Long studentId, Long disciplineId) throws Exception;
    void saveStudentScoreToDiscipline(Long disciplineId, Long studentId, int scoresToAdd) throws Exception;
    void dissociateStudentFromDiscipline(Long disciplineId, Long studentId) throws Exception;

}

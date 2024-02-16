package com.aston.dz2.service.impl;

import com.aston.dz2.entity.Discipline;
import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDisciplineScore;
import com.aston.dz2.entity.dto.DisciplineDto;
import com.aston.dz2.entity.enums.Semester;
import com.aston.dz2.exception.*;
import com.aston.dz2.repository.CrudBonusRepository;
import com.aston.dz2.service.DisciplineService;
import com.aston.dz2.service.GroupService;
import com.aston.dz2.service.TeacherService;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Default
@Stateless
@NoArgsConstructor
public class DisciplineServiceImpl implements DisciplineService {

    @Inject
    private TeacherService teacherService;
    @Inject
    @Named("disciplineRepository")
    private CrudBonusRepository<Discipline, Long> repository;
    @Inject
    private GroupService groupService;
    @Inject
    private Logger logger;

    public DisciplineServiceImpl(CrudBonusRepository<Discipline, Long> repository, TeacherService teacherService, GroupService groupService){
        this.repository = repository;
        this.teacherService = teacherService;
        this.groupService = groupService;
        logger = LoggerFactory.getLogger(DisciplineServiceImpl.class);
    }

    @Override
    public void addDiscipline(DisciplineDto dto) throws Exception {
        Discipline discipline = Discipline.builder()
                .name(dto.getName())
                .lecturer(teacherService.getTeacherById(dto.getLecturer_id()))
                .assistant(teacherService.getTeacherById(dto.getAssistant_id()))
                .semester(Semester.getByNumberValue(dto.getSemester()))
                .build();

        repository.save(discipline);
    }

    @Override
    public void updateDiscipline(Long id, DisciplineDto dto) throws Exception {
        if (!repository.existsById(id)){
            throw new DisciplineNotFoundException("Discipline with id " + id + " does not exist!");
        }
        Discipline discipline = Discipline.builder()
                .id(id)
                .name(dto.getName())
                .lecturer(teacherService.getTeacherById(dto.getLecturer_id()))
                .assistant(teacherService.getTeacherById(dto.getAssistant_id()))
                .semester(Semester.getByNumberValue(dto.getSemester()))
                .build();
        repository.save(discipline);
    }

    @Override
    public void deleteDiscipline(Long id) throws Exception {
        if (!repository.existsById(id)){
            throw new DisciplineNotFoundException("Discipline with id " + id + " does not exist!");
        }
        repository.deleteById(id);
    }

    @Override
    public Discipline getById(Long id) throws Exception {
        if (!repository.existsById(id)){
            throw new DisciplineNotFoundException("Discipline with id " + id + " does not exist!");
        }
        return repository.getById(id);
    }

    @Override
    public List<Discipline> getAll() throws Exception {
        logger.info("Service getting all disciplines");
        return repository.getAll();
    }

    @Override
    public boolean existsById(Long id) throws Exception {
        logger.info("Checking discipline with id " + id + " exists");
        return repository.existsById(id);
    }

    @Override
    public StudentDisciplineScore getStudentScoreInDiscipline(Long studentId, Long disciplineId) throws Exception {
        return repository.getDisciplineScoreByStudentId(disciplineId, studentId);
    }

    @Override
    public List<StudentDisciplineScore> getScoresInDiscipline(Long disciplineId) throws Exception {
        return repository.getAllScoresOfDiscipline(disciplineId);
    }

    @Override
    public List<StudentDisciplineScore> getStudentScores(Long studentId) throws Exception {
        return repository.getAllScoresOfStudent(studentId);
    }

    @Override
    public void updateStudentScoreInDiscipline(Long studentId, Long disciplineId, int newScore) throws Exception {
        int score;
        if (newScore > 100) score = 100;
        else score = Math.max(newScore, 0);
        repository.saveStudentScoreToDiscipline(disciplineId, studentId, score);
    }

    @Override
    public void addGroupToDiscipline(Long disciplineId, Long groupId) throws Exception{
        Group group = groupService.getById(groupId);
        for (Student student : group.getStudents()){
            repository.associateStudentWithDiscipline(student.getId(), disciplineId);
        }
    }

    @Override
    public void deleteGroupFromDiscipline(Long disciplineId, Long groupId) throws Exception{
        Group group = groupService.getById(groupId);
        for (Student student : group.getStudents()){
            repository.dissociateStudentFromDiscipline(disciplineId, student.getId());
        }
    }
}

package com.aston.dz2.repository.impl;

import com.aston.dz2.entity.*;
import com.aston.dz2.entity.enums.Semester;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.CrudBonusRepository;
import com.aston.dz2.service.GroupService;
import com.aston.dz2.service.StudentService;
import com.aston.dz2.service.TeacherService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Stateless
@Named("disciplineRepository")
@NoArgsConstructor
public class DisciplineRepository implements CrudBonusRepository<Discipline, Long> {

    @Inject
    private ConnectionBean databaseBean;
    @Inject
    private GroupService groupService;
    @Inject
    private TeacherService teacherService;
    @Inject
    private StudentService studentService;
    @Inject
    private Logger logger;

    public DisciplineRepository(GroupService groupService, TeacherService teacherService, StudentService studentService, ConnectionBean connectionBean){
        this.groupService = groupService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.databaseBean = connectionBean;
        logger = LoggerFactory.getLogger(DisciplineRepository.class);
    }

    private static final String DISCIPLINE_TABLE = "discipline";
    private static final String SCORE_TABLE = "studentdisciplinescore";
    private static final String CREATE_DISCIPLINE = "INSERT INTO " + DISCIPLINE_TABLE + " (name, lecturer_id, assistant_id, semester) VALUES (?,?,?,?)";
    private static final String UPDATE_DISCIPLINE_BY_ID = "UPDATE " + DISCIPLINE_TABLE + " SET name = ?, lecturer_id = ?, assistant_id = ?, semester = ? WHERE id = ?";
    private static final String SELECT_All_DISCIPLINE = "SELECT d.id AS discipline_id, d.name AS discipline_name," +
            " d.lecturer_id, d.assistant_id, d.semester, sds.score, sds.student_id" +
            " FROM " + DISCIPLINE_TABLE + " d LEFT JOIN " + SCORE_TABLE + " sds ON d.id = sds.discipline_id";
    private static final String SELECT_DISCIPLINE_BY_ID = SELECT_All_DISCIPLINE + " WHERE d.id = ?";
    private static final String DELETE_DISCIPLINE_BY_ID = "DELETE FROM " + DISCIPLINE_TABLE + " WHERE id = ?";
    private static final String SELECT_EXISTS = "SELECT COUNT(*) FROM " + DISCIPLINE_TABLE + " WHERE id = ? AND name = ? AND lecturer_id = ? AND assistant_id = ? AND semester = ?";
    private static final String SELECT_EXISTS_BY_ID = "SELECT COUNT(*) FROM " + DISCIPLINE_TABLE + " WHERE id = ?";
    private static final String SELECT_SCORE_BY_STUDENT_ID = "SELECT * FROM " + SCORE_TABLE + " WHERE student_id = ?";
    private static final String SELECT_SCORES_BY_DISCIPLINE = "SELECT * FROM " + SCORE_TABLE + " WHERE discipline_id = ?";
    private static final String SELECT_SCORES_BY_STUDENT_AND_DISCIPLINE = "SELECT * FROM " + SCORE_TABLE + " WHERE student_id = ? AND discipline_id = ?";
    private static final String ASSOCIATE_STUDENT_WITH_DISCIPLINE = "INSERT INTO " + SCORE_TABLE + " (discipline_id, student_id, score) VALUES (?,?,0)";
    private static final String DISASSOCIATE_STUDENT_FROM_DISCIPLINE = "DELETE FROM " + SCORE_TABLE + " WHERE discipline_id = ? AND student_id = ?";
    private static final String SAVE_STUDENT_SCORE = "UPDATE " + SCORE_TABLE + " SET score = ? WHERE student_id = ? AND discipline_id = ?";

    @Override
    public StudentDisciplineScore getDisciplineScoreByStudentId(Long disciplineId, Long studentId) throws Exception {
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SCORES_BY_STUDENT_AND_DISCIPLINE)) {
            preparedStatement.setLong(1, studentId);
            preparedStatement.setLong(2, disciplineId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return buildScoreFromResultSet(resultSet);
            }
        }
    }

    @Override
    public List<StudentDisciplineScore> getAllScoresOfDiscipline(Long disciplineId) throws Exception {
        List<StudentDisciplineScore> scores = new ArrayList<>();
        Connection connection = databaseBean.getConnection();
            try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SCORES_BY_DISCIPLINE)) {
            preparedStatement.setLong(1, disciplineId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    scores.add(buildScoreFromResultSet(resultSet));
                }
            }
        }
        return scores;
    }

    @Override
    public List<StudentDisciplineScore> getAllScoresOfStudent(Long studentId) throws Exception {
        List<StudentDisciplineScore> scores = new ArrayList<>();
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SCORE_BY_STUDENT_ID)) {
            preparedStatement.setLong(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()){
                    scores.add(buildScoreFromResultSet(resultSet));
                }
            }
        }
        return scores;
    }

    private StudentDisciplineScore buildScoreFromResultSet(ResultSet resultSet) throws Exception {
        StudentDisciplineScore score = new StudentDisciplineScore();
        score.setStudent(studentService.getStudentById(resultSet.getLong("student_id")));
        score.setDiscipline(getById(resultSet.getLong("discipline_id")));
        score.setScore(resultSet.getInt("score"));
        return score;
    }

    @Override
    public void associateStudentWithDiscipline(Long studentId, Long disciplineId) throws Exception {
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(ASSOCIATE_STUDENT_WITH_DISCIPLINE)) {
            preparedStatement.setLong(1, disciplineId);
            preparedStatement.setLong(2, studentId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void saveStudentScoreToDiscipline(Long disciplineId, Long studentId, int newScore) throws Exception {
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SAVE_STUDENT_SCORE)) {
            preparedStatement.setLong(1, newScore);
            preparedStatement.setLong(2, studentId);
            preparedStatement.setLong(3, disciplineId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void dissociateStudentFromDiscipline(Long disciplineId, Long studentId) throws Exception{
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(DISASSOCIATE_STUDENT_FROM_DISCIPLINE)){
            preparedStatement.setLong(1, disciplineId);
            preparedStatement.setLong(2, studentId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void save(Discipline element) throws Exception {
        Connection connection = databaseBean.getConnection();
         try(PreparedStatement preparedStatement = (element.getId() == null)
                 ? connection.prepareStatement(CREATE_DISCIPLINE)
                 : connection.prepareStatement(UPDATE_DISCIPLINE_BY_ID)) {
            preparedStatement.setString(1, element.getName());
            preparedStatement.setLong(2, element.getLecturer().getId());
            preparedStatement.setLong(3, element.getAssistant().getId());
            preparedStatement.setString(4, element.getSemester().name());

            if (element.getId() != null) {
                preparedStatement.setLong(5, element.getId());
                if (element.getGroups() != null && !element.getGroups().isEmpty()) {
                    for (Group group : element.getGroups()) {
                        for (Student student : group.getStudents()) {
                            associateStudentWithDiscipline(student.getId(), element.getId());
                        }
                    }
                }
            }
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean exists(Discipline element) throws Exception {
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EXISTS)) {
            preparedStatement.setLong(1, element.getId());
            preparedStatement.setString(2, element.getName());
            preparedStatement.setLong(3, element.getLecturer().getId());
            preparedStatement.setLong(4, element.getAssistant().getId());
            preparedStatement.setString(5, element.getSemester().getStringValue());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean existsById(Long id) throws Exception {
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EXISTS_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public Discipline getById(Long id) throws Exception {
        Discipline discipline = null;
        List<StudentDisciplineScore> scores = new ArrayList<>();
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DISCIPLINE_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (discipline == null) {
                        discipline = new Discipline();
                        long lecturerId = resultSet.getLong("lecturer_id");
                        long assistantId = resultSet.getLong("assistant_id");
                        discipline.setId(resultSet.getLong("discipline_id"));
                        discipline.setName(resultSet.getString("discipline_name"));
                        discipline.setLecturer(lecturerId == 0 ? null : teacherService.getTeacherById(lecturerId));
                        discipline.setAssistant(assistantId == 0 ? null : teacherService.getTeacherById(assistantId));
                        discipline.setSemester(Semester.getByStringValue(resultSet.getString("semester")));
                        discipline.setGroups(groupService.getByDiscipline(discipline.getId()));
                    }

                    long studentId = resultSet.getLong("student_id");
                    if (studentId != 0){
                        StudentDisciplineScore score = new StudentDisciplineScore();
                        score.setDiscipline(discipline);
                        score.setStudent(studentService.getStudentById(studentId));
                        score.setScore(resultSet.getInt("score"));
                        scores.add(score);
                    }
                }

                if (discipline != null) {
                    discipline.setScores(scores);
                }
            }
        }
        return discipline;
    }

    @Override
    public List<Discipline> getAll() throws Exception {
        Connection connection = databaseBean.getConnection();
        logger.info("Discipline repository method getAll()");
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_All_DISCIPLINE);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Map<Long, Discipline> disciplineMap = new HashMap<>();
            while (resultSet.next()) {
                Long disciplineId = resultSet.getLong("discipline_id");
                long lecturerId = resultSet.getLong("lecturer_id");
                long assistantId = resultSet.getLong("assistant_id");
                if (!disciplineMap.containsKey(disciplineId)) {
                    Set<Group> groups = groupService.getByDiscipline(disciplineId);
                    Discipline discipline = new Discipline();
                    discipline.setId(disciplineId);
                    discipline.setName(resultSet.getString("discipline_name"));
                    discipline.setLecturer(lecturerId == 0 ? null : teacherService.getTeacherById(lecturerId));
                    discipline.setAssistant(assistantId == 0 ? null : teacherService.getTeacherById(assistantId));
                    discipline.setSemester(Semester.getByStringValue(resultSet.getString("semester")));
                    discipline.setGroups(groups);
                    discipline.setScores(new ArrayList<>());
                    disciplineMap.put(disciplineId, discipline);
                }
                long studentId = resultSet.getLong("student_id");
                if (studentId != 0){
                    Discipline discipline = disciplineMap.get(disciplineId);
                    StudentDisciplineScore score = new StudentDisciplineScore();
                    score.setDiscipline(discipline);
                    score.setStudent(studentService.getStudentById(studentId));
                    score.setScore(resultSet.getInt("score"));
                    discipline.getScores().add(score);
                }
            }

            return new ArrayList<>(disciplineMap.values());
        }
    }

    @Override
    public void deleteById(Long id) throws Exception {
         Connection connection = databaseBean.getConnection();
         try(PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DISCIPLINE_BY_ID)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }
}

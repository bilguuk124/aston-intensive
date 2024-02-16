package com.aston.dz2.repository.impl;

import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.enums.Level;
import com.aston.dz2.exception.GroupDoesNotExistsException;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.CrudRepository;
import com.aston.dz2.service.DepartmentService;
import com.aston.dz2.service.GroupService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Named("studentRepository")
@NoArgsConstructor
public class StudentRepository implements CrudRepository<Student, Long> {

    @Inject
    private ConnectionBean database;
    @Inject
    private DepartmentService departmentService;
    @Inject
    private GroupService groupService;
    @Inject
    private Logger  logger;

    public StudentRepository(ConnectionBean connectionBean, DepartmentService departmentService, GroupService groupService, Logger logger){
        this.database = connectionBean;
        this.departmentService = departmentService;
        this.groupService = groupService;
        this.logger = logger;
    }


    private static final String INSERT_QUERY = "INSERT INTO student (firstname, lastname, department_id, level, group_id) VALUES (?,?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE student SET firstName = ?, lastname = ?, department_id = ?, level = ?, group_id = ? WHERE id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM student";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM student WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM student WHERE id = ?";
    private static final String EXISTS_QUERY = "SELECT COUNT(*) FROM student WHERE id = ? AND firstname = ? AND lastname = ? AND level = ? AND department_id = ? AND group_id = ?";
    private static final String EXISTS_BY_ID_QUERY = "SELECT COUNT(*) FROM student WHERE id = ?";

    @Override
    public void save(Student student) throws Exception {
        logger.info("Repository save method");
        Connection connection = database.getConnection();
        try (PreparedStatement preparedStatement = (student.getId() == null)
                ? connection.prepareStatement(INSERT_QUERY)
                : connection.prepareStatement(UPDATE_QUERY)) {

            Level.getByStringValue(student.getLevel().getStringValue());
            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setLong(3, student.getDepartment().getId());
            preparedStatement.setString(4, student.getLevel().getStringValue());

            if (student.getGroup() == null){
                preparedStatement.setNull(5, Types.INTEGER);
            } else{
                preparedStatement.setLong(5, student.getGroup().getId());
            }

            if (student.getId() != null) {
                preparedStatement.setLong(6, student.getId());
            }

            preparedStatement.executeUpdate();
        }

    }

    @Override
    public List<Student> getAll() throws Exception {
        logger.info("Repository get all method");
        List<Student> students = new ArrayList<>();
        Connection connection = database.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Student student = buildStudentFromResultSet(resultSet);
                students.add(student);
            }
        }
        return students;
    }

    @Override
    public Student getById(Long id) throws Exception {
        logger.info("Repository get by id method");
        Student student = null;
        Connection connection = database.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    student = buildStudentFromResultSet(resultSet);
                }
            }
        }
        return student;
    }

    @Override
    public void deleteById(Long id) throws Exception {
        logger.info("Repository delete by id method");
        Connection connection = database.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean exists(Student student) throws Exception {
        logger.info("Repository exists method");
        Connection connection = database.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_QUERY)) {
            preparedStatement.setLong(1, student.getId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.setString(4, student.getLevel().getStringValue());
            preparedStatement.setLong(5, student.getDepartment().getId());
            preparedStatement.setLong(6, student.getGroup().getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }

    }

    @Override
    public boolean existsById(Long id) throws Exception {
        logger.info("Repository exists by id method");
        Connection connection = database.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_BY_ID_QUERY)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next() && resultSet.getInt(1) > 0;
                }
            }
    }

    private Student buildStudentFromResultSet(ResultSet resultSet) throws Exception {
        Group group;
        try{
            group = groupService.getById(resultSet.getLong("group_id"));
        }
        catch (GroupDoesNotExistsException e){
            group = null;
        }
        long departmentId = (resultSet.getLong("department_id"));
        return Student.builder()
                .id(resultSet.getLong("id"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .level(Level.getByStringValue(resultSet.getString("level")))
                .department(departmentId == 0 ? null : departmentService.getById(departmentId))
                .group(group)
                .build();
    }

}

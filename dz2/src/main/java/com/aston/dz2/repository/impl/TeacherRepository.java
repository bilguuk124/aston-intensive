package com.aston.dz2.repository.impl;

import com.aston.dz2.entity.Teacher;
import com.aston.dz2.entity.enums.TeacherRank;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.CrudRepository;
import com.aston.dz2.service.DepartmentService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Named("teacherRepository")
@NoArgsConstructor
public class TeacherRepository implements CrudRepository<Teacher, Long> {

    @Inject
    private ConnectionBean databaseBean;
    @Inject
    private DepartmentService departmentService;
    private final String tableName = "teacher";

    private static final String INSERT_QUERY = "INSERT INTO teacher (firstname, lastname, department_id, rank) VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE teacher SET firstName = ?, lastname = ?, department_id = ?, rank = ? WHERE id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM teacher";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM teacher WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM teacher WHERE id = ?";
    private static final String EXISTS_QUERY = "SELECT COUNT(*) FROM teacher WHERE id = ? AND firstname = ? AND lastname = ? AND department_id = ? AND rank = ?";
    private static final String EXISTS_BY_ID_QUERY = "SELECT COUNT(*) FROM teacher WHERE id = ?";

    public TeacherRepository(ConnectionBean connectionBean, DepartmentService departmentService){
        this.databaseBean = connectionBean;
        this.departmentService = departmentService;
    }

    @Override
    public void save(Teacher teacher) throws Exception {
        Connection connection = databaseBean.getConnection();
            try (PreparedStatement preparedStatement = (teacher.getId() == null)
                    ? connection.prepareStatement(INSERT_QUERY)
                    : connection.prepareStatement(UPDATE_QUERY)) {

                preparedStatement.setString(1, teacher.getFirstName());
                preparedStatement.setString(2, teacher.getLastName());
                preparedStatement.setLong(3, teacher.getDepartment().getId());
                preparedStatement.setString(4, teacher.getRank().getStringValue());

                if (teacher.getId() != null) {
                    preparedStatement.setLong(5, teacher.getId());
                }

                preparedStatement.executeUpdate();
            }
    }

    @Override
    public boolean exists(Teacher teacher) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_QUERY)) {
            preparedStatement.setLong(1, teacher.getId());
            preparedStatement.setString(2, teacher.getFirstName());
            preparedStatement.setString(3, teacher.getLastName());
            preparedStatement.setLong(4, teacher.getDepartment().getId());
            preparedStatement.setString(5, teacher.getRank().getStringValue());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean existsById(Long id) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public Teacher getById(Long id) throws Exception {
        Teacher teacher = null;
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    teacher = buildTeacherFromResultSet(resultSet);
                }
            }
        }
        return teacher;
    }

    @Override
    public List<Teacher> getAll() throws Exception {
        List<Teacher> teachers = new ArrayList<>();
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Teacher teacher = buildTeacherFromResultSet(resultSet);
                teachers.add(teacher);
            }
        }
        return teachers;
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private Teacher buildTeacherFromResultSet(ResultSet resultSet) throws Exception {
        long departmentId = resultSet.getLong("department_id");
        return Teacher.builder()
                .id(resultSet.getLong("id"))
                .firstName(resultSet.getString("firstname"))
                .lastName(resultSet.getString("lastname"))
                .rank(TeacherRank.getByStringValue(resultSet.getString("rank")))
                .department(departmentId == 0 ? null : departmentService.getById(departmentId))
                .build();
    }
}

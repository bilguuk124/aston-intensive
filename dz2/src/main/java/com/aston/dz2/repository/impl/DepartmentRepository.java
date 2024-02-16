package com.aston.dz2.repository.impl;

import com.aston.dz2.entity.Department;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.CrudRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Named("departmentRepository")
@NoArgsConstructor
public class DepartmentRepository implements CrudRepository<Department,Long> {

    @Inject
    private ConnectionBean databaseBean;
    private static final String tableName = "department";

    private static final String INSERT_QUERY = "INSERT INTO " + tableName + " (name, shortname, description) VALUES (?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE " + tableName + " SET name = ?, shortname = ?, description = ? WHERE id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM " + tableName;
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM " + tableName + " WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM " + tableName + " WHERE id = ?";
    private static final String EXISTS_QUERY = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ? AND name = ? AND shortname = ? AND description = ?";
    private static final String EXISTS_BY_ID_QUERY = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";

    public DepartmentRepository(ConnectionBean connectionBean){
        this.databaseBean = connectionBean;
    }

    @Override
    public void save(Department element) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = (element.getId() == null)
                ? connection.prepareStatement(INSERT_QUERY)
                : connection.prepareStatement(UPDATE_QUERY)) {

            preparedStatement.setString(1, element.getName());
            preparedStatement.setString(2, element.getShortName());
            preparedStatement.setString(3, element.getDescription());

            if (element.getId() != null) {
                preparedStatement.setLong(4, element.getId());
            }

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean exists(Department element) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_QUERY)) {
            preparedStatement.setLong(1, element.getId());
            preparedStatement.setString(2, element.getName());
            preparedStatement.setString(3, element.getShortName());
            preparedStatement.setString(4, element.getDescription());
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
    public Department getById(Long id) throws Exception {
        Department department = null;
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    department = buildDepartmentFromResultSet(resultSet);
                }
                return department;
            }
        }
    }

    @Override
    public List<Department> getAll() throws Exception {
        List<Department> departments = new ArrayList<>();
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Department department = buildDepartmentFromResultSet(resultSet);
                departments.add(department);
            }
        }
        return departments;
    }

    @Override
    public void deleteById(Long id) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private Department buildDepartmentFromResultSet(ResultSet set) throws Exception {
        return Department.builder()
                .id(set.getLong("id"))
                .name(set.getString("name"))
                .shortName(set.getString("shortname"))
                .description(set.getString("description"))
                .build();
    }
}

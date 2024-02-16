package com.aston.dz2.repository.impl;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.enums.Level;
import com.aston.dz2.exception.GroupDoesNotExistsException;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.CrudGroupRepository;
import com.aston.dz2.service.DepartmentService;
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
@Named("groupRepository")
@NoArgsConstructor
public class GroupRepository implements CrudGroupRepository<Group, Long> {

    @Inject
    private ConnectionBean databaseBean;
    @Inject
    private DepartmentService departmentService;
    @Inject
    private Logger logger;

    public GroupRepository(ConnectionBean connectionBean, DepartmentService departmentService){
        this.databaseBean = connectionBean;
        this.departmentService = departmentService;
        logger = LoggerFactory.getLogger(GroupRepository.class);
    }

    private static final String GROUP_TABLE = "_group";

    private static final String INSERT_QUERY =
            "INSERT INTO " + GROUP_TABLE + " (name, department_id) VALUES (?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE " + GROUP_TABLE + " SET name = ?, department_id = ? WHERE id = ?";

    private static final String SELECT_EXISTS_QUERY =
            "SELECT COUNT(*) FROM " + GROUP_TABLE + " WHERE id = ? AND name = ? AND department_id = ?";

    private static final String SELECT_EXISTS_BY_ID_QUERY =
            "SELECT COUNT(*) FROM " + GROUP_TABLE + " WHERE id = ?";

    private static final String SELECT_GROUP_BY_ID_QUERY =
            "SELECT g.id AS group_id, g.name AS group_name, g.department_id AS department_id" +
                    " FROM " + GROUP_TABLE + " g " +
                    " WHERE g.id = ?";

    private static final String SELECT_ALL_GROUPS_QUERY =
            "SELECT g.id AS group_id, g.name AS group_name, g.department_id AS department_id" +
                    " FROM " + GROUP_TABLE + " g";

    private static final String DELETE_BY_ID_QUERY =
            "DELETE FROM " + GROUP_TABLE + " WHERE id = ?";

    private static final String SELECT_GROUPS_BY_DISCIPLINE = "SELECT g.id AS group_id, g.name AS group_name, g.department_id AS department_id" +
            " FROM " + GROUP_TABLE + " g " +
            " LEFT JOIN student s ON g.id = s.group_id" +
            " LEFT JOIN studentdisciplinescore sc ON s.id = sc.student_id" +
            " WHERE discipline_id = ?" +
            " GROUP BY g.id";

    private static final String SELECT_STUDENTS_BY_GROUP_ID = "SELECT g.id AS group_id, g.name AS group_name," +
            "d.id AS department_id, d.name AS department_name, d.shortname AS department_shortname, d.description AS department_description," +
            "s.id AS student_id, s.firstname AS student_fname, s.lastname AS student_lname, s.level AS level" +
            " FROM " + GROUP_TABLE + " g " +
            "LEFT JOIN student s ON g.id = s.group_id " +
            "LEFT JOIN department d ON g.department_id = d.id " +
            "WHERE g.id = ?";

    @Override
    public void save(Group element) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = (element.getId() == null)
                ? connection.prepareStatement(INSERT_QUERY)
                : connection.prepareStatement(UPDATE_QUERY)) {
            preparedStatement.setString(1, element.getName());
            preparedStatement.setLong(2, element.getDepartment().getId());
            if (element.getId() != null) {
                preparedStatement.setLong(3, element.getId());
            }
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean exists(Group element) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EXISTS_QUERY)) {
            preparedStatement.setLong(1, element.getId());
            preparedStatement.setString(2, element.getName());
            preparedStatement.setLong(3, element.getDepartment().getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean existsById(Long id) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EXISTS_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public Group getById(Long id) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUP_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Group group;
                if(resultSet.next()){
                    group = new Group();
                    group.setId(resultSet.getLong("group_id"));
                    group.setName(resultSet.getString("group_name"));
                    group.setDepartment(departmentService.getById(resultSet.getLong("department_id")));
                    group.setStudents(getStudentsByGroup(group));
                    return group;
                }else{
                    throw new GroupDoesNotExistsException("Group with id " + id + " was not found");
                }
            }
        }
    }

    @Override
    public List<Group> getAll() throws Exception {
        logger.info("Repository method get all!");
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GROUPS_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Group> groups = new ArrayList<>();
            while (resultSet.next()) {
                long departmentId = resultSet.getLong("department_id");
                Department department = departmentId == 0 ? null : departmentService.getById(departmentId);
                String groupName = resultSet.getString("group_name");
                Group group = new Group(resultSet.getLong("group_id"), groupName, department);
                group.setStudents(getStudentsByGroup(group));
                groups.add(group);
            }
            return groups;
        }
    }

    @Override
    public void deleteById(Long id) throws Exception {
        Connection connection = databaseBean.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Set<Group> getByDiscipline(Long disciplineId) throws Exception{
        logger.info("Getting groups by discipline");
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUPS_BY_DISCIPLINE)){
            Set<Group> groups = new HashSet<>();
            preparedStatement.setLong(1, disciplineId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    Group group = new Group();
                    group.setId(resultSet.getLong("group_id"));
                    group.setName(resultSet.getString("group_name"));
                    group.setDepartment(departmentService.getById(resultSet.getLong("department_id")));
                    group.setStudents(getStudentsByGroup(group));
                    groups.add(group);
                }
            return groups;
            }
        }
    }

    private Set<Student> getStudentsByGroup(Group group) throws Exception {
        logger.info("Getting student for group: {}", group);
        Connection connection = databaseBean.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENTS_BY_GROUP_ID)){
            preparedStatement.setLong(1, group.getId());
            Set<Student> students = new HashSet<>();
            Department department = null;
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    if (department == null){
                        department = new Department();
                        department.setId(resultSet.getLong("department_id"));
                        department.setName(resultSet.getString("department_name"));
                        department.setShortName(resultSet.getString("department_shortname"));
                        department.setDescription(resultSet.getString("department_description"));
                    }
                    long studentId = (resultSet.getLong("student_id"));

                    if (studentId > 0){
                        Student student = new Student();
                        student.setId(studentId);
                        student.setFirstName(resultSet.getString("student_fname"));
                        student.setLastName(resultSet.getString("student_lname"));
                        student.setLevel(Level.getByStringValue(resultSet.getString("level")));
                        student.setDepartment(department);
                        student.setGroup(group);
                        students.add(student);
                    }
                }
            }
            return students;
        }
    }
}

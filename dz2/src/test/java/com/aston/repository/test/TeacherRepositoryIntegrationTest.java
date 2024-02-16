package com.aston.repository.test;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.Teacher;
import com.aston.dz2.entity.enums.TeacherRank;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.impl.TeacherRepository;
import com.aston.dz2.service.DepartmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class TeacherRepositoryIntegrationTest {
    private static final ConnectionBean connectionBean = new ConnectionBean("jdbc:postgresql://localhost:8888/testdb");
    @Mock
    private static DepartmentService departmentService;
    private TeacherRepository repository;
    private static final Department department = new Department(1L, "Department of Computer Science", "DCS", "Coder for life");

    @BeforeEach
    public void setUp(){
        openMocks(this);
        connectionBean.initialize();
        repository = new TeacherRepository(connectionBean, departmentService);
    }

    @AfterEach
    public void cleanUp(){
        connectionBean.closeConnection();
    }

    @Test
    public void createTeacher() throws Exception {
        when(departmentService.getById(1L)).thenReturn(department);
        Teacher teacher = Teacher.builder()
                .firstName("Max")
                .lastName("Kuznetsov")
                .rank(TeacherRank.DISTINGUISHED)
                .department(department)
                .build();
        repository.save(teacher);
        Teacher result = repository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertAll(() -> {
            assertNotNull(result);
            assertEquals(teacher.getFirstName(), result.getFirstName());
            assertEquals(teacher.getLastName(), result.getLastName());
            assertEquals(teacher.getRank(), result.getRank());
            assertEquals(teacher.getDepartment(), result.getDepartment());
        });
        repository.deleteById(result.getId());
    }

    @Test
    public void updateTeacher() throws Exception{
        when(departmentService.getById(1L)).thenReturn(department);
        Teacher teacher = Teacher.builder()
                .firstName("Max")
                .lastName("Kuznetsov")
                .rank(TeacherRank.DISTINGUISHED)
                .department(department)
                .build();
        repository.save(teacher);
        teacher = repository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(teacher);
        teacher.setFirstName("Frank");
        teacher.setLastName("Sinatra");
        teacher.setRank(TeacherRank.ASSOCIATE);
        repository.save(teacher);
        Teacher result = repository.getById(teacher.getId());
        Teacher finalTeacher = teacher;
        assertAll(() -> {
            assertNotNull(result);
            assertEquals(finalTeacher.getId(), result.getId());
            assertEquals(finalTeacher.getFirstName(), result.getFirstName());
            assertEquals(finalTeacher.getLastName(), result.getLastName());
            assertEquals(finalTeacher.getRank(), result.getRank());
            assertEquals(finalTeacher.getDepartment(), result.getDepartment());
        });
        repository.deleteById(teacher.getId());
    }
    @Test
    public void readTeachers() throws Exception {
        when(departmentService.getById(1L)).thenReturn(department);
        Teacher teacher1 = Teacher.builder()
                .firstName("Max")
                .lastName("Kuznetsov")
                .rank(TeacherRank.DISTINGUISHED)
                .department(department)
                .build();
        Teacher teacher2 = Teacher.builder()
                .firstName("Frank")
                .lastName("Sinatra")
                .rank(TeacherRank.ASSOCIATE)
                .department(department)
                .build();
        repository.save(teacher1);
        repository.save(teacher2);
        List<Teacher> allTeachers = repository.getAll();
        List<Teacher> twoTeachers = allTeachers.stream().skip(Math.max(0, allTeachers.size() - 2)).toList();

        assertAll(() -> {
            assertNotNull(twoTeachers.get(0));
            assertEquals(teacher1.getFirstName(), twoTeachers.get(0).getFirstName());
            assertEquals(teacher1.getLastName(), twoTeachers.get(0).getLastName());
            assertEquals(teacher1.getRank(), twoTeachers.get(0).getRank());
            assertEquals(teacher1.getDepartment(), twoTeachers.get(0).getDepartment());
        });

        assertAll(() -> {
            assertNotNull(twoTeachers.get(1));
            assertEquals(teacher2.getFirstName(), twoTeachers.get(1).getFirstName());
            assertEquals(teacher2.getLastName(), twoTeachers.get(1).getLastName());
            assertEquals(teacher2.getRank(), twoTeachers.get(1).getRank());
            assertEquals(teacher2.getDepartment(), twoTeachers.get(1).getDepartment());
        });

        repository.deleteById(twoTeachers.get(0).getId());
        repository.deleteById(twoTeachers.get(1).getId());
    }
    @Test
    public void deleteTeacher() throws Exception {
        when(departmentService.getById(anyLong())).thenReturn(department);
        Teacher teacher1 = Teacher.builder()
                .firstName("Max")
                .lastName("Kuznetsov")
                .rank(TeacherRank.DISTINGUISHED)
                .department(department)
                .build();
        repository.save(teacher1);
        Teacher result = repository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(result);
        repository.deleteById(result.getId());
        Teacher result1 = repository.getById(result.getId());
        assertNull(result1);
    }
}

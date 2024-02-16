package com.aston.repository.test;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.enums.Level;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.impl.StudentRepository;
import com.aston.dz2.service.DepartmentService;
import com.aston.dz2.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;


public class StudentRepositoryIntegrationTest {


    @Mock
    private static GroupService groupService;
    @Mock
    private static DepartmentService departmentService;
    private static final Logger logger = LoggerFactory.getLogger(StudentRepositoryIntegrationTest.class);
    private static final ConnectionBean connectionBean = new ConnectionBean("jdbc:postgresql://localhost:8888/testdb");
    private StudentRepository studentRepository;
    private static final Department department = new Department(1L,"School of Mathematics", "SOM", "Math is life");



    @BeforeEach
    public void setUp(){
        openMocks(this);
        connectionBean.initialize();
        studentRepository = new StudentRepository(connectionBean, departmentService, groupService, logger);
    }

    @BeforeEach
    public void clean(){
        connectionBean.closeConnection();
    }

    @Test
    public void testAddStudent() throws Exception {
        when(departmentService.getById(1L)).thenReturn(department);
        Student student = Student
                .builder()
                .firstName("test")
                .lastName("test")
                .level(Level.BACHELOR)
                .department(department)
                .build();
        studentRepository.save(student);
        Student result = studentRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertAll(() -> {
                    assertNotNull(result);
                    assertEquals(student.getFirstName(), result.getFirstName());
                    assertEquals(student.getLastName(), result.getLastName());
                    assertEquals(student.getLevel(), result.getLevel());
                    assertEquals(student.getDepartment(), result.getDepartment());
                }
        );
    }

    @Test
    public void testUpdateStudent() throws Exception{
        when(departmentService.getById(1L)).thenReturn(department);
        Student student = Student
                .builder()
                .id(1L)
                .firstName("test1")
                .lastName("test1")
                .level(Level.MASTER)
                .department(department)
                .build();
        studentRepository.save(student);
        Student result = studentRepository.getById(1L);
        assertAll(() -> {
                    assertNotNull(result);
                    assertEquals(student.getId(), result.getId());
                    assertEquals(student.getFirstName(), result.getFirstName());
                    assertEquals(student.getLastName(), result.getLastName());
                    assertEquals(student.getLevel(), result.getLevel());
                    assertEquals(student.getDepartment(), result.getDepartment());
                }
        );
    }

    @Test
    public void getAll() throws Exception{
        Student student = Student
                .builder()
                .firstName("test1")
                .lastName("test1")
                .level(Level.BACHELOR)
                .department(department)
                .build();
        Student student1 = Student
                .builder()
                .firstName("test2")
                .lastName("test2")
                .level(Level.BACHELOR)
                .department(department)
                .build();
        studentRepository.save(student);
        studentRepository.save(student1);
        List<Student> studentList = studentRepository.getAll();
        List<Student> result = studentList.stream().skip(Math.max(0, studentList.size() - 2)).toList();

        assertAll(()-> {
            assertNotNull(result.get(0));
            assertEquals(student.getFirstName(), result.get(0).getFirstName());
            assertEquals(student.getLastName(), result.get(0).getLastName());
            assertEquals(student.getLevel(), result.get(0).getLevel());
            assertEquals(student.getDepartment(), result.get(0).getDepartment());
        });

        assertAll(()-> {
            assertNotNull(result.get(1));
            assertEquals(student1.getFirstName(), result.get(1).getFirstName());
            assertEquals(student1.getLastName(), result.get(1).getLastName());
            assertEquals(student1.getLevel(), result.get(1).getLevel());
            assertEquals(student1.getDepartment(), result.get(1).getDepartment());
        });
    }

    @Test
    public void deleteStudent() throws Exception {
        Student student = Student
                .builder()
                .firstName("test1")
                .lastName("test1")
                .level(Level.BACHELOR)
                .department(department)
                .build();
        studentRepository.save(student);
        Student lastStudent = studentRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(lastStudent);
        assertNotNull(lastStudent.getId());
        long id = lastStudent.getId();
        studentRepository.deleteById(id);
        assertNull(studentRepository.getById(id));
    }


}

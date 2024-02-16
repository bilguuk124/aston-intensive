package com.aston.repository.test;

import com.aston.dz2.entity.Department;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.impl.DepartmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentRepositoryIntegrationTest {

    private final ConnectionBean connectionBean = new ConnectionBean("jdbc:postgresql://localhost:8888/testdb");
    private final DepartmentRepository repository = new DepartmentRepository(connectionBean);

    @BeforeEach
    public void setUp(){
        connectionBean.initialize();
    }

    @AfterEach
    public void cleanUp(){
        connectionBean.closeConnection();
    }

    @Test
    public void createDepartment() throws Exception {
        Department department = Department.builder()
                .name("School of Mathematics")
                .shortName("SOM")
                .description("Math is life")
                .build();
        repository.save(department);

        Department result = repository.getAll().stream().reduce((first,second) -> second).orElse(null);
        assertAll(()->{
            assertNotNull(result);
            assertEquals(department.getName(), result.getName());
            assertEquals(department.getShortName(), result.getShortName());
            assertEquals(department.getDescription(), result.getDescription());
        });
    }

    @Test
    public void updateDepartment() throws Exception {
        Department department = Department.builder()
                .id(1L)
                .name("School of Engineering")
                .shortName("SOE")
                .description("Engineering is life")
                .build();
        repository.save(department);
        Department result = repository.getById(1L);
        assertAll(()->{
            assertNotNull(result);
            assertEquals(department.getId(), result.getId());
            assertEquals(department.getName(), result.getName());
            assertEquals(department.getShortName(), result.getShortName());
            assertEquals(department.getDescription(), result.getDescription());
        });
    }

    @Test
    public void readDepartments() throws Exception{
        Department department1 = Department.builder()
                .name("School of Engineering")
                .shortName("SOE")
                .description("Engineering is life")
                .build();
        Department department2 = Department.builder()
                .name("School of Mathematics")
                .shortName("SOM")
                .description("Math is life")
                .build();
        repository.save(department1);
        repository.save(department2);
        List<Department> departmentList = repository.getAll();
        List<Department> result = departmentList.stream().skip(Math.max(0, departmentList.size()-2)).toList();

        assertAll(()->{
            assertNotNull(result.get(0));
            assertEquals(department1.getName(), result.get(0).getName());
            assertEquals(department1.getShortName(), result.get(0).getShortName());
            assertEquals(department1.getDescription(), result.get(0).getDescription());
        });

        assertAll(()->{
            assertNotNull(result.get(1));
            assertEquals(department2.getName(), result.get(1).getName());
            assertEquals(department2.getShortName(), result.get(1).getShortName());
            assertEquals(department2.getDescription(), result.get(1).getDescription());
        });
    }

    @Test
    public void deleteDepartment() throws Exception {
        Department department1 = Department.builder()
                .name("School of Engineering")
                .shortName("SOE")
                .description("Engineering is life")
                .build();
        repository.save(department1);
        Department result = repository.getAll().stream().reduce((first,second) -> second).orElse(null);
        assertNotNull(result);
        repository.deleteById(result.getId());
        assertNull(repository.getById(result.getId()));
    }
}

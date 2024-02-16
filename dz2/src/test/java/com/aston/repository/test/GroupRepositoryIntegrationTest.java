package com.aston.repository.test;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.Group;
import com.aston.dz2.exception.GroupDoesNotExistsException;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.impl.GroupRepository;
import com.aston.dz2.service.DepartmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class GroupRepositoryIntegrationTest {

    private final ConnectionBean connectionBean = new ConnectionBean("jdbc:postgresql://localhost:8888/testdb");
    @Mock
    private DepartmentService departmentService;
    private final Department department = new Department(1L, "School of cooking", "SoC", "If food is life, then the chef is lifeguard");
    private final Department anotherDepartment = new Department(2L, "School of coding", "Soc", "If code is life, then the programmer is god");

    private GroupRepository repository;

    @BeforeEach
    public void setUp(){
        openMocks(this);
        connectionBean.initialize();
        repository = new GroupRepository(connectionBean, departmentService);
    }

    @AfterEach
    public void cleanUp(){
        connectionBean.closeConnection();
    }

    @Test
    public void createGroup() throws Exception{
        when(departmentService.getById(1L)).thenReturn(department);
        Group group = Group.builder()
                .name("P34141")
                .department(department)
                .build();
        repository.save(group);
        Group result = repository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertAll(() -> {
           assertNotNull(result);
           assertEquals(group.getName(), result.getName());
           assertEquals(group.getDepartment(), result.getDepartment());
        });
        repository.deleteById(result.getId());
    }

    @Test
    public void updateGroup() throws Exception{
        when(departmentService.getById(1L)).thenReturn(department);
        when(departmentService.getById(2L)).thenReturn(anotherDepartment);
        Group group = Group.builder()
                .name("P34141")
                .department(department)
                .build();
        repository.save(group);
        group = repository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(group);
        group.setName("P34121");
        group.setDepartment(anotherDepartment);
        repository.save(group);
        Group result = repository.getById(group.getId());
        Group finalGroup = group;
        assertAll(() -> {
            assertNotNull(result);
            assertEquals(finalGroup.getId(), result.getId());
            assertEquals(finalGroup.getName(), result.getName());
            assertEquals(finalGroup.getDepartment(), result.getDepartment());
        });
        repository.deleteById(result.getId());
    }

    @Test
    public void readGroups() throws Exception{
        when(departmentService.getById(1L)).thenReturn(department);
        when(departmentService.getById(2L)).thenReturn(anotherDepartment);
        Group group1 = Group.builder()
                .name("P34141")
                .department(department)
                .build();
        Group group2 = Group.builder()
                .name("P34121")
                .department(anotherDepartment)
                .build();
        repository.save(group1);
        repository.save(group2);
        List<Group> groups = repository.getAll();
        List<Group> lastTwo = groups.stream().skip(Math.max(0, groups.size() - 2)).toList();
        assertAll(() -> {
            assertNotNull(lastTwo.get(0));
            assertEquals(group1.getName(), lastTwo.get(0).getName());
            assertEquals(group1.getDepartment(), lastTwo.get(0).getDepartment());
        });
        assertAll(() -> {
            assertNotNull(lastTwo.get(1));
            assertEquals(group2.getName(), lastTwo.get(1).getName());
            assertEquals(group2.getDepartment(), lastTwo.get(1).getDepartment());
        });
        repository.deleteById(lastTwo.get(0).getId());
        repository.deleteById(lastTwo.get(1).getId());
    }

    @Test
    public void deleteGroup() throws Exception{
        when(departmentService.getById(1L)).thenReturn(department);
        Group group1 = Group.builder()
                .name("P34141")
                .department(department)
                .build();
        repository.save(group1);
        Group result = repository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(result);
        repository.deleteById(result.getId());
        assertThrows(GroupDoesNotExistsException.class , () -> repository.getById(result.getId()));
    }

}

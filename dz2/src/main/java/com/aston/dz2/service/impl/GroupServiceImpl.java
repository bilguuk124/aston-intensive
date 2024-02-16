package com.aston.dz2.service.impl;

import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.dto.GroupDto;
import com.aston.dz2.exception.DisciplineNotFoundException;
import com.aston.dz2.exception.GroupDoesNotExistsException;
import com.aston.dz2.exception.StudentDoesNotExistsException;
import com.aston.dz2.exception.StudentNotInThisGroupException;
import com.aston.dz2.repository.CrudGroupRepository;
import com.aston.dz2.service.DepartmentService;
import com.aston.dz2.service.DisciplineService;
import com.aston.dz2.service.GroupService;
import com.aston.dz2.service.StudentService;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@Stateless
@Default
@NoArgsConstructor
public class GroupServiceImpl implements GroupService {

    @Inject
    @Named("groupRepository")
    private CrudGroupRepository<Group, Long> repository;
    @Inject
    private DepartmentService departmentService;
    @Setter
    @Inject
    private DisciplineService disciplineService;
    @Setter
    @Inject
    private StudentService studentService;
    @Inject
    private Logger logger;

    public GroupServiceImpl(CrudGroupRepository<Group, Long> repository, DepartmentService departmentService, DisciplineService disciplineService, StudentService studentService){
        this.repository = repository;
        this.departmentService = departmentService;
        this.disciplineService = disciplineService;
        this.studentService = studentService;
        logger = LoggerFactory.getLogger(GroupServiceImpl.class);
    }

    @Override
    public Group getById(Long id) throws Exception {
        if (!repository.existsById(id)) {
            throw new GroupDoesNotExistsException("Group with id " + id + " does not exists!");
        }
        return repository.getById(id);
    }

    @Override
    public List<Group> getAll() throws Exception {
        return repository.getAll();
    }

    @Override
    public void addGroup(GroupDto dto) throws Exception {
        Group group = Group.builder()
                .name(dto.getName())
                .department(departmentService.getById(dto.getDepartmentId()))
                .build();

        repository.save(group);
    }

    @Override
    public void updateGroup(Long id, GroupDto dto) throws Exception {
        if (!repository.existsById(id)){
            throw new GroupDoesNotExistsException("Group with id " + id + " does not exist!");
        }
        Group group = Group.builder()
                .id(id)
                .name(dto.getName())
                .department(departmentService.getById(dto.getDepartmentId()))
                .build();
        repository.save(group);
    }

    @Override
    public Set<Group> getByDiscipline(Long id) throws Exception {
        logger.info("Getting group by discipline with id " + id);
        if (!disciplineService.existsById(id)){
            logger.error("Discipline with id = {} does not exist", id);
            throw new DisciplineNotFoundException("Discipline with id=" + id + " does not exist!");
        }
        return repository.getByDiscipline(id);
    }

    @Override
    public void deleteGroup(Long groupId) throws Exception {
        if (!repository.existsById(groupId)){
            throw new GroupDoesNotExistsException("Group with id " + groupId + " does not exist!");
        }
        repository.deleteById(groupId);
    }

    @Override
    public void addStudentToGroup(Long groupId, Long studentId) throws Exception{
        if (!existsById(groupId)){
            throw new GroupDoesNotExistsException("Group with id " + groupId + " does not exist!");
        }
        if (!studentService.existsById(studentId)) {
            throw new StudentDoesNotExistsException("Student with id " + studentId + " does not exist!");
        }
        Group group = repository.getById(groupId);
        Student student = studentService.getStudentById(studentId);
        student.setGroup(group);
        studentService.updateStudent(student);
    }

    @Override
    public void removeStudentFromGroup(Long groupId, Long studentId) throws Exception{
        if (!existsById(groupId)){
            throw new GroupDoesNotExistsException("Group with id " + groupId + " does not exist!");
        }
        if (!repository.existsById(studentId)) {
            throw new StudentDoesNotExistsException("Student with id " + studentId + " does not exist!");
        }
        Group group = repository.getById(groupId);
        Student student = studentService.getStudentById(studentId);
        if (!student.getGroup().equals(group)) {
            throw new StudentNotInThisGroupException("Student with id " + studentId + " is not in group with id " + groupId);
        }
        student.setGroup(null);
        studentService.updateStudent(student);
    }

    private boolean existsById(Long id) throws Exception{
        return repository.existsById(id);
    }

}

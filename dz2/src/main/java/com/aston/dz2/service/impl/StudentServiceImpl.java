package com.aston.dz2.service.impl;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.dto.StudentDto;
import com.aston.dz2.entity.enums.Level;
import com.aston.dz2.exception.StudentDoesNotExistsException;
import com.aston.dz2.repository.CrudRepository;
import com.aston.dz2.service.DepartmentService;
import com.aston.dz2.service.StudentService;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;

import java.util.List;

@Stateless
@Default
@NoArgsConstructor
public class StudentServiceImpl implements StudentService {

    @Inject
    @Named("studentRepository")
    private CrudRepository<Student, Long> repository;

    @Inject
    private DepartmentService departmentService;

    public StudentServiceImpl(CrudRepository<Student, Long> repository, DepartmentService departmentService){
        this.repository = repository;
        this.departmentService = departmentService;
    }

    @Override
    public void addStudent(StudentDto dto) throws Exception {
        Student student = Student.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .department(departmentService.getById(dto.getDepartmentId()))
                .level(Level.getByStringValue(dto.getLevel()))
                .build();
        repository.save(student);
    }

    @Override
    public void updateStudent(Long id, StudentDto dto) throws Exception {
        if (!repository.existsById(id)) {
            throw new StudentDoesNotExistsException("Student with id = " + id + " does not exist!");
        }

        Student student = Student.builder()
                .id(id)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .department(departmentService.getById(dto.getDepartmentId()))
                .level(Level.getByStringValue(dto.getLevel()))
                .build();

        repository.save(student);
    }

    @Override
    public void updateStudent(Student student) throws Exception{
        if (!repository.existsById(student.getId())){
            throw new StudentDoesNotExistsException("Student with id = " + student.getId() + " does not exist!");
        }
        repository.save(student);
    }

    @Override
    public void deleteStudent(Long id) throws Exception {
        if (!repository.existsById(id)){
            throw new StudentDoesNotExistsException("Student with id = " + id + " does not exist!");
        }
        repository.deleteById(id);
    }

    @Override
    public List<Student> getAllStudents() throws Exception {
        return repository.getAll();
    }

    @Override
    public Student getStudentById(Long id) throws Exception {
        if (!repository.existsById(id)) {
            throw new StudentDoesNotExistsException("Student with id = " + id + " does not exist!");
        }
        return repository.getById(id);
    }

    @Override
    public boolean existsById(Long id) throws Exception {
        return repository.existsById(id);
    }
}

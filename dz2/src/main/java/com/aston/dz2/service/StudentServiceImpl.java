package com.aston.dz2.service;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDTO;
import com.aston.dz2.repository.StudentRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class StudentServiceImpl implements StudentService {

    @Inject
    private StudentRepository repository;

    @Override
    public void addStudent(StudentDTO dto) {

    }

    @Override
    public void updateStudent(Long id, StudentDTO dto) {

    }

    @Override
    public boolean deleteStudent(Long id) {
        return false;
    }

    @Override
    public List<Student> getAllStudent() {
        return null;
    }

    @Override
    public Student getStudentById(Long id) {
        return null;
    }
}

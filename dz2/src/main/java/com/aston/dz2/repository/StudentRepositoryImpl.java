package com.aston.dz2.repository;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDTO;
import jakarta.enterprise.inject.Default;

import java.util.List;

@Default
public class StudentRepositoryImpl implements StudentRepository{
    @Override
    public void save(Student student) {

    }

    @Override
    public void save(StudentDTO dto) {

    }

    @Override
    public List<Student> getAllStudents() {
        return null;
    }

    @Override
    public Student getStudentById(Long id) {
        return null;
    }

    @Override
    public boolean deleteStudent(Long id) {
        return false;
    }
}

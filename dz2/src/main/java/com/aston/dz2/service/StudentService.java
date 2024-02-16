package com.aston.dz2.service;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.dto.StudentDto;
import java.util.List;

public interface StudentService {

    void addStudent(StudentDto dto) throws Exception;

    void updateStudent(Long id, StudentDto dto) throws Exception;

    void updateStudent(Student student) throws Exception;

    void deleteStudent(Long id) throws Exception;

    List<Student> getAllStudents() throws Exception;

    Student getStudentById(Long id) throws Exception;

    boolean existsById(Long id) throws Exception;
}

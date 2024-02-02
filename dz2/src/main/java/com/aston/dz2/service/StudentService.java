package com.aston.dz2.service;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDTO;

import java.util.List;

public interface StudentService {

    /**
     * Create a new student
     * @param dto - student's info
     */
    void addStudent(StudentDTO dto);

    /**
     * Update the existing student
     * @param id of the student
     * @param dto new info of student
     */
    void updateStudent(Long id, StudentDTO dto);

    /**
     * Delete an existing student
     * @param id of the student
     * @return if operation was successful
     */
    boolean deleteStudent(Long id);

    /**
     * Get all students
     * @return all students
     */
    List<Student> getAllStudent();

    /**
     * Get a student by id
     * @param id of the student
     * @return found student
     */
    Student getStudentById(Long id);
}

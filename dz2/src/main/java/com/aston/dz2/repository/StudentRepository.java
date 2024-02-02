package com.aston.dz2.repository;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDTO;

import java.util.List;

public interface StudentRepository {

    /**
     * Save student to database
     * @param student to save
     */
    void save(Student student);

    /**
     * Update student
     * @param dto - new info
     */
    void save (StudentDTO dto);

    /**
     * Retrieve all students from database
     * @return all the students
     */
    List<Student> getAllStudents();

    /**
     * Retrieve one student by id
     * @param id of the student
     * @return student with id
     */
    Student getStudentById(Long id);

    /**
     * Delete student by id
     * @param id of the student to delete
     * @return if the operation was successful
     */
    boolean deleteStudent(Long id);
}

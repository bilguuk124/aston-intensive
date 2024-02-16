package com.aston.dz2.service;

import com.aston.dz2.entity.Teacher;
import com.aston.dz2.entity.dto.TeacherDto;
import java.util.List;

public interface TeacherService {

    Teacher getTeacherById(Long id) throws Exception;

    List<Teacher> getAllTeachers() throws Exception;

    void addTeacher(TeacherDto dto) throws Exception;

    void deleteTeacher(Long id) throws Exception;

    void updateTeacher(Long id, TeacherDto dto) throws Exception;
}

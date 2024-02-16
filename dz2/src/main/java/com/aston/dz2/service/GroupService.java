package com.aston.dz2.service;

import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.dto.GroupDto;

import java.util.List;
import java.util.Set;

public interface GroupService {

    Group getById(Long id) throws Exception;

    List<Group> getAll() throws Exception;

    void addGroup(GroupDto dto) throws Exception;

    void updateGroup(Long id, GroupDto dto) throws Exception;

    Set<Group> getByDiscipline(Long id) throws Exception;

    void deleteGroup(Long groupId) throws Exception;

    void addStudentToGroup(Long groupId, Long studentId) throws Exception;
    void removeStudentFromGroup(Long groupId, Long studentId) throws Exception;

    void setStudentService(StudentService studentService);
    void setDisciplineService(DisciplineService disciplineService);
}

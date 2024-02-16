package com.aston.dz2.service.impl;

import com.aston.dz2.entity.Teacher;
import com.aston.dz2.entity.dto.TeacherDto;
import com.aston.dz2.entity.enums.TeacherRank;
import com.aston.dz2.exception.TeacherNotFoundException;
import com.aston.dz2.exception.TeacherRankNotFoundException;
import com.aston.dz2.repository.CrudRepository;
import com.aston.dz2.service.DepartmentService;
import com.aston.dz2.service.TeacherService;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;

import java.util.List;

@Default
@Stateless
@NoArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    @Inject
    @Named("teacherRepository")
    private CrudRepository<Teacher, Long> repository;

    @Inject
    private DepartmentService departmentService;

    public TeacherServiceImpl(CrudRepository<Teacher, Long> repository, DepartmentService departmentService){
        this.repository = repository;
        this.departmentService = departmentService;
    }

    @Override
    public Teacher getTeacherById(Long id) throws Exception {
        if (!repository.existsById(id)){
            throw new TeacherNotFoundException("Teacher with id = " + id + " does not exist!");
        }
        return repository.getById(id);
    }

    @Override
    public List<Teacher> getAllTeachers() throws Exception {
        return repository.getAll();
    }

    @Override
    public void addTeacher(TeacherDto dto) throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .rank(TeacherRank.getByStringValue(dto.getRank()))
                .department(departmentService.getById(dto.getDepartmentId()))
                .build();
        repository.save(teacher);
    }


    @Override
    public void deleteTeacher(Long id) throws Exception {
        if (!repository.existsById(id)) {
            throw new TeacherNotFoundException("Teacher with id = " + id + " does not exist!");
        }
        repository.deleteById(id);
    }

    @Override
    public void updateTeacher(Long id, TeacherDto dto) throws Exception {
        if (!repository.existsById(id)){
            throw new TeacherRankNotFoundException("Teacher with id = " + id + " does not exist!");
        }

        Teacher teacher = Teacher.builder()
                .id(id)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .department(departmentService.getById(dto.getDepartmentId()))
                .rank(TeacherRank.getByStringValue(dto.getRank()))
                .build();

        repository.save(teacher);
    }


}

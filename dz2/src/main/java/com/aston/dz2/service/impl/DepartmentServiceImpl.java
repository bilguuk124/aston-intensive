package com.aston.dz2.service.impl;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.dto.DepartmentDto;
import com.aston.dz2.exception.DepartmentDoesNotExistException;
import com.aston.dz2.repository.CrudRepository;
import com.aston.dz2.service.DepartmentService;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;

import java.util.List;

@Default
@Stateless
@NoArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    @Inject
    @Named("departmentRepository")
    private CrudRepository<Department, Long> repository;

    public DepartmentServiceImpl(CrudRepository<Department, Long> repository){
        this.repository = repository;
    }

    @Override
    public Department getById(Long id) throws Exception {
        if (!repository.existsById(id)){
            throw new DepartmentDoesNotExistException("Department with id = " + id + " does not exists");
        }
        return repository.getById(id);
    }

    @Override
    public List<Department> getAll() throws Exception {
        return repository.getAll();
    }

    @Override
    public void addDepartment(DepartmentDto dto) throws Exception {
        Department department = Department.builder()
                .name(dto.getName())
                .shortName(dto.getShortName())
                .description(dto.getDescription())
                .build();
        repository.save(department);
    }

    @Override
    public void updateDepartment(Long id, DepartmentDto dto) throws Exception {
        if (!repository.existsById(id)){
            throw new DepartmentDoesNotExistException("Department with id = " + id + " does not exists");
        }
        Department department = Department.builder()
                .id(id)
                .name(dto.getName())
                .shortName(dto.getShortName())
                .description(dto.getDescription())
                .build();
        repository.save(department);
    }

    @Override
    public void deleteById(Long id) throws Exception {
        if (!repository.existsById(id)){
            throw new DepartmentDoesNotExistException("Department with id = " + id + " does not exists");
        }
        repository.deleteById(id);
    }
}

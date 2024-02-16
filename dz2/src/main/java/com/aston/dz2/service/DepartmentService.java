package com.aston.dz2.service;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {

    /**
     * Get department by id
     * @param id of the department
     * @return department if exists
     */
    Department getById(Long id) throws Exception;

    /**
     * Get all the departments
     * @return list of departments
     */
    List<Department> getAll() throws Exception;

    /**
     * Add a new department
     * @param dto - info of the department
     */
    void addDepartment(DepartmentDto dto) throws Exception;

    /**
     * Update an existing department
     * @param id of the department
     * @param dto - info to update
     */
    void updateDepartment(Long id, DepartmentDto dto) throws Exception;

    void deleteById(Long id) throws Exception;
}

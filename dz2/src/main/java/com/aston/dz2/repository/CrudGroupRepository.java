package com.aston.dz2.repository;

import com.aston.dz2.entity.Group;

import java.util.Set;

public interface CrudGroupRepository<C,T> extends CrudRepository<C,T>{
    Set<Group> getByDiscipline(Long disciplineId) throws Exception;
}

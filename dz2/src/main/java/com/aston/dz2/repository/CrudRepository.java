package com.aston.dz2.repository;


import java.util.List;

/**
 * General interface for crud operations
 * @param <C> Entity class
 * @param <T> Entity's id class
 */
public interface CrudRepository<C, T> {
    void save(C element) throws Exception;

    boolean exists(C element) throws Exception;

    boolean existsById(T id) throws Exception;

    C getById(T id) throws Exception;

    List<C> getAll() throws Exception;

    void deleteById(T id) throws Exception;
}

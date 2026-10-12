package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * Generic Repository interface demonstrating Abstraction and Generic Programming in Java.
 * Defines standard CRUD operations for all database entities.
 *
 * @param <T> The domain entity type
 */
public interface Repository<T> {
    /**
     * Retrieves an entity by its unique identifier.
     * @param id the entity identifier
     * @return an Optional containing the entity if found
     * @throws DatabaseException if a database access error occurs
     */
    Optional<T> findById(int id) throws DatabaseException;

    /**
     * Retrieves all entities of type T.
     * @return list of entities
     * @throws DatabaseException if a database access error occurs
     */
    List<T> findAll() throws DatabaseException;

    /**
     * Persists a new entity into the database.
     * @param entity the entity to save
     * @return the persisted entity with generated primary key
     * @throws DatabaseException if a database access error occurs
     */
    T save(T entity) throws DatabaseException;

    /**
     * Updates an existing entity in the database.
     * @param entity the entity with updated values
     * @return true if update succeeded
     * @throws DatabaseException if a database access error occurs
     */
    boolean update(T entity) throws DatabaseException;

    /**
     * Deletes an entity by its identifier.
     * @param id the entity identifier
     * @return true if deletion succeeded
     * @throws DatabaseException if a database access error occurs
     */
    boolean deleteById(int id) throws DatabaseException;
}

package com.vhre.base.core.base.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Base service interface defining generic CRUD and pagination operations.
 *
 * @param <Dto> The Data Transfer Object type
 * @param <ID>  The primary key type
 */
public interface BaseService<Dto, ID> {

    /**
     * Retrieves all active (non-deleted) records.
     */
    List<Dto> findAll();

    /**
     * Retrieves a paginated list of active records.
     *
     * @param pageable Pagination and sorting criteria
     * @return A page containing the DTOs
     */
    Page<Dto> findAll(Pageable pageable);

    /**
     * Retrieves a single record by its identifier.
     *
     * @param id The unique identifier
     * @return The found DTO
     */
    Dto findById(ID id);

    /**
     * Saves a new record.
     */
    Dto save(Dto dto);

    /**
     * Updates an existing record.
     */
    Dto update(ID id, Dto dto);

    /**
     * Performs a soft delete on a record by its identifier.
     */
    void delete(ID id);
}
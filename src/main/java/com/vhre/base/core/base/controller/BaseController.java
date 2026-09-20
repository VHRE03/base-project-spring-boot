package com.vhre.base.core.base.controller;

import com.vhre.base.core.base.service.BaseService;
import com.vhre.base.core.exceptions.EndpointDisabledException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Unified Base Controller providing CRUD operations and pagination.
 * Allows derived controllers to selectively expose endpoints via constructor configuration.
 */
public abstract class BaseController<Entity, Dto, ID> {

    protected final BaseService<Dto, ID> service;
    private final Set<ApiMethod> allowedMethods;

    /**
     * Enum defining the available HTTP operations in the controller.
     */
    public enum ApiMethod {
        READ_ALL, READ_ONE, CREATE, UPDATE, DELETE
    }

    /**
     * Default constructor.
     * Exposes ALL endpoints by default.
     *
     * @param service The underlying base service
     */
    protected BaseController(BaseService<Dto, ID> service) {
        this.service = service;
        this.allowedMethods = EnumSet.allOf(ApiMethod.class);
    }

    /**
     * Parameterized constructor.
     * Exposes ONLY the endpoints explicitly provided in the arguments.
     *
     * @param service        The underlying base service
     * @param allowedMethods Varargs array of methods to enable (e.g., ApiMethod.READ_ALL, ApiMethod.CREATE)
     */
    protected BaseController(BaseService<Dto, ID> service, ApiMethod... allowedMethods) {
        this.service = service;
        this.allowedMethods = allowedMethods.length > 0
                ? EnumSet.copyOf(Arrays.asList(allowedMethods))
                : EnumSet.noneOf(ApiMethod.class);
    }

    /**
     * Helper method to verify if the requested operation is enabled.
     * Throws an exception if the method is disabled, returning a 405 error.
     */
    private void checkMethodAllowed(ApiMethod method) {
        if (!allowedMethods.contains(method)) {
            throw new EndpointDisabledException("This endpoint is disabled for this resource.");
        }
    }

    @Operation(summary = "Get a paginated list of records")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public ResponseEntity<Page<Dto>> getAll(@ParameterObject Pageable pageable) {
        checkMethodAllowed(ApiMethod.READ_ALL);
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Get a record by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record found successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found"),
            @ApiResponse(responseCode = "405", description = "Method not allowed")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Dto> getById(@PathVariable ID id) {
        checkMethodAllowed(ApiMethod.READ_ONE);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Create a new record")
    @ApiResponse(responseCode = "201", description = "Record created successfully")
    @PostMapping
    public ResponseEntity<Dto> create(@Valid @RequestBody Dto dto) {
        checkMethodAllowed(ApiMethod.CREATE);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @Operation(summary = "Update an existing record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record updated successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found for update")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Dto> update(@PathVariable ID id, @Valid @RequestBody Dto dto) {
        checkMethodAllowed(ApiMethod.UPDATE);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Delete a record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found for deletion")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        checkMethodAllowed(ApiMethod.DELETE);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
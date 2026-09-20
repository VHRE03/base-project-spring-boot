package com.vhre.base.core.base.service;

import com.vhre.base.core.base.entity.BaseEntity;
import com.vhre.base.core.base.mapper.BaseMapper;
import com.vhre.base.core.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Abstract implementation of the BaseService containing reusable CRUD logic.
 * Enforces transaction management and soft-delete mechanisms.
 */
@RequiredArgsConstructor
public abstract class BaseServiceImpl<Entity extends BaseEntity, Dto, ID> implements BaseService<Dto, ID> {

    protected final JpaRepository<Entity, ID> repository;
    protected final BaseMapper<Entity, Dto> mapper;

    @Override
    @Transactional(readOnly = true)
    public List<Dto> findAll() {
        return mapper.toDtos(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Dto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Dto findById(ID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
    }

    @Override
    @Transactional
    public Dto save(Dto dto) {
        Entity entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public Dto update(ID id, Dto dto) {
        Entity existingEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for update with ID: " + id));
        mapper.updateEntityFromDto(dto, existingEntity);
        return mapper.toDto(repository.save(existingEntity));
    }

    @Override
    @Transactional
    public void delete(ID id) {
        Entity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for deletion with ID: " + id));

        entity.setDeleted(true);
        repository.save(entity);
    }
}
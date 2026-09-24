package com.vhre.base.core.base.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Base mapper interface defining standard conversion methods between Entities and DTOs.
 * Best used in conjunction with MapStruct or similar mapping libraries.
 *
 * @param <Entity> The JPA Entity type
 * @param <Dto>    The Data Transfer Object type
 */
public interface BaseMapper<Entity, Dto> {
    /**
     * Converts a single Entity into its corresponding DTO.
     *
     * @param entity The source entity object
     * @return The mapped DTO, or null if the input is null
     */
    Dto toDto(Entity entity);

    /**
     * Converts a single DTO into its corresponding Entity.
     * Useful for creating new records.
     *
     * @param dto The source DTO object
     * @return The mapped Entity, or null if the input is null
     */
    Entity toEntity(Dto dto);

    /**
     * Converts a list of Entities into a list of DTOs.
     *
     * @param entities The source list of entity objects
     * @return A list of mapped DTOs
     */
    List<Dto> toDtos(List<Entity> entities);

    /**
     * Converts a list of DTOs into a list of Entities.
     *
     * @param dtos The source list of DTO objects
     * @return A list of mapped Entities
     */
    List<Entity> toEntities(List<Dto> dtos);

    /**
     * Updates an existing Entity instance with data from a DTO.
     * Highly recommended for update (PUT/PATCH) operations to preserve the JPA entity state
     * and avoid creating detached instances.
     *
     * <p><strong>Contract:</strong> the target {@code Entity} handed to this method is a
     * <em>managed</em> JPA instance loaded by {@code BaseServiceImpl#update}. The mapper
     * implementation MUST NOT overwrite the fields owned by the persistence layer:</p>
     * <ul>
     *   <li>{@code id}: altering the identifier of a managed instance makes Hibernate throw
     *       {@code HibernateException: "Identifier of an instance ... was altered from X to null"}
     *       at flush/commit time. The DTO id is expected to be {@code null} (it is validated
     *       with {@code @Null} in {@code BaseDTO}), so mapping it would always corrupt the entity.</li>
     *   <li>{@code createdAt} / {@code updatedAt}: maintained by Spring Data auditing
     *       ({@code @CreatedDate} / {@code @LastModifiedDate}).</li>
     *   <li>{@code deleted}: the soft-delete flag is only toggled by
     *       {@code BaseServiceImpl#delete}, never by payload data.</li>
     * </ul>
     *
     * <p>The {@code @Mapping(..., ignore = true)} declarations below make MapStruct honor this
     * contract in the implementations it generates for concrete mappers extending this interface.
     * If you use a different mapping library or hand-write the implementation, you are still
     * responsible for not copying these fields from the DTO into the target entity.</p>
     *
     * Note: The @MappingTarget annotation is specific to MapStruct. If you are using a
     * different mapping library or doing manual mapping, you can remove this annotation.
     *
     * @param dto    The source DTO containing the updated data
     * @param entity The target Entity instance to be updated
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(Dto dto, @MappingTarget Entity entity);
}

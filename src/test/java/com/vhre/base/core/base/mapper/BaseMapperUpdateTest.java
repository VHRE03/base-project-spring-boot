package com.vhre.base.core.base.mapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Regression test for the update flow driven by {@code BaseServiceImpl#update(ID, Dto)}.
 * <p>
 * The DTO handed to {@code updateEntityFromDto} is the request payload. Due to the
 * {@code @Null} constraint on {@code BaseDTO#id}, its identifier is always {@code null}.
 * The MapStruct-generated implementation must therefore never copy {@code id},
 * {@code createdAt}, {@code updatedAt} or {@code deleted} from the DTO into the
 * <em>managed</em> target entity.
 * <p>
 * Overwriting the identifier of a managed instance previously made Hibernate fail at
 * flush/commit time with:
 * {@code HibernateException: Identifier of an instance of '...Entity' was altered from X to null}.
 */
class BaseMapperUpdateTest {

    private final SampleMapper mapper = new SampleMapperImpl();

    @Test
    void updateDoesNotAlterIdentifierOfManagedEntity() {
        SampleEntity entity = managedEntity("before");

        // This is what PUT /{id} actually receives: a payload without id (validated @Null in BaseDTO)
        SampleDTO dto = new SampleDTO();
        dto.setName("after");

        mapper.updateEntityFromDto(dto, entity);

        assertNotNull(entity.getId(), "The managed entity must keep its identifier");
        assertEquals("after", entity.getName(), "Business fields must still be mapped");
    }

    @Test
    void updateIgnoresIdEvenIfThePayloadCarriesOne() {
        SampleEntity entity = managedEntity("before");
        UUID originalId = entity.getId();

        SampleDTO dto = new SampleDTO();
        dto.setId(UUID.randomUUID()); // defensive: path variable wins over any body id
        dto.setName("after");

        mapper.updateEntityFromDto(dto, entity);

        assertEquals(originalId, entity.getId(),
                "The id from the request path must always win over any id in the payload");
        assertEquals("after", entity.getName());
    }

    @Test
    void updateDoesNotOverwriteAuditingAndSoftDeleteFields() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(3);

        SampleEntity entity = managedEntity("before");
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        SampleDTO dto = new SampleDTO();
        dto.setName("after");
        dto.setCreatedAt(LocalDateTime.now().minusYears(10));
        dto.setUpdatedAt(null);
        dto.setDeleted(true); // a soft-deleted entity must never be resurrected by a PUT

        mapper.updateEntityFromDto(dto, entity);

        assertEquals(createdAt, entity.getCreatedAt(), "@CreatedDate is owned by the auditing layer");
        assertEquals(updatedAt, entity.getUpdatedAt(), "@LastModifiedDate is owned by the auditing layer");
        assertFalse(entity.isDeleted(), "The soft-delete flag is only managed by BaseServiceImpl#delete");
        assertEquals("after", entity.getName());
    }

    private SampleEntity managedEntity(String name) {
        SampleEntity entity = new SampleEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        return entity;
    }
}

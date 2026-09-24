package com.vhre.base.core.base.mapper;

import com.vhre.base.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Minimal concrete entity used to verify the MapStruct-generated contract of {@link BaseMapper}.
 * Mirrors how consuming projects extend {@code BaseEntity}.
 */
@Getter
@Setter
class SampleEntity extends BaseEntity {

    private String name;
}

package com.vhre.base.core.base.mapper;

import org.mapstruct.Mapper;

/**
 * Concrete MapStruct mapper mirroring the standard usage pattern of consuming projects:
 * an empty {@code @Mapper} interface extending {@link BaseMapper}.
 */
@Mapper
interface SampleMapper extends BaseMapper<SampleEntity, SampleDTO> {
}

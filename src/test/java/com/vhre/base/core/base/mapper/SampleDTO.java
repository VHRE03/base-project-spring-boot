package com.vhre.base.core.base.mapper;

import com.vhre.base.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Minimal concrete DTO used to verify the MapStruct-generated contract of {@link BaseMapper}.
 * Mirrors how consuming projects extend {@code BaseDTO}.
 */
@Getter
@Setter
class SampleDTO extends BaseDTO {

    private String name;
}

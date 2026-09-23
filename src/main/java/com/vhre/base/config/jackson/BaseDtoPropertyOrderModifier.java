package com.vhre.base.config.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.vhre.base.core.base.dto.BaseDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class BaseDtoPropertyOrderModifier extends BeanSerializerModifier {
    private static final List<String> AUDIT_FIELDS = List.of("createdAt", "updatedAt", "deleted");

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        if (!BaseDTO.class.isAssignableFrom(beanDesc.getBeanClass())) {
            return beanProperties;
        }

        List<BeanPropertyWriter> sorted = new ArrayList<>(beanProperties);
        sorted.sort(Comparator
                .comparingInt((BeanPropertyWriter p) -> {
                    String name = p.getName();
                    if ("id".equals(name)) return 0; // Primero el ID
                    if (AUDIT_FIELDS.contains(name)) return 2; // Al final auditoría
                    return 1; // En medio los campos propios del DTO
                })
                .thenComparing(beanProperties::indexOf)
        );
        return sorted;
    }
}

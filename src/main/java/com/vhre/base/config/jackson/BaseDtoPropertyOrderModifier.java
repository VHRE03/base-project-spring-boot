package com.vhre.base.config.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.vhre.base.core.base.dto.BaseDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDtoPropertyOrderModifier extends BeanSerializerModifier {

    private static final List<String> AUDIT_FIELDS = List.of("createdAt", "updatedAt", "deleted");

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        if (!BaseDTO.class.isAssignableFrom(beanDesc.getBeanClass())) {
            return beanProperties;
        }

        Map<String, Integer> originalOrder = new HashMap<>();
        for (int i = 0; i < beanProperties.size(); i++) {
            originalOrder.put(beanProperties.get(i).getName(), i);
        }

        List<BeanPropertyWriter> sorted = new ArrayList<>(beanProperties);
        sorted.sort(Comparator
                .comparingInt((BeanPropertyWriter p) -> {
                    String name = p.getName();
                    if ("id".equals(name)) return 0;
                    if (AUDIT_FIELDS.contains(name)) return 2;
                    return 1;
                })
                .thenComparingInt(p -> originalOrder.getOrDefault(p.getName(), Integer.MAX_VALUE))
        );
        return sorted;
    }
}

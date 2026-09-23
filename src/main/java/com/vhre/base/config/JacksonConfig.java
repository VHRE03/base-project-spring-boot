package com.vhre.base.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vhre.base.config.jackson.BaseDtoPropertyOrderModifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    @ConditionalOnMissingBean(name = "baseDtoOrderModule")
    public Module baseDtoOrderModule() {
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new BaseDtoPropertyOrderModifier());
        return module;
    }
}

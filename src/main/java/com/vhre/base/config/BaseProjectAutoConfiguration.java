package com.vhre.base.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(JacksonConfig.class)
public class BaseProjectAutoConfiguration {
}